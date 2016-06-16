package fiuba.matchapp.networking.httpRequests;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;
import fiuba.matchapp.networking.jsonUtils.JsonUtils;

/**
 * Created by ger on 01/06/16.
 */
public abstract class PutUpdateUserData {

    private static final String TAG = "PutUpdateUserData";
    private static final int MY_SOCKET_TIMEOUT_MS = 200000 ;
    private User user;
    private JSONObject userJson;
    private JSONObject paramsJson;

    protected abstract void onUpdateDataSuccess();
    protected abstract void onAppServerDefaultError();
    protected abstract void onAppServerConnectionError();

    public PutUpdateUserData(User user){
        this.user = user;
        initBody();
    }

    public void make() {

        BaseStringRequest updateUserDataRequest = new BaseStringRequest(RestAPIContract.PUT_USER(user.getEmail()), getHeaders(), getBody() ,getResponseListener(), getErrorListener(), Request.Method.PUT);

        updateUserDataRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToRequestQueue(updateUserDataRequest);
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
    public void changeInterests(List<UserInterest> interests){
        fillBody( interests);
    }
    public void changeGcmRegistrationId(String gcmId){
        fillBody("gcm_registration_id", gcmId);
    }

    @NonNull
    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken());
        return headers;
    }

    private void initBody(){
        paramsJson = new JSONObject();
        userJson = new JSONObject();
    }
    private void fillBody(String key, int changeValue) {

        try {
            userJson.put(key, changeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fillBody( List<UserInterest> interests) {

        try {

            JSONArray jsonArray = new JSONArray();
            for(UserInterest i:interests){
                if(i != null){
                    JSONObject intObj = new JSONObject();
                    intObj.put("value", i.getDescription());
                    intObj.put("category",i.getCategory());
                    jsonArray.put(intObj);
                }
            }

            userJson.put("interests", jsonArray);

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

    private void fillBody() {

        try {
            userJson.put("gcm_registration_id", FirebaseInstanceId.getInstance().getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getBody(){
        String body = "";

        try {
            paramsJson.put("user",userJson);

            JSONObject metadataJson = JsonMetadataUtils.getMetadata(1);
            paramsJson.put("metadata", metadataJson);

            body =paramsJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Body: " + body);

        return body;
    }
    @NonNull
    private Response.Listener<String> getResponseListener() {
        return new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Success response: " + response);
                onUpdateDataSuccess();

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
                onAppServerDefaultError();
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
                onAppServerDefaultError();
            }

            @Override
            protected void onRefreshAppServerTokenFailedUserConnectionError() {
                onAppServerDefaultError();
            }

            @Override
            protected void onErrorNoAuth() {
                logout();
            }
        };
        request.make();
    }

    protected abstract void logout();

    private void retry() {
        this.make();
    }
}
