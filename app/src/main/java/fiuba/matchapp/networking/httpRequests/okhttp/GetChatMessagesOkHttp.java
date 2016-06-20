package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.ChatRoom;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import fiuba.matchapp.networking.jsonUtils.JsonParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ger on 19/06/16.
 */
public abstract class GetChatMessagesOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "GetChatMessagesOkHttp" ;
    private final OkHttpClient client;
    private final String idChat;
    private final String idMessage;


    protected abstract void logout();

    protected abstract void onConnectionError();

    protected abstract void onSuccess(List<Message> messages);

    public GetChatMessagesOkHttp(String idChat, String idMessage){
        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();
        this.idChat = idChat;
        this.idMessage = idMessage;
    }

    public void makeRequest(){


        String url = RestAPIContract.GET_CHAT_HISTORY(idChat,idMessage);

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onConnectionError();
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
                    Log.e(TAG, "Error code " + response.code());
                    if (response.code() == 401){
                        Log.e(TAG, "Error 401");
                        onErrorNoAuthRequest();
                    }else {
                        onConnectionError();
                    }
                }
            }
        };

        try {
            get(url,callBack);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void onErrorNoAuthRequest() {
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
        request.makeRequest();
    }



    Call get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }

    private void onSuccessResponse(String response) throws JSONException {

        JSONObject jsonResponse = new JSONObject(response);

        List<Message> messages = JsonParser.getMessagesFromJSONResponse(jsonResponse);

        onSuccess(messages);
    }
}
