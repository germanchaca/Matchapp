package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;
import fiuba.matchapp.networking.jsonUtils.JsonParser;
import fiuba.matchapp.utils.InterestsUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ger on 19/06/16.
 */
public abstract class PostSignInOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "PostSignInOkHttp" ;
    private final OkHttpClient client;
    private final String password;
    private final Object email;

    protected abstract void onSignInFailedUserConnectionError();
    protected abstract void onSignInFailedUserNotCorrect();
    protected abstract void onSignInSuccess();

    public PostSignInOkHttp(String email, String hashedPassword){
        client = MyApplication.getInstance().getAppServerClient();
        this.email = email;
        this.password = hashedPassword;
    }

    public Call makeRequest(){
        JSONObject paramsJson = new JSONObject();
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("email", this.email);

            userJson.put("password", this.password);

            userJson.put("gcm_registration_id",FirebaseInstanceId.getInstance().getToken());

            paramsJson.put("user",userJson);


            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = RestAPIContract.POST_APPSERVER_TOKEN;
        String json = paramsJson.toString();

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onSignInFailedUserConnectionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, "Success, response: " + responseStr + "code: " + response.code());
                    onSuccess(responseStr);
                } else {
                    // Request not successful
                    Log.e(TAG, "Unexpected code " + response.code());
                    if (response.code() == 401){
                        Log.e(TAG, "Error 401");
                        onSignInFailedUserNotCorrect();
                    }else {
                        onSignInFailedUserConnectionError();
                    }
                }
            }
        };

        try {
            return post(url,json,callBack);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void onSuccess(String responseStr) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(responseStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        User loggedUser = JsonParser.getUserFromJSONresponse(obj);
        String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);

        MyApplication.getInstance().getPrefManager().storeUser(loggedUser);
        MyApplication.getInstance().getPrefManager().storeUserPass(this.password);
        MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

        GetInterestsOkHttp interestsRequest = new GetInterestsOkHttp() {
            @Override
            protected void onAppServerConnectionError() {

            }

            @Override
            protected void onGetInterestsSuccess(List<Interest> interests) {
                MyApplication.getInstance().getPrefManager().storeInterests(InterestsUtils.getStringListMap(interests));
                MyApplication.getInstance().getPrefManager().setFirstTimeVisited();

                onSignInSuccess();
            }

            @Override
            protected void logout() {

            }
        };
        interestsRequest.makeRequest();


    }


    Call post(String url, String json, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }

}
