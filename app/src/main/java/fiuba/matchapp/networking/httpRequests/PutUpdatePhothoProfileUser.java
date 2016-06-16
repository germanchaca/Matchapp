package fiuba.matchapp.networking.httpRequests;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;

/**
 * Created by ger on 01/06/16.
 */
public abstract class PutUpdatePhothoProfileUser {
    private static final String TAG = "PutUpdatePhotoProfile";
    private static final int MY_SOCKET_TIMEOUT_MS = 200000 ;
    private final String encodedImage;
    private User user;

    protected abstract void onUpdatePhotoProfileSuccess();
    protected abstract void onAppServerUpdatePhotoProfileDefaultError();
    protected abstract void onAppServerConnectionError();

    public PutUpdatePhothoProfileUser(User user, String encodedImage){
        this.user = user;
        this.encodedImage = encodedImage;
    }

    public void make(){
        BaseStringRequest updateUserProfilePhotoRequest = new BaseStringRequest(RestAPIContract.PUT_PHOTO_USER(user.getEmail()), getHeaders(), getBody() ,getResponseListener(), getErrorListener(), Request.Method.PUT);

        updateUserProfilePhotoRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(updateUserProfilePhotoRequest);
    }
    @NonNull
    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken());
        return headers;
    }

    private String getBody() {
        String body = "";
        JSONObject paramsJson = new JSONObject();

        try {

            paramsJson.put("photo",encodedImage);

            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);

            body =paramsJson.toString();
            Log.d(TAG, "Body: " + body);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;
    }

    @NonNull
    private Response.Listener<String> getResponseListener() {
        return new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Success response: " + response);
                onUpdatePhotoProfileSuccess();
            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                try{
                    if (error.networkResponse!= null){
                        String response = new String(error.networkResponse.data, "utf-8");
                        try {
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("Mensaje");
                            Log.e(TAG, "Volley error: " + message + ", code: " + error.networkResponse.statusCode);

                            if  (error instanceof NoConnectionError) {
                                onAppServerConnectionError();
                                return;
                            }
                            if (error.networkResponse.statusCode == 401){
                                onErrorNoAuth();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                onAppServerUpdatePhotoProfileDefaultError();
                return;
            }
        };
        return errorListener;
    }
    private void onErrorNoAuth() {
        PostAppServerTokenRequest request = new PostAppServerTokenRequest() {
            @Override
            protected void onRefreshAppServerTokenSuccess() {
                retry();
            }

            @Override
            protected void onRefreshAppServerTokenFailedDefaultError() {
                onAppServerUpdatePhotoProfileDefaultError();
            }

            @Override
            protected void onRefreshAppServerTokenFailedUserConnectionError() {
                onAppServerUpdatePhotoProfileDefaultError();
            }

            @Override
            protected void onErrorNoAuth() {
                logOut();
            }
        };
        request.make();
    }

    protected abstract void logOut();

    private void retry() {
        this.make();
    }
}
