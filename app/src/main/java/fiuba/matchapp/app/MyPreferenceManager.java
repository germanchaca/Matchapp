package fiuba.matchapp.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;

/**
 * Created by german on 4/21/2016.
 */
public class MyPreferenceManager {



    private String TAG = MyPreferenceManager.class.getSimpleName();

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "matchapp";

    private static final String KEY_NOTIFICATIONS = "notifications";
    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ALIAS = "user_alias" ;
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_GENDER = "user_gender" ;
    private static final String KEY_USER_AGE = "user_age" ;
    private static final String KEY_USER_LOCATION_LATITUDE = "location_latitude";
    private static final String KEY_USER_LOCATION_LONGITUDE = "location_longitude";
    private static final String KEY_USER_PHOTO_PROFILE = "user_photo_profile" ;
    private static final String KEY_USER_INTERESTS = "user_interests" ;

    private static final String KEY_APP_SERVER_TOKEN = "token" ;


    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void storeAppServerToken (String token){
        editor.putString(KEY_APP_SERVER_TOKEN, token);
        editor.commit();
        Log.d(TAG, "AppServerToken guardado en shared preferences. " + token);

    }
    public String getAppServerToken (){
        String appServerToken = pref.getString(KEY_APP_SERVER_TOKEN,null);
        return appServerToken;
    }
    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_ALIAS,user.getAlias());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_GENDER, user.getGenre());

        editor.putInt(KEY_USER_AGE, user.getAge());
        editor.putString(KEY_USER_LOCATION_LONGITUDE, Double.toString(user.getLongitude()));
        editor.putString(KEY_USER_LOCATION_LATITUDE, Double.toString(user.getLatitude()));

        editor.putString(KEY_USER_PHOTO_PROFILE, user.getPhotoProfile());
        //editor.putString(KEY_USER_BIRTHDAY, user.getBirthday());
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<UserInterest>>() {}.getType();
        editor.putString(KEY_USER_INTERESTS, gson.toJson(user.getInterests(),type));

        editor.commit();

        Log.d(TAG, "Usuario guardado en shared preferences. " + user.getName() + ", " + user.getEmail() + ", "+ user.getAlias()+ ", " + user.getGenre()+ ", " + user.getAge()+ ", "+  user.getLongitude()+ ", " + user.getLatitude() + ", "+ user.getPhotoProfile());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email,alias, gender, birthday, longitude, latitude,fbId, photoProfile;
            int age;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            alias = pref.getString(KEY_USER_ALIAS, null);
            email = pref.getString(KEY_USER_EMAIL, null);
            gender = pref.getString(KEY_USER_GENDER, null);
            //birthday = pref.getString(KEY_USER_AGE, null);
            age = pref.getInt(KEY_USER_AGE, 0);
            longitude = pref.getString(KEY_USER_LOCATION_LONGITUDE, null);
            latitude = pref.getString(KEY_USER_LOCATION_LATITUDE, null);
            photoProfile = pref.getString(KEY_USER_PHOTO_PROFILE, null);

            Gson gson = new Gson();
            String json = pref.getString(KEY_USER_INTERESTS, null);
            Type type = new TypeToken<ArrayList<UserInterest>>() {}.getType();
            ArrayList<Interest> interests = gson.fromJson(json, type);

            User user = new User();
            user.setName(name);
            user.setAge(age);
            user.setAlias(alias);
            user.setEmail(email);
            user.setId(id);
            user.setGenre(gender);
            user.setLatitude(Double.parseDouble(latitude));
            user.setLongitude(Double.parseDouble(longitude));
            user.setPhotoProfile(photoProfile);
            user.setAge(age);
            user.setInterests(interests);
            return user;
        }
        return null;
    }


    public void addNotification(String notification) {

        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
