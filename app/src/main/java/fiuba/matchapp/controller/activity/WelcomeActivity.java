package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.google.repacked.apache.commons.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.baseActivity.FacebookLoginActivity;
import fiuba.matchapp.networking.httpRequests.okhttp.PostSignInOkHttp;
import fiuba.matchapp.utils.FacebookUtils;
import fiuba.matchapp.utils.MD5;
import fiuba.matchapp.view.LockedProgressDialog;


public class WelcomeActivity extends FacebookLoginActivity {
    private static final String TAG = "WelcomeActivity";
    private LockedProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getInstance().getPrefManager().clear();
        Log.d(TAG,super.printHashKey(getApplicationContext()));

        setContentView(R.layout.activity_welcome);

        Button loginButton = (Button) findViewById(R.id.btn_login);
        Button signUpButton = (Button) findViewById(R.id.btn_signup);
        Button loginWithFacebook = (Button) findViewById(R.id.btn_fb_login);
        progressDialog = new LockedProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setMessage(getResources().getString(R.string.running_auth));

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
                        public void onCompleted(final JSONObject object,
                                                GraphResponse response) {

                            try {
                                if (object.has("email")){
                                    String email = object.getString("email");

                                    progressDialog.show();
                                    PostSignInOkHttp request = new PostSignInOkHttp(email, MD5.getHashedPassword(facebook_id)) {
                                        @Override
                                        protected void onSignInFailedUserConnectionError() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    onLoginFailed(getResources().getString(R.string.internet_problem));
                                                }
                                            });
                                        }

                                        @Override
                                        protected void onSignInFailedUserNotCorrect() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    launchFbSignUpActivity(object, facebook_id, first_name, full_name, profile_image);
                                                }
                                            });
                                        }

                                        @Override
                                        protected void onSignInSuccess() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    launchMainActivity();
                                                }
                                            });
                                        }
                                    };
                                    request.makeRequest();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,birthday");


            request.setParameters(parameters);
            request.executeAsync();
        }
    }
    public void onLoginFailed(String errorMessage) {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), errorMessage , Toast.LENGTH_LONG).show();
    }
    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void launchFbSignUpActivity(JSONObject object, String facebook_id, String first_name, String full_name, String profile_image) {
        Intent i = new Intent(WelcomeActivity.this, FbSignupActivity.class);
        FacebookUtils.fillIntentWithUserDataFromFaceebookResponse(object, i,facebook_id,first_name,full_name,profile_image);
        startActivity(i);
    }


}
