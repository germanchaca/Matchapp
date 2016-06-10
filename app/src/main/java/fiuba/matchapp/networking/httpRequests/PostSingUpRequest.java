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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.fragment.InterestsRecyclerViewFragment;
import fiuba.matchapp.controller.fragment.UploadProfilePhotoFragment;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.jsonUtils.JsonMetadataUtils;
import fiuba.matchapp.networking.jsonUtils.JsonParser;
import fiuba.matchapp.networking.jsonUtils.JsonUtils;

/**
 * Created by ger on 31/05/16.
 */
public abstract class PostSingUpRequest {
    private static final String TAG = "SignupActivity";
    private static final int MY_SOCKET_TIMEOUT_MS = 200000 ;
    private final User user;
    private final String password;

    protected abstract void onSignupSuccess(List<Interest> mapInterestsByCategory);
    protected abstract void onSignUpFailedUserInvalidError();
    protected abstract void onSignUpFailedUserConnectionError();
    protected abstract void onSignUpFailedDefaultError();

    public PostSingUpRequest(User user, String password){
        this.user = user;
        this.password = password;
    }

    public void make() {

        BaseStringRequest signUpRequest = new BaseStringRequest(RestAPIContract.POST_USER, getHeaders(), getBody() ,getResponseListener(), getErrorListener(), Request.Method.POST);

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
            userJson.put("name", user.getName());
            userJson.put("alias", user.getAlias());
            userJson.put("email", user.getEmail());
            userJson.put("sex", user.getGenre());
            userJson.put("age", user.getAge());


            JSONArray jsonArray = new JSONArray();
            for(UserInterest i:user.getInterests()){
                if(i != null){
                    JSONObject intObj = new JSONObject();
                    intObj.put("value", i.getDescription());
                    intObj.put("category",i.getCategory());
                    jsonArray.put(intObj);
                }
            }

            userJson.put("interests", jsonArray);

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

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    User loggedUser = JsonParser.getUserFromJSONresponse(obj);
                    String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);
                    MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

                    getAllInterestFromAppServer(loggedUser,appServerToken);

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


    private void onSuccessResponse(User loggedUser, String appServerToken, List<Interest> mapInterestsByCategory) throws JSONException {


        MyApplication.getInstance().getPrefManager().storeUser(loggedUser);

        MyApplication.getInstance().getPrefManager().storeUserPass(this.password);

        onSignupSuccess(mapInterestsByCategory);
    }

    private void getAllInterestFromAppServer( final User loggedUser, final String appServerToken  ) {
        GetInterestsRequest request = new GetInterestsRequest() {
            @Override
            protected void onGetInterestsSuccess(List<Interest> interests) {

                try {
                    onSuccessResponse(loggedUser, appServerToken, interests);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void onGetInterestsDefaultError() {
                onSignUpFailedUserConnectionError();
            }

            @Override
            protected void onGetInterestsConnectionError() {
                onSignUpFailedUserConnectionError();

            }

        };
        request.make();
    }




}
