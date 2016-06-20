package fiuba.matchapp.networking.httpRequests.okhttp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
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
public abstract class PutInterestsOkHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "PutInterestsOkHttp" ;
    private final OkHttpClient client;
    private final User myUser;
    private final List<UserInterest> interestsToChange;

    protected abstract void onAppServerConnectionError();
    protected abstract void onUpdateDataSuccess();
    protected abstract void logout();

    public PutInterestsOkHttp(User myUser,List<UserInterest> interests){
        client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .build();
        this.myUser = myUser;
        this.interestsToChange = interests;
    }

    public void makeRequest(){
        JSONObject paramsJson = new JSONObject();
        JSONObject userJson = new JSONObject();
        try {

            JSONArray jsonArray = new JSONArray();
            for(UserInterest i:interestsToChange){
                if(i != null){
                    JSONObject intObj = new JSONObject();
                    intObj.put("value", i.getDescription());
                    intObj.put("category",i.getCategory());
                    jsonArray.put(intObj);
                }
            }

            userJson.put("interests", jsonArray);
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
            put(url,json,callBack);

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
                onAppServerConnectionError();
            }

            @Override
            protected void onRefreshAppServerTokenSuccess() {
                makeRequest();
            }
        };
        request.makeRequest();
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
