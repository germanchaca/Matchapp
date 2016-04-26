package fiuba.matchapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

/**
 * Created by german on 4/26/2016.
 */
public abstract class FacebookLoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private LoginManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        manager = LoginManager.getInstance();
        manager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        onFacebookLoggedIn(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(FacebookLoginActivity.this, android.R.string.cancel, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                        Toast.makeText(FacebookLoginActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    protected abstract void onFacebookLoggedIn(LoginResult loginResult);

    protected class FacebookLogInButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            manager.logInWithReadPermissions(FacebookLoginActivity.this, Arrays.asList("public_profile", "email"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
