package fiuba.matchapp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.controller.fragment.DatePickerFragment;
import fiuba.matchapp.controller.clickToSelectEditText.ClickToSelectEditText;
import fiuba.matchapp.controller.clickToSelectEditText.Item;
import fiuba.matchapp.networking.BaseRequest;
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
        headers.put("content-type", "application/json");

        HashMap<String, String> params = new HashMap<>();

        JSONObject userJson = new JSONObject();
        userJson.put("name", userName);
        userJson.put("alias", userName);
        userJson.put("email", userEmail);
        userJson.put("sex", userGender);
        userJson.put("userAge", userAge);
        JSONArray interestsJsonArray = new JSONArray();
        userJson.put("interests", interestsJsonArray);
        userJson.put("photo_profile", "");
        userJson.put("userPassword", userPassword);
        JSONObject locationJson = JsonObjectGen.getJsonObjectFromLocation(0, 0);
        userJson.put("location", locationJson);
        userJson.put("gcm_registration_id", FirebaseInstanceId.getInstance().getToken());

        params.put("user", userJson.toString());

        JSONObject metadataJson = JSONmetadata.getMetadata(1);
        params.put("metadata", metadataJson.toString());

        Log.d(TAG, "params: " + params.toString());

        Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Server Response: " + response);
                progressDialog.dismiss();
                User loggedUser = JsonParser.getUserFromJSONresponse(response);
                String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(response);

                if (loggedUser != null) {
                    MyApplication.getInstance().getPrefManager().storeUser(loggedUser);
                }
                if(appServerToken != null){
                    MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);
                }
                onSignupSuccess();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                int httpStatusCode = networkResponse.statusCode;

                if(httpStatusCode == HttpURLConnection.HTTP_BAD_REQUEST) {

                }
                String errorMessage = null;
                if (error instanceof NoConnectionError) {
                    errorMessage = getResources().getString(R.string.internet_problem);
                } else {
                    errorMessage = getResources().getString(R.string.signup_failed);
                }

                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                onSignupFailed(errorMessage);
                progressDialog.dismiss();
            }
        };

        BaseRequest strReq2 = new BaseRequest(RestAPIContract.POST_USER, params, response, errorListener, Request.Method.POST);
        BaseRequest strReq3 = new BaseRequest(RestAPIContract.POST_USER, params, headers, response, errorListener, Request.Method.POST);
        MyApplication.getInstance().addToRequestQueue(strReq2);
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
        setResult(RESULT_OK, null);

        Intent intent = new Intent(this, Intro.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed(String errorMessage) {
        Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
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

        /* StringRequest strReq = new StringRequest(Request.Method.POST,
                RestAPIContract.POST_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response: " + response);
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    User loggedUser = JsonParser.getUserFromJSONresponse(obj);
                    String appServerToken = JsonParser.getAppServerTokenFromJSONresponse(obj);

                    if(loggedUser != null){
                        MyApplication.getInstance().getPrefManager().storeUser(loggedUser);
                    }
                    MyApplication.getInstance().getPrefManager().storeAppServerToken(appServerToken);

                    onSignupSuccess();

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = null;
                if(error instanceof NoConnectionError) {
                    errorMessage = getResources().getString(R.string.internet_problem);
                }else{
                    errorMessage = getResources().getString(R.string.signup_failed);
                }

                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                onSignupFailed(errorMessage);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONArray interestsJsonArray = new JSONArray();

                JSONObject locationJson= JsonObjectGen.getJsonObjectFromLocation(0,0);

                JSONObject userJson=new JSONObject();
                try {
                    userJson.put("name",userName);
                    userJson.put("alias",userName);
                    userJson.put("email",email);
                    userJson.put("sex",userGender);
                    userJson.put("userAge",userAge);
                    userJson.put("interests", interestsJsonArray);
                    userJson.put("photo_profile","");
                    userJson.put("userPassword", MD5.getHashedPassword(userPassword));
                    userJson.put("location", locationJson);
                    userJson.put("gcm_registration_id", FirebaseInstanceId.getInstance().getToken());

                    params.put("user", userJson.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject metadataJson = JSONmetadata.getMetadata(1);
                params.put("metadata", metadataJson.toString());

                Log.d(TAG, "params: " + params.toString());
                return params;
            }
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("content-type","application/json; charset=utf-8");
                return headers;
            }


        };
    }
    /*User user;
        user = new User("0", userName, userName, email,userBirthday,userGender);
        if (hasFbId){
            user.setFbId(this.fbId);
        }
        MyApplication.getInstance().getPrefManager().storeUser(user);*/
