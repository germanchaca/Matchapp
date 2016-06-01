package fiuba.matchapp.networking;

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

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

/**
 * Created by ger on 31/05/16.
 */
public abstract class PostSingUpRequest {
    private static final String TAG = "SignupActivity";
    private final User user;
    private final String password;

    protected abstract void onSignupSuccess();
    protected abstract void onSignUpFailedUserInvalidError();
    protected abstract void onSignUpFailedUserConnectionError();
    protected abstract void onSignUpFailedDefaultError();

    public PostSingUpRequest(User user, String password){
        this.user = user;
        this.password = password;
    }

    public void make() {

        BaseStringRequest signUpRequest = new BaseStringRequest(RestAPIContract.POST_USER, getHeaders(), getBody() ,getResponseListener(), getErrorListener(), Request.Method.POST);

        signUpRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
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
            userJson.put("name", user.getName());
            userJson.put("alias", user.getAlias());
            userJson.put("email", user.getEmail());
            userJson.put("sex", user.getGenre());
            userJson.put("age", user.getAge());

            JSONArray interestsJsonArray = new JSONArray(user.getInterests());
            userJson.put("interests", interestsJsonArray);

            userJson.put("photo_profile", user.getPhotoProfile());

            userJson.put("password", password);

            JSONObject locationJson = JsonUtils.getJsonObjectFromLocation(user.getLatitude(), user.getLongitude());
            userJson.put("location", locationJson);
            userJson.put("gcm_registration_id", FirebaseInstanceId.getInstance().getToken());

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
                        try {
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("Mensaje");
                            Log.e(TAG, "Volley error: " + message + ", code: " + error.networkResponse.statusCode);

                            if (error.networkResponse.statusCode == 400){
                                onSignUpFailedUserInvalidError();
                                return;

                            }else if  (error instanceof NoConnectionError) {
                                onSignUpFailedUserConnectionError();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                onSignUpFailedDefaultError();
                return;
            }
        };
        return errorListener;
    }


    private void onSuccessResponse(String response) throws JSONException {
        JSONObject obj = new JSONObject(response);
        User loggedUser = JsonParser.getUserFromJSONresponse(obj);
        String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);

        MyApplication.getInstance().getPrefManager().storeUser(loggedUser);
        MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

        onSignupSuccess();
    }

}
