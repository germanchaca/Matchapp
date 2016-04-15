package fiuba.matchapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fiuba.matchapp.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _userNameText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _userNameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink  = (TextView) findViewById(R.id.link_signup);
        
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

        String email = _userNameText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implementar la lógica de autenticación acá, notar que el handler 3000 milisegundos pero es sólo para mostrar la pantallita del ProgressDialog

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                //Aca va la logica cuando vuelve del registro exitoso!!!!!!
                onLoginSuccess();
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Deshabilita la opción de volver a la MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        launchMainActivity();
        _loginButton.setEnabled(true);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
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
