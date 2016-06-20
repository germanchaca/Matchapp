package fiuba.matchapp.networking.gcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.httpRequests.okhttp.PutUserDataOkHttp;

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

        PutUserDataOkHttp request = new PutUserDataOkHttp(user) {
            @Override
            protected void onAppServerConnectionError() {

            }

            @Override
            protected void onUpdateDataSuccess() {

            }

            @Override
            protected void logout() {

            }
        };
        request.changeGcmRegistrationId(token);
        request.makeRequest();


    }
}

