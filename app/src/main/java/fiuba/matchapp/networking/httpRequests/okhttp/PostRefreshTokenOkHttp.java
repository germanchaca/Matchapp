package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;
import fiuba.matchapp.networking.jsonUtils.JsonParser;
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
public abstract class PostRefreshTokenOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "PostRefreshTokenOkHttp" ;
    private final OkHttpClient client;
    private final User myUser;
    private final String myPassword;

    protected abstract void onRefreshAppServerTokenSuccess();
    protected abstract void onRefreshAppServerTokenConnectionError();
    protected abstract void onErrorNoAuth();

    public PostRefreshTokenOkHttp(){
        client = new OkHttpClient();
        this.myUser = MyApplication.getInstance().getPrefManager().getUser();
        this.myPassword = MyApplication.getInstance().getPrefManager().getUserCredentials();
    }

    public Call makeRefreshRequest(){
        JSONObject paramsJson = new JSONObject();
        JSONObject userJson = new JSONObject();
        try {

            userJson.put("email", myUser.getEmail());

            userJson.put("password", myPassword);

            paramsJson.put("user",userJson);

            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = RestAPIContract.POST_SIGN_IN;
        String json = paramsJson.toString();

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onRefreshAppServerTokenConnectionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, "Success, response: " + responseStr + "code: " + response.code());
                    try {
                        onSuccessResponse(responseStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.e(TAG, "Unexpected code " + response.code());
                    if (response.code() == 401){
                        Log.e(TAG, "Error 401");
                        onErrorNoAuth();
                    }else {
                        onRefreshAppServerTokenConnectionError();
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


    Call post(String url, String json, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken())
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }

    private void onSuccessResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);
        MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

        onRefreshAppServerTokenSuccess();
    }



}
