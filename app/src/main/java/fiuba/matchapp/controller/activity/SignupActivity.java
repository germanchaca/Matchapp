package fiuba.matchapp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
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
import java.util.Calendar;
import java.util.HashMap;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.controller.fragment.DatePickerFragment;
import fiuba.matchapp.controller.clickToSelectEditText.ClickToSelectEditText;
import fiuba.matchapp.controller.clickToSelectEditText.Item;
import fiuba.matchapp.networking.BaseStringRequest;
import fiuba.matchapp.networking.JSONmetadata;
import fiuba.matchapp.networking.JsonObjectGen;
import fiuba.matchapp.networking.JsonParser;
import fiuba.matchapp.networking.RestAPIContract;
import fiuba.matchapp.utils.MD5;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText _nameText;
    private EditText _emailText;
    private EditText _dateText;
    private EditText _passwordText;
    private Button _signupButton;
    private TextView _loginLink;

    private ClickToSelectEditText<Item> _sex_input;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userGender;
    private String userBirthday;
    private int userAge, year,month,day;
    private String fbId;
    private Boolean hasFbId;
    private DatePickerFragment dateFragment;
    private LinearLayout parentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_username);
        _dateText = (EditText) findViewById(R.id.input_date);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
        _sex_input = (ClickToSelectEditText<Item>) findViewById(R.id.sex_input);
        parentLayout = (LinearLayout) findViewById(R.id.linearLayoutSignUp);

        initConfirmSignInButton();
        initLoginLinkButton();
        initSexInput();
        inicializarConCuentaFacebook();

    }

    private void initConfirmSignInButton() {
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signup();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initLoginLinkButton() {
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initSexInput() {
        ArrayList<Item> lstSexos = new ArrayList<Item>();

        String[] sexos = getResources().getStringArray(R.array.sex_array);

        for (int i = 0; i < sexos.length; i++) {
            Item iSexo = new Item(sexos[i]);
            lstSexos.add(iSexo);
        }
        _sex_input.setItems(lstSexos);
    }


    public void inicializarConCuentaFacebook() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("userName");
            userEmail = extras.getString("email");
            userGender = extras.getString("userGender");
            userBirthday = extras.getString("userBirthday");
            fbId = extras.getString("fbId");

            _dateText.setText(userBirthday);
            _emailText.setText(userEmail);
            _nameText.setText(userName);
            _sex_input.setText(userGender);
            hasFbId = true;
        } else {
            hasFbId = false;

        }
    }

    public void showDatePickerDialog(View v) {
        dateFragment = new DatePickerFragment();
        dateFragment.setEditText(_dateText);
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    public void signup() throws JSONException {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed(getResources().getString(R.string.signup_failed));
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.creating_account));
        progressDialog.show();

        userName = _nameText.getText().toString();
        userEmail = _emailText.getText().toString();
        userBirthday = _dateText.getText().toString();
        userAge = calculateUserAge();
        userGender = _sex_input.getText().toString();
        userPassword = MD5.getHashedPassword(_passwordText.getText().toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");

        JSONObject paramsJson = new JSONObject();

        JSONObject userJson = new JSONObject();
        userJson.put("name", userName);
        userJson.put("alias", userName);
        userJson.put("email", userEmail);
        userJson.put("sex", userGender);
        userJson.put("age", userAge);
        JSONArray interestsJsonArray = new JSONArray();
        userJson.put("interests", interestsJsonArray);
        userJson.put("photo_profile", "");
        userJson.put("password", userPassword);
        JSONObject locationJson = JsonObjectGen.getJsonObjectFromLocation(0, 0);
        userJson.put("location", locationJson);
        userJson.put("gcm_registration_id", FirebaseInstanceId.getInstance().getToken());

        paramsJson.put("user",userJson);


        JSONObject metadataJson = JSONmetadata.getMetadata(1);

        paramsJson.put("metadata", metadataJson);

        Response.Listener<String> stringResponse = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response);
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    User loggedUser = JsonParser.getUserFromJSONresponse(obj);
                    String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);

                    MyApplication.getInstance().getPrefManager().storeUser(loggedUser);
                    MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

                    onSignupSuccess();

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }
            }
        };


        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMessage = getResources().getString(R.string.signup_failed);

                try{
                    if (error.networkResponse!= null){
                        String response = new String(error.networkResponse.data, "utf-8");
                        try {
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("Mensaje");
                            Log.e(TAG, "Volley error: " + message + ", code: " + error.networkResponse.statusCode);

                            if (error.networkResponse.statusCode == 400){
                                errorMessage = getResources().getString(R.string.username_used);
                                _emailText.setError(getResources().getString(R.string.username_used));

                            }else {
                                if (error instanceof NoConnectionError) {
                                    errorMessage = getResources().getString(R.string.internet_problem);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                onSignupFailed(errorMessage);
                progressDialog.dismiss();
            }
        };

        String body =paramsJson.toString();


        Log.d(TAG, "Body: " + body);

        BaseStringRequest signUpRequest = new BaseStringRequest(RestAPIContract.POST_USER, headers, body ,stringResponse, errorListener, Request.Method.POST);

        MyApplication.getInstance().addToRequestQueue(signUpRequest);
    }

    private int  calculateUserAge() {
        final Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);

        int age = currentYear - dateFragment.birthYear;

        if (today.get(Calendar.MONTH) < dateFragment.birthMonth ) {
            age--;
        } else if (today.get(Calendar.MONTH) == dateFragment.birthMonth
                && today.get(Calendar.DAY_OF_MONTH) < dateFragment.birthDay) {
            age--;
        }
        return age;
    }

    public void onSignupSuccess() {


        _signupButton.setEnabled(true);

        Intent intent = new Intent(this, Intro.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed(String errorMessage) {
        //Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
        Snackbar.make(parentLayout,errorMessage,Snackbar.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String birthDate = _dateText.getText().toString();
        String gender = _sex_input.getText().toString();

        if (name.isEmpty()) {
            _nameText.setError(getResources().getString(R.string.invalid_name_empty));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (birthDate.isEmpty()) {
            _dateText.setError(getResources().getString(R.string.invalid_birthdate));
            valid = false;
        } else {
            _dateText.setError(null);
        }
        if (gender.isEmpty()) {
            _sex_input.setError(getResources().getString(R.string.invalid_gender));
            valid = false;
        } else {
            _sex_input.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getResources().getString(R.string.invalid_mail_invalid));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getResources().getString(R.string.invalid_password_format));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}