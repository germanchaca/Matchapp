package fiuba.matchapp.networking.httpRequests;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.ChatRoom;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.networking.jsonUtils.JsonParser;

/**
 * Created by ger on 01/06/16.
 */
public abstract class GetChatOpenRoomsRequest {

    private static final String TAG = "GetChatHistoryRequest";
    private static final int MY_SOCKET_TIMEOUT_MS = 200000 ;


    protected abstract void onGetChatOpenRoomsRequestFailedDefaultError();

    protected abstract void onGetChatOpenRoomsRequestFailedUserConnectionError();

    protected abstract void onGetChatOpenRoomsRequestSuccess(List<ChatRoom> chatRooms);

    public GetChatOpenRoomsRequest(){

    }
    public void make() {

        BaseStringRequest signUpRequest = new BaseStringRequest(RestAPIContract.GET_CHATROOMS, getHeaders(), "" ,getResponseListener(), getErrorListener(), Request.Method.GET);

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
                                onGetChatOpenRoomsRequestFailedUserConnectionError();
                                return;
                            }else if (error.networkResponse.statusCode == 401) {
                                onErrorNoAuth();
                                return;
                            }else {
                                onGetChatOpenRoomsRequestFailedDefaultError();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
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


    private void onSuccessResponse(String response) throws JSONException {

        JSONArray jsonResponse = new JSONArray(response);

        List<ChatRoom> chatRooms = JsonParser.getChatRoomsFromJSONResponse(jsonResponse);

        onGetChatOpenRoomsRequestSuccess(chatRooms);
    }
    private void onErrorNoAuth() {
        PostAppServerTokenRequest request = new PostAppServerTokenRequest() {
            @Override
            protected void onRefreshAppServerTokenSuccess() {
                retry();
            }

            @Override
            protected void onRefreshAppServerTokenFailedDefaultError() {
                onGetChatOpenRoomsRequestFailedDefaultError();
            }

            @Override
            protected void onRefreshAppServerTokenFailedUserConnectionError() {
                onGetChatOpenRoomsRequestFailedUserConnectionError();
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