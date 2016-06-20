package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;
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
public abstract class PutUserDataOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "PutUserDataOkHttp" ;
    private final OkHttpClient client;
    private final User myUser;
    private JSONObject userJson;
    private JSONObject paramsJson;

    protected abstract void onAppServerConnectionError();
    protected abstract void onUpdateDataSuccess();
    protected abstract void logout();

    public PutUserDataOkHttp(User myUser){
        client = MyApplication.getInstance().getAppServerClient();
        this.myUser = myUser;
        paramsJson = new JSONObject();
        userJson = new JSONObject();
    }
    public void changeName(String name){
        fillBody("name", name);
    }
    public void changeAlias(String alias){
        fillBody("name", alias);
    }
    public void changeAge(int age){
        fillBody("age", age);
    }
    public void changeLocation(double latitude,double longitude ){
        fillBody( latitude,longitude);
    }
    public void changeGcmRegistrationId(String gcmId){
        fillBody("gcm_registration_id", gcmId);
    }
    private void fillBody(String key, int changeValue) {
        try {
            userJson.put(key, changeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fillBody( double latitude,double longitude) {
        try {
            JSONObject locationJson = JsonUtils.getJsonObjectFromLocation(latitude, longitude);
            userJson.put("location", locationJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fillBody(String key, String changeValue) {
        try {
            userJson.put(key, changeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Call makeRequest(){
        try {
            paramsJson.put("user",userJson);
            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = RestAPIContract.PUT_USER(myUser.getEmail());
        String json = paramsJson.toString();

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onAppServerConnectionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, "Success, response: " + responseStr + "code: " + response.code());
                    onUpdateDataSuccess();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.e(TAG, "Unexpected code " + response.code());
                    if (response.code() == 401){
                        Log.e(TAG, "Error 401");
                        onErrorNoAuthRequest();
                    }else {
                        onAppServerConnectionError();
                    }
                }
            }
        };

        try {
            return put(url,json,callBack);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void onErrorNoAuthRequest() {
        PostRefreshTokenOkHttp request = new PostRefreshTokenOkHttp() {
            @Override
            protected void onErrorNoAuth() {
                logout();
            }

            @Override
            protected void onRefreshAppServerTokenConnectionError() {
                onAppServerConnectionError();
            }

            @Override
            protected void onRefreshAppServerTokenSuccess() {
                makeRequest();
            }
        };
        request.makeRefreshRequest();
    }



    Call put(String url, String json, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken())
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }


}
