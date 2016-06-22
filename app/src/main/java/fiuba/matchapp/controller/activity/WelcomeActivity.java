package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.google.repacked.apache.commons.codec.binary.Base64;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.baseActivity.FacebookLoginActivity;
import fiuba.matchapp.utils.FacebookUtils;


public class WelcomeActivity extends FacebookLoginActivity {
    private static final String TAG = "WelcomeActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInstance().getPrefManager().clear();

        setContentView(R.layout.activity_welcome);

        Button loginButton = (Button) findViewById(R.id.btn_login);
        Button signUpButton = (Button) findViewById(R.id.btn_signup);
        Button loginWithFacebook = (Button) findViewById(R.id.btn_fb_login);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchLoginActivity();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                launchSignUpActivity();
            }
        });

        loginWithFacebook.setOnClickListener(new FacebookLoginActivity.FacebookLogInButtonListener());
    }

    private void launchSignUpActivity() {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onFacebookLoggedIn(LoginResult loginResult) {

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            final String facebook_id = profile.getId();

            final String first_name = profile.getFirstName();

            final String full_name=profile.getName();
            //get The Uri of the profile picture.
            final String profile_image = profile.getProfilePictureUri(400, 600).toString();

            Log.d(TAG,"FBloginSuccess: " + facebook_id + " " + full_name + " " + profile_image);

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {

                            Intent i = new Intent(WelcomeActivity.this, SignupActivity.class);
                            FacebookUtils.fillIntentWithUserDataFromFaceebookResponse(object, i,facebook_id,first_name,full_name,profile_image);
                            startActivity(i);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,birthday");

            request.setParameters(parameters);
            request.executeAsync();
        }
    }



}
