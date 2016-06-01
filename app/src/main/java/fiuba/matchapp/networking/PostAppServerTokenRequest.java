package fiuba.matchapp.networking;

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

/**
 * Created by ger on 01/06/16.
 */
public abstract class PostAppServerTokenRequest {
    private static final String TAG = "PostSignInRequest";
    private static final int MY_SOCKET_TIMEOUT_MS = 200000 ;

    protected abstract void onRefreshAppServerTokenSuccess();

    protected abstract void onRefreshAppServerTokenFailedDefaultError();

    protected abstract void onRefreshAppServerTokenFailedUserConnectionError();

    public PostAppServerTokenRequest(){

    }
    public void make() {

        BaseStringRequest signUpRequest = new BaseStringRequest(RestAPIContract.POST_APPSERVER_TOKEN, getHeaders(), "" ,getResponseListener(), getErrorListener(), Request.Method.POST);

        signUpRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(signUpRequest);
    }
    @NonNull
    private HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", MyApplication.getInstance().getPrefManager().getAppServerToken());
        return headers;
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
                        try {
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("Mensaje");
                            Log.e(TAG, "Volley error: " + message + ", code: " + error.networkResponse.statusCode);

                            if  (error instanceof NoConnectionError) {
                                onRefreshAppServerTokenFailedUserConnectionError();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                onRefreshAppServerTokenFailedDefaultError();
                return;
            }
        };
        return errorListener;
    }

    private void onSuccessResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);
        MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

        onRefreshAppServerTokenSuccess();
    }


}
