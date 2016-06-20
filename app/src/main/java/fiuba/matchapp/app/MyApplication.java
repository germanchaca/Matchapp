package fiuba.matchapp.app;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;


import java.util.concurrent.TimeUnit;

import fiuba.matchapp.controller.activity.WelcomeActivity;
import okhttp3.OkHttpClient;

/**
 * Created by german on 4/21/2016.
 */
public class MyApplication extends Application {
    public static final String TAG = MyApplication.class
            .getSimpleName();
    public static final String VERSION ="0.1";


    private static MyApplication mInstance;

    private MyPreferenceManager pref;
    private OkHttpClient appServerClient;

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

    public OkHttpClient getAppServerClient(){
        if(appServerClient == null){
            appServerClient = new OkHttpClient.Builder()
                    .connectTimeout(200, TimeUnit.SECONDS)
                    .writeTimeout(200, TimeUnit.SECONDS)
                    .readTimeout(200, TimeUnit.SECONDS)
                    .build();
        }
        return appServerClient;
    }

    public void cancelAllPendingAppServerRequests(){
        if(appServerClient != null){
            appServerClient.dispatcher().cancelAll();
        }
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
