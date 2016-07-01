package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import java.io.IOException;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ger on 19/06/16.
 */
public abstract class DeleteSingOutOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "DeleteSingOutOkHttp" ;
    private final OkHttpClient client;

    protected abstract void onConnectionError();
    protected abstract void onSuccess();
    protected abstract void logout();

    public DeleteSingOutOkHttp(){
        client = new OkHttpClient();
    }

    public void makeRequest(){

        String url = RestAPIContract.DELETE_SIGN_OUT;

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onConnectionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseStr = response.body().string();
                        Log.d(TAG, "Success, response: " + responseStr + "code: " + response.code());
                        onSuccess();
                    } catch (IOException e) {
                        Log.d(TAG, "Error ");
                        onSuccess();
                    }
                } else {
                    // Request not successful
                    Log.e(TAG, "Unexpected code " + response.code());
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
            delete(url,callBack);

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
        request.makeRefreshRequest();
    }



    Call delete(String url,  Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }


}
