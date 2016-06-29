package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.MyMessage;
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
public abstract class PostNewMessageOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "PostNewMessageOkHttp" ;
    private final OkHttpClient client;
    private final String userId;
    private final MyMessage msg;

    protected abstract void onPostChatNewMessageRequestConnectionError(int positionInAdapter);
    protected abstract void onPostChatNewMessageRequestSuccess(int positionInAdapter);
    protected abstract void logout();

    public PostNewMessageOkHttp(String userId, MyMessage msg){
        //client = MyApplication.getInstance().getAppServerClient();
        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();
        this.userId = userId;
        this.msg = msg;
    }

    public Call makeRequest(){
        JSONObject paramsJson = new JSONObject();
        try {

            paramsJson.put("To", this.userId);
            paramsJson.put("message", this.msg.getMessage());

            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = RestAPIContract.POST_CHAT;
        String json = paramsJson.toString();

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onPostChatNewMessageRequestConnectionError(msg.getPositionInAdapter());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, "Success, response: " + responseStr + "code: " + response.code());
                    onPostChatNewMessageRequestSuccess(msg.getPositionInAdapter());
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.e(TAG, "Unexpected code " + response.code());
                    if (response.code() == 401){
                        Log.e(TAG, "Error 401");
                        onErrorNoAuthRequest();
                    }else {
                        onPostChatNewMessageRequestConnectionError(msg.getPositionInAdapter());
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

    private void onErrorNoAuthRequest() {
        PostRefreshTokenOkHttp request = new PostRefreshTokenOkHttp() {
            @Override
            protected void onErrorNoAuth() {
                logout();
            }

            @Override
            protected void onRefreshAppServerTokenConnectionError() {
                onPostChatNewMessageRequestConnectionError(msg.getPositionInAdapter());
            }

            @Override
            protected void onRefreshAppServerTokenSuccess() {
                makeRequest();
            }
        };
        request.makeRefreshRequest();
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


}
