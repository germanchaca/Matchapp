package fiuba.matchapp.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;

/**
 * Created by german on 4/25/2016.
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();
        }else {
            launchMainActivity();
        }

    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}