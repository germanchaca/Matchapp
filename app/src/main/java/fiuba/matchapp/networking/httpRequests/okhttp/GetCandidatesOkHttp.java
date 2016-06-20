package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.User;
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
public abstract class GetCandidatesOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "GetCandidatesOkHttp" ;
    private final OkHttpClient client;

    protected abstract void onSuccess(List<User> candidates);
    protected abstract void onConnectionError();
    protected abstract void onLimitDayReached();
    protected abstract void logout();

    public GetCandidatesOkHttp(){
        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();

    }

    public void makeRequest(){


        String url = RestAPIContract.GET_MATCH;

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
                    }else if(response.code() == 402) {
                        onLimitDayReached();
                    }else{
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

        JSONObject objJSon = new JSONObject(response);
        JSONObject metadata = objJSon.getJSONObject("metadata");
        int count = metadata.getInt("count");
        List<User> users = new ArrayList<>();
        if( count > 0){
            JSONArray obj = objJSon.getJSONArray("users");
            users = JsonParser.getUsersFromJSONresponse(obj);
        }
        onSuccess(users);
    }
}
