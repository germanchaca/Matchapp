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
public abstract class PutPhotoProfileOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "PutPhotoProfileOkHttp" ;
    private final OkHttpClient client;
    private final User myUser;
    private final String base64EncodedPhoto;

    protected abstract void logout();
    protected abstract void onSuccess();
    protected abstract void onConnectionError();


    public PutPhotoProfileOkHttp(User myUser, String base64EncodedPhoto){
        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();
        this.myUser = myUser;
        this.base64EncodedPhoto = base64EncodedPhoto;
    }

    public void makeRequest(){

        String url = RestAPIContract.PUT_PHOTO_USER(myUser.getEmail());

        JSONObject paramsJson = new JSONObject();
        try {

            paramsJson.put("photo",base64EncodedPhoto);

            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = paramsJson.toString();

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected error ");
                onConnectionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, "Success, response: " + responseStr + "code: " + response.code());
                    onSuccess();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.e(TAG, "Unexpected code " + response.code());
                    if (response.code() == 401){
                        Log.e(TAG, "Error 401");
                        onErrorNoAuth();
                    }else {
                        onConnectionError();
                    }
                }
            }
        };

        try {
            put(url,json,callBack);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void onErrorNoAuth() {
        PostRefreshTokenOkHttp request = new PostRefreshTokenOkHttp() {
            @Override
            protected void onErrorNoAuth() {
                logout();
            }

            @Override
            protected void onRefreshAppServerTokenConnectionError() {
                onConnectionError();
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
