package fiuba.matchapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

public class LoginActivity extends FacebookLoginActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText _userNameText;
    private EditText _passwordText;
    private Button _loginButton;
    private Button loginWithFacebook;
    private TextView _signupLink;
    private TextView _link_forgot_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _userNameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        loginWithFacebook = (Button) findViewById(R.id.btn_fb_login);
        _link_forgot_password = (TextView) findViewById(R.id.link_forgot_password);

        _link_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        /**
         * Check for login session. It user is already logged in
         * redirect him to main activity
         * */
        if (MyApplication.getInstance().getPrefManager().getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        loginWithFacebook.setOnClickListener(new FacebookLogInButtonListener());
    }

    @Override
    protected void onFacebookLoggedIn(LoginResult loginResult) {

        System.out.println("loginSuccess");
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        Log.v("LoginActivity", response.toString());
                        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                        try {
                            String id = object.getString("id");
                            i.putExtra("fbId", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String firstName = object.getString("first_name");
                            String lastName = object.getString("last_name");
                            String userName = new StringBuilder(firstName).append(" ").append(lastName).toString();
                            i.putExtra("userName", userName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String email = object.getString("email");
                            i.putExtra("email", email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String gender = object.getString("gender");
                            String[] sexos = getResources().getStringArray(R.array.sex_array);
                            if (gender.contentEquals("male")) {
                                gender = sexos[0];
                            } else {
                                gender = sexos[1];
                            }
                            i.putExtra("gender", gender);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            String birthday = object.getString("birthday");
                            i.putExtra("birthday", birthday);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(i);
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,birthday,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

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
                //TODO: enviar esto al servidor para que le mande la contraseña al mail
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

    public void login() {
        Log.d(TAG, "Intentando Loguear");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.running_auth));
        progressDialog.show();

        final String email = _userNameText.getText().toString();
        String password = _passwordText.getText().toString();

        //START server auth logic
        /*
        StringRequest strReq = new StringRequest(Request.Method.POST,
                RestAPIContract.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in
                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(userObj.getString("user_id"),
                                userObj.getString("name"),
                                userObj.getString("email"));

                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(user);

                        onLoginSuccess();
                        progressDialog.dismiss();
                    } else {
                        //Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    progressDialog.dismiss();
                    //Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                //Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                onLoginFailed();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", "german");
                params.put("email", email);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);

        //ENDS server auth logic
        */

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        //mandar data de volley
                        // onLoginFailed();
                        onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    public void onBackPressed() {
        // Deshabilita la opción de volver a la MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        User user = new User("0", "german", "germanchaca@gmail.com", null, null);
        MyApplication.getInstance().getPrefManager().storeUser(user);

        _loginButton.setEnabled(true);
        launchMainActivity();
    }

    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), getResources().getString(R.string.invalid_auth), Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
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
