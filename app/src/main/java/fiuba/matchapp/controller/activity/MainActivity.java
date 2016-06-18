package fiuba.matchapp.controller.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import java.io.Serializable;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.controller.baseActivity.GetLocationActivity;
import fiuba.matchapp.controller.fragment.OpenChatsFragment;
import fiuba.matchapp.controller.fragment.fragmentPlayMatching;
import fiuba.matchapp.model.User;
import fiuba.matchapp.networking.gcm.Config;
import fiuba.matchapp.networking.gcm.NotificationUtils;
import fiuba.matchapp.networking.httpRequests.GetUserRequest;
import fiuba.matchapp.networking.httpRequests.SignOutRequest;
import fiuba.matchapp.view.LockedProgressDialog;

public class MainActivity extends GetLocationActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private OpenChatsFragment fragmentChats;
    private fiuba.matchapp.controller.fragment.fragmentPlayMatching fragmentPlayMatching;
    private LockedProgressDialog progressDialog;
    private RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initFragments();
          //Broadcast receiver calls when new push notification is received
        initNotificationBroadcastReceiver();

    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        initToolbar();
        progressDialog = new LockedProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setMessage(getResources().getString(R.string.signing_out));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFragments() {
        fragmentChats = new OpenChatsFragment();
        fragmentPlayMatching = new fragmentPlayMatching();
        setCurrentTabFragment(1);
        //super.initUserLastLocation();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles new push notification
     */
    private void handlePushNotification( Intent intent) {
        if (intent.hasExtra("type")) {
            String type = intent.getStringExtra("type");
            if (type == Config.PUSH_TYPE_NEW_MESSAGE) {
                //TODO cambiar de color un icono o poner un badge

            }else if(type == Config.PUSH_TYPE_NEW_MATCH){
                if (intent.hasExtra("user_id")) {
                    String userId = intent.getStringExtra("user_id");
                    GetUserRequest request = new GetUserRequest(userId) {
                        @Override
                        protected void onGetUserRequestFailedDefaultError() {

                        }

                        @Override
                        protected void onGetUserRequestFailedUserConnectionError() {

                        }

                        @Override
                        protected void onGetUserRequestSuccess(User user) {
                            Intent i = new Intent(MainActivity.this, NewMatchActivity.class);

                            i.putExtra("new_match_user", (Parcelable) user);

                            startActivity(i);
                            finish();
                        }

                        @Override
                        protected void logout() {
                            MyApplication.getInstance().logout();

                        }
                    };
                    request.make();

                }
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

                logout();
                break;

            case R.id.action_profile:

                Intent intent = new Intent(getApplicationContext(), EditableProfileActivity.class);
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

    private void logout() {

        progressDialog.show();
        Log.d(TAG,"signOut");
        SignOutRequest request = new SignOutRequest() {
            @Override
            protected void onDeleteAppServerTokenSuccess() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();
            }

            @Override
            protected void onDeleteTokenFailedDefaultError() {
                String errorMessage = getResources().getString(R.string.internet_problem);
                onDeleteTokenError(errorMessage);
            }

            @Override
            protected void onDeleteTokenFailedUserConnectionError() {
                String errorMessage = getResources().getString(R.string.internet_problem);
                onDeleteTokenError(errorMessage);
            }

            @Override
            protected void logout() {
                progressDialog.dismiss();
                MyApplication.getInstance().logout();

            }
        };
        request.make();
    }

    private void onDeleteTokenError(String errorMessage) {
        progressDialog.dismiss();
        Snackbar.make(parentLayout,errorMessage,Snackbar.LENGTH_LONG).show();
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
