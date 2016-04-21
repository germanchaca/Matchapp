package fiuba.matchapp.activity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import fiuba.matchapp.R;
import fiuba.matchapp.app.Config;
import fiuba.matchapp.fragment.Connect;
import fiuba.matchapp.fragment.OpenChatsFragment;
import fiuba.matchapp.gcm.GcmIntentService;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private String TAG_OPENCHATS_FRAGMENT = OpenChatsFragment.class.getSimpleName();
    private String TAG_CONNECT = Connect.class.getSimpleName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // if (MyApplication.getInstance().getPrefManager().getUser() == null) {
        //launchLoginActivity();
        //}

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

                    //Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    //Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    //Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }


        if (savedInstanceState == null) {
            // During initial setup, plug in the openChats fragment.
            OpenChatsFragment openChatsFragment = new OpenChatsFragment();
            openChatsFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction().add(R.id.contentFragment, openChatsFragment, TAG_OPENCHATS_FRAGMENT).addToBackStack(TAG_OPENCHATS_FRAGMENT).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private void launchLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        //Intent intent = new Intent(this,LoginActivity.class);
        //startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_logout:
                break;

            case R.id.action_profile:

                Intent intent = new Intent(context, EditableProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.icon_game:

                Fragment fragmentGame = getFragmentManager().findFragmentByTag(TAG_CONNECT);
                if (fragmentGame == null) {
                    fragmentGame = new Connect();
                }
                getFragmentManager().beginTransaction().replace(R.id.contentFragment, fragmentGame, TAG_CONNECT).addToBackStack(TAG_CONNECT).commit();

                break;

            case R.id.icon_chats:

                Fragment fragmentChats = getFragmentManager().findFragmentByTag(TAG_OPENCHATS_FRAGMENT);
                if (fragmentChats == null) {
                    fragmentChats = new OpenChatsFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.contentFragment, fragmentChats, TAG_OPENCHATS_FRAGMENT).addToBackStack(TAG_OPENCHATS_FRAGMENT).commit();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, getResources().getString(R.string.check_google_play_service_error));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_google_play_service_error), Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

}
