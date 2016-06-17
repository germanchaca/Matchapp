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
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;

/**
 * Created by ger on 01/06/16.
 */
public abstract class PostChatNewMessageRequest {

    private static final String TAG = "PostChatNewMessageRequest";
    private static final int MY_SOCKET_TIMEOUT_MS = 200000 ;
    private final String userId;
    private final String message;

    protected abstract void onPostChatNewMessageRequestFailedDefaultError();

    protected abstract void onPostChatNewMessageRequestFailedUserConnectionError();

    protected abstract void onPostChatNewMessageRequestSuccess();

    public PostChatNewMessageRequest(String userId, String msj){
        this.userId = userId;
        this.message = msj;
    }
    public void make() {

        BaseStringRequest signUpRequest = new BaseStringRequest(RestAPIContract.POST_CHAT, getHeaders(), getBody() ,getResponseListener(), getErrorListener(), Request.Method.POST);

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

    private String getBody() {
        String body = "";
        JSONObject paramsJson = new JSONObject();

        try {
            paramsJson.put("To", this.userId);
            paramsJson.put("message", this.message);

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
                onPostChatNewMessageRequestSuccess();

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
                            onPostChatNewMessageRequestFailedUserConnectionError();
                            return;
                        }else {
                            onPostChatNewMessageRequestFailedDefaultError();
                        }
                    }else {
                        Log.d(TAG, "Network Response == null " );
                        //Retry
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

}
