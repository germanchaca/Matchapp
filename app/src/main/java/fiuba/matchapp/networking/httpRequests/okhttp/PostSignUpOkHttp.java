package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;
import fiuba.matchapp.networking.jsonUtils.JsonParser;
import fiuba.matchapp.networking.jsonUtils.JsonUtils;
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
public abstract class PostSignUpOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "PostSignUpOkHttp" ;
    private final OkHttpClient client;
    private final User user;
    private final String password;

    protected abstract void onSignupSuccess(List<Interest> mapInterestsByCategory);
    protected abstract void onSignUpFailedUserInvalidError();
    protected abstract void onSignUpFailedUserConnectionError();
    protected abstract void onLogOutError();

    public PostSignUpOkHttp(User user, String password){
        client = new OkHttpClient();
        this.user = user;
        this.password = password;
    }

    public void makeRequest(){
        JSONObject paramsJson = new JSONObject();
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("name", user.getName());
            userJson.put("alias", user.getAlias());
            userJson.put("email", user.getEmail());
            userJson.put("sex", user.getGenre());
            userJson.put("age", user.getAge());


            JSONArray jsonArray = new JSONArray();
            for(UserInterest i:user.getInterests()){
                if(i != null){
                    JSONObject intObj = new JSONObject();
                    intObj.put("value", i.getDescription());
                    intObj.put("category",i.getCategory());
                    jsonArray.put(intObj);
                }
            }

            userJson.put("interests", jsonArray);

            userJson.put("photo_profile", user.getPhotoProfile());

            userJson.put("password", password);

            JSONObject locationJson = JsonUtils.getJsonObjectFromLocation(user.getLatitude(), user.getLongitude());
            userJson.put("location", locationJson);
            userJson.put("gcm_registration_id", FirebaseInstanceId.getInstance().getToken());

            paramsJson.put("user",userJson);

            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = RestAPIContract.POST_USER;
        String json = paramsJson.toString();

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onSignUpFailedUserConnectionError();
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
                    if (response.code() == 400){
                        Log.e(TAG, "Error 400");

                        onSignUpFailedUserInvalidError();
                    }else {
                        onSignUpFailedUserConnectionError();
                    }
                }
            }
        };

        try {
            post(url,json,callBack);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void onSuccess(String responseStr) {
        try {
            JSONObject obj = new JSONObject(responseStr);
            User loggedUser = JsonParser.getUserFromJSONresponse(obj);
            String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);

            MyApplication.getInstance().getPrefManager().storeUser(loggedUser);
            MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);
            MyApplication.getInstance().getPrefManager().storeUserPass(password);

            getAllInterestFromAppServer();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private void getAllInterestFromAppServer() {

        GetInterestsOkHttp request = new GetInterestsOkHttp() {
            @Override
            protected void onAppServerConnectionError() {
                onSignUpFailedUserConnectionError();
            }

            @Override
            protected void onGetInterestsSuccess(List<Interest> interests) {
                onSignupSuccess(interests);
            }

            @Override
            protected void logout() {
                onLogOutError();
            }
        };
        request.makeRequest();

    }


}
