package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.Interest;
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
public abstract class GetInterestsOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "GetInterestsOkHttp" ;
    private final OkHttpClient client;


    protected abstract void onAppServerConnectionError();
    protected abstract void onGetInterestsSuccess(List<Interest> interests);
    protected abstract void logout();

    public GetInterestsOkHttp(){
        client = MyApplication.getInstance().getAppServerClient();
    }

    public Call makeRequest(){


        String url = RestAPIContract.GET_INTERESTS;

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
                        onAppServerConnectionError();
                    }
                }
            }
        };

        try {
            return get(url,callBack);

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
        JSONObject obj = new JSONObject(response);
        List<Interest> interests = JsonParser.getInterestsFromJSONresponse(obj);

        onGetInterestsSuccess(interests);
    }
}
