package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import java.io.IOException;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
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
public abstract class DeleteUserAccountOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "DeleteUserAccountOkHttp" ;
    private final OkHttpClient client;
    private final User myUser;


    protected abstract void onDeleteSuccess();
    protected abstract void onDeleteConnectionError();

    protected abstract void logout();

    public DeleteUserAccountOkHttp(User user){
        client = new OkHttpClient();
        this.myUser = user;
    }

    public void makeRequest(){


        String url = RestAPIContract.DELETE_USER(this.myUser.getEmail());

        Callback callBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Unexpected code ");
                onDeleteConnectionError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, "Success, response: " + responseStr + "code: " + response.code());
                    onDeleteSuccess();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                    Log.e(TAG, "Error code " + response.code());
                    if (response.code() == 401){
                        Log.e(TAG, "Error 401");
                        onErrorNoAuthRequest();
                    }else {
                        onDeleteConnectionError();
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
                onDeleteConnectionError();
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
                .delete()
                .url(url)
                .addHeader("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }
}
