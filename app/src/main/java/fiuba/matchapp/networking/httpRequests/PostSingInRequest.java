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
import fiuba.matchapp.networking.jsonUtils.JsonParser;

/**
 * Created by ger on 01/06/16.
 */
public abstract class PostSingInRequest {

    private static final String TAG = "PostSignInRequest";
    private static final int MY_SOCKET_TIMEOUT_MS = 200000 ;
    private final String password;
    private final String email;

    protected abstract void onSignInFailedDefaultError();

    protected abstract void onSignInFailedUserConnectionError();

    protected abstract void onSignInSuccess();

    public PostSingInRequest(String email, String hashedPassword){
        this.email = email;
        this.password = hashedPassword;
    }
    public void make() {

        BaseStringRequest signUpRequest = new BaseStringRequest(RestAPIContract.POST_APPSERVER_TOKEN, getHeaders(), getBody() ,getResponseListener(), getErrorListener(), Request.Method.POST);

        signUpRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(signUpRequest);
    }
    @NonNull
    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    private String getBody() {
        String body = "";
        JSONObject paramsJson = new JSONObject();

        JSONObject userJson = new JSONObject();
        try {
            userJson.put("email", this.email);

            userJson.put("password", this.password);

            paramsJson.put("user",userJson);

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
                try {
                    onSuccessResponse(response);

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }
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
                        //JSONObject obj = new JSONObject(response);
                        //String message = obj.getString("Mensaje");
                        Log.e(TAG, "Volley error: "  + ", code: " + error.networkResponse.statusCode);

                        if  (error instanceof NoConnectionError) {
                            onSignInFailedUserConnectionError();
                            return;
                        }else {
                            onSignInFailedUserConnectionError();
                        }
                    }else {
                        Log.d(TAG, "Network Response == null " );
                        /*PostSingInRequest request = new PostSingInRequest(email, password) {
                            @Override
                            protected void onSignInFailedDefaultError() {
                                onSignInFailedUserConnectionError();
                            }

                            @Override
                            protected void onSignInFailedUserConnectionError() {
                                onSignInFailedUserConnectionError();
                            }

                            @Override
                            protected void onSignInSuccess() {
                                retry();
                            }
                        };*/
                        make();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return;
            }
        };
        return errorListener;
    }

     /*private void retry (){
        onSignInSuccess();
     }*/


    private void onSuccessResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        User loggedUser = JsonParser.getUserFromJSONresponse(obj);
        String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);

        MyApplication.getInstance().getPrefManager().storeUser(loggedUser);
        MyApplication.getInstance().getPrefManager().storeUserPass(this.password);
        MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

        onSignInSuccess();
    }

}
