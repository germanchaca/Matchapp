package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fiuba.matchapp.R;
import fiuba.matchapp.networking.httpRequests.okhttp.PostSignInOkHttp;
import fiuba.matchapp.utils.MD5;
import fiuba.matchapp.view.LockedProgressDialog;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText _userNameText;
    private EditText _passwordText;
    private LockedProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _userNameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);

        Button _loginButton = (Button) findViewById(R.id.btn_login);
        Button _signupButton = (Button) findViewById(R.id.btn_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }
    public void login() {
        Log.d(TAG, "Intentando Loguear");

        if (!validate()) {
            onLoginFailed(getResources().getString(R.string.invalid_auth));
            return;
        }

        progressDialog = new LockedProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setMessage(getResources().getString(R.string.running_auth));
        progressDialog.show();

        final String email = _userNameText.getText().toString();
        final String password = _passwordText.getText().toString();



        PostSignInOkHttp request = new PostSignInOkHttp(email, MD5.getHashedPassword(password)) {
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
                onLoginFailed(getResources().getString(R.string.error_invalid_credentials));
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String errorMessage) {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), errorMessage , Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _userNameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _userNameText.setError(getResources().getString(R.string.invalid_username_empty));
            valid = false;
        } else {
            _userNameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError(getResources().getString(R.string.invalid_password_empty));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}


/*

_link_forgot_password = (TextView) findViewById(R.id.link_forgot_password);

initForgotPasswordLinkButton();

public void showForgotPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_forgot_password));
        builder.setIcon(R.drawable.ic_https_24dp_whitw);

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint(R.string.dialog_forgot_password_hint);

        builder.setView(input);

        builder.setPositiveButton(getResources().getString(R.string.dialog_forgot_password_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailToSendPassword = input.getText().toString();

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.dialog_forgot_password_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
     private void initForgotPasswordLinkButton() {
        _link_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    }
 */