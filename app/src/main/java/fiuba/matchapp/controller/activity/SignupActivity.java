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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.controller.fragment.DatePickerFragment;
import fiuba.matchapp.controller.clickToSelectEditText.ClickToSelectEditText;
import fiuba.matchapp.controller.clickToSelectEditText.Item;
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
    private String email;
    private String password;
    private String gender;
    private String birthday;
    private int age;
    private String fbId;
    private Boolean hasFbId;
    
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


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayList<Item> lstSexos = new ArrayList<Item>();

        String[] sexos = getResources().getStringArray(R.array.sex_array);

        for (int i = 0; i < sexos.length ; i++){
            Item iSexo = new Item(sexos[i]);
            lstSexos.add(iSexo);
        }
        _sex_input.setItems(lstSexos);
        inicializarConCuentaFacebook();

    }


    public void inicializarConCuentaFacebook(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("userName");
            email = extras.getString("email");
            gender = extras.getString("gender");
            birthday = extras.getString("birthday");
            fbId = extras.getString("fbId");

            _dateText.setText(birthday);
            _emailText.setText(email);
            _nameText.setText(userName);
            _sex_input.setText(gender);
            hasFbId = true;
        }else{
            hasFbId = false;
        }
    }
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(_dateText);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void signup() {
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
        email = _emailText.getText().toString();
        birthday = _dateText.getText().toString();
        age = Integer.parseInt(birthday.replace("/", ""));
        gender = _sex_input.getText().toString();
        password = _passwordText.getText().toString();
        //aca tambien mandar el fbImageUrl al server

        StringRequest strReq = new StringRequest(Request.Method.POST,
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
                    userJson.put("sex",gender);
                    userJson.put("age",age);
                    userJson.put("interests", interestsJsonArray);
                    userJson.put("photo_profile","");
                    userJson.put("password", MD5.getHashedPassword(password));
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

        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    public void onSignupSuccess() {
        /*User user;
        user = new User("0", userName, userName, email,birthday,gender);
        if (hasFbId){
            user.setFbId(this.fbId);
        }
        MyApplication.getInstance().getPrefManager().storeUser(user);*/

        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(this, Intro.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed(String errorMessage) {
        Toast.makeText(getBaseContext(),errorMessage, Toast.LENGTH_LONG).show();
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