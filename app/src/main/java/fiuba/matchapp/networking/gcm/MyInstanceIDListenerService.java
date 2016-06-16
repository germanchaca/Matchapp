package fiuba.matchapp.networking.gcm;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.PutUpdateUserData;
import fiuba.matchapp.networking.httpRequests.RestAPIContract;

/**
 * Created by german on 4/21/2016.
 * This service invokes onTokenRefresh() method whenever there is a change in gcm registration token.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {


    private static final String TAG = "GCM ";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        // checking for valid login session
        User user = MyApplication.getInstance().getPrefManager().getUser();
        if (user == null) {
            return;
        }
        PutUpdateUserData request = new PutUpdateUserData(user) {
            @Override
            protected void onUpdateDataSuccess() {

            }

            @Override
            protected void onAppServerDefaultError() {

            }

            @Override
            protected void onAppServerConnectionError() {

            }

            @Override
            protected void logout() {
                MyApplication.getInstance().logout();
            }
        };
        request.changeGcmRegistrationId(token);
        request.make();

    }
}

