package fiuba.matchapp.app;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;


import fiuba.matchapp.controller.activity.WelcomeActivity;

/**
 * Created by german on 4/21/2016.
 */
public class MyApplication extends Application {
    public static final String TAG = MyApplication.class
            .getSimpleName();
    public static final String VERSION ="0.1";


    private static MyApplication mInstance;

    private MyPreferenceManager pref;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }


    public void logout() {
        pref.clear();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void deletteAccount() {
        this.logout();
    }

}
