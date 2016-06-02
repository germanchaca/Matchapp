package fiuba.matchapp.controller.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fiuba.matchapp.R;
import fiuba.matchapp.model.User;
import fiuba.matchapp.controller.fragment.DatePickerFragment;
import fiuba.matchapp.controller.clickToSelectEditText.ClickToSelectEditText;
import fiuba.matchapp.controller.clickToSelectEditText.Item;
import fiuba.matchapp.model.UserInterest;
import fiuba.matchapp.networking.httpRequests.PostSingUpRequest;
import fiuba.matchapp.utils.AgeUtils;
import fiuba.matchapp.utils.FacebookUtils;
import fiuba.matchapp.utils.MD5;

public class SignupActivity extends GetLocationActivity {
    private static final String TAG = "SignupActivity";


    private EditText _nameText;
    private EditText _emailText;
    private EditText _dateText;
    private EditText _passwordText;
    private Button _signupButton;
    private TextView _loginLink;

    private ClickToSelectEditText<Item> _sex_input;

    private DatePickerFragment dateFragment;
    private LinearLayout parentLayout;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        //launchFinishingSignUpActivity();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initSignUpFromFacebookData(extras);
        }
        super.initUserLastLocation();
        super.locationServiceConnect();

    }

    private void initViews() {
        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_username);
        _dateText = (EditText) findViewById(R.id.input_date);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
        _sex_input = (ClickToSelectEditText<Item>) findViewById(R.id.sex_input);
        parentLayout = (LinearLayout) findViewById(R.id.linearLayoutSignUp);
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
        initGenreInputDialog();

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.creating_account));
    }

    private void initGenreInputDialog() {
        ArrayList<Item> lstGenres = new ArrayList<Item>();

        String[] sexos = getResources().getStringArray(R.array.sex_array);

        for (int i = 0; i < sexos.length; i++) {
            Item iSexo = new Item(sexos[i]);
            lstGenres.add(iSexo);
        }
        _sex_input.setItems(lstGenres);
    }


    public void initSignUpFromFacebookData(Bundle extras) {
        //String fbId = FacebookUtils.getFbId(extras);
        User userFromFacebookData = FacebookUtils.getUserFromFacebookData(extras);

        _emailText.setText(userFromFacebookData.getEmail());
        _nameText.setText(userFromFacebookData.getName());
        _sex_input.setText(userFromFacebookData.getGenre());
    }

    public void showDatePickerDialog(View v) {
        dateFragment = new DatePickerFragment();
        dateFragment.setEditText(_dateText);
        dateFragment.show(getFragmentManager(), "datePicker");
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validateUserFields()) {
            onSignupFailed(getResources().getString(R.string.signup_failed));
            return;
        }
        super.locationServiceConnect();

        _signupButton.setEnabled(false);

        progressDialog.show();
        
        User user = new User();
        user.setEmail(_emailText.getText().toString());
        user.setName(_nameText.getText().toString());
        user.setAlias(_nameText.getText().toString());
        user.setAge(AgeUtils.getAgeFromBirthDay(dateFragment.birthYear, dateFragment.birthMonth, dateFragment.birthDay));
        user.setInterests(new ArrayList<UserInterest>());
        user.setGenre(_sex_input.getText().toString());
        user.setLatitude(super.latitude);
        user.setLongitude(super.longitude);

        String userPassword = MD5.getHashedPassword(_passwordText.getText().toString());

        PostSingUpRequest postSignUpRequest = new PostSingUpRequest(user, userPassword) {
            @Override
            protected void onSignupSuccess() {
                onRequestSignupSuccess();
            }

            @Override
            protected void onSignUpFailedUserInvalidError() {
                String errorMessage = getResources().getString(R.string.username_used);
                _emailText.setError(errorMessage);
                onSignupFailed(errorMessage);
            }

            @Override
            protected void onSignUpFailedUserConnectionError() {
                String errorMessage = getResources().getString(R.string.internet_problem);
                onSignupFailed(errorMessage);
            }

            @Override
            protected void onSignUpFailedDefaultError() {
                String errorMessage = getResources().getString(R.string.signup_failed);
                onSignupFailed(errorMessage);
            }
        };

        postSignUpRequest.make();
    }

    public void onRequestSignupSuccess() {

        progressDialog.dismiss();
        _signupButton.setEnabled(true);

        launchFinishingSignUpActivity();
    }

    private void launchFinishingSignUpActivity() {
        Intent intent = new Intent(this, FinishingSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed(String errorMessage) {
        progressDialog.dismiss();
        Snackbar.make(parentLayout,errorMessage,Snackbar.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        
    }

    public boolean validateUserFields() {
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