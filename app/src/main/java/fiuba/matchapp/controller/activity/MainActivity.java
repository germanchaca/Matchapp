package fiuba.matchapp.controller.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.Serializable;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.fragment.OpenChatsFragment;
import fiuba.matchapp.controller.fragment.fragmentPlayMatching;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.gcm.Config;
import fiuba.matchapp.networking.gcm.NotificationUtils;

public class MainActivity extends GetLocationActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static Context context;
    private OpenChatsFragment fragmentChats;
    private fiuba.matchapp.controller.fragment.fragmentPlayMatching fragmentPlayMatching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.activity_main);
        initToolbar();

        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();

        } else {
            initFragments();
            //launchNewMatchActivity();
        }
          //Broadcast receiver calls when new push notification is received
        initNotificationBroadcastReceiver();
        initUserLastLocation();
        locationServiceConnect();        //para el getLocationActivity
    }

    private void launchNewMatchActivity() {
        Intent intent = new Intent(MainActivity.this, NewMatchActivity.class);

        Serializable userMatched = new User();

        intent.putExtra("new_match_user",userMatched);
        intent.putExtra("chat_room_id","1");

        startActivity(intent);
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFragments() {
        fragmentChats = new OpenChatsFragment();
        fragmentPlayMatching = new fragmentPlayMatching();
        setCurrentTabFragment(1);
        super.initUserLastLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // by doing this, the activity will be notified each time a new message arrives
        registerNotificationBroadcastReceiver();
        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        unregisterNotificationBroadcastreceiver();
        super.onPause();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Handles new push notification
     */
    private void handlePushNotification(Intent intent) {
        if (intent.hasExtra("type")) {
            String type = intent.getStringExtra("type");
            if (type == Config.PUSH_TYPE_NEW_MESSAGE) {
                //solo incrementar el badge de unreadCount, el handle lo hago en ChatRoomActivity el agregar uevo mensaje a la history
            }
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragmentPlayMatching);
                break;
            case 1:
                replaceFragment(fragmentChats);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.contentFragment, fragment);

        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_logout:
                MyApplication.getInstance().logout();
                break;

            case R.id.action_profile:

                Intent intent = new Intent(context, EditableProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.icon_game:
                setCurrentTabFragment(0);
                break;

            case R.id.icon_chats:
                setCurrentTabFragment(1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initNotificationBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    handlePushNotification(intent);
                }

            }
        };
    }
    private void registerNotificationBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    private void unregisterNotificationBroadcastreceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
