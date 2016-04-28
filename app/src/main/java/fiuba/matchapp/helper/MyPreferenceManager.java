package fiuba.matchapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import fiuba.matchapp.model.User;

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

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_GENDER = "user_gender" ;
    private static final String KEY_USER_BIRTHDAY = "user_birthday" ;
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_USER_LOCATION_LATITUDE = "location_latitude";
    private static final String KEY_USER_LOCATION_LONGITUDE = "location_longitude";


    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_GENDER, user.getGenre());
        editor.putString(KEY_USER_BIRTHDAY, user.getBirthday());
        editor.putString(KEY_USER_LOCATION_LONGITUDE, user.getLongitude());
        editor.putString(KEY_USER_LOCATION_LATITUDE, user.getLatitude());
        editor.commit();

        Log.e(TAG, "Usuario guardado en shared preferences. " + user.getName() + ", " + user.getEmail());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String id, name, email, gender, birthday, longitude, latitude;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);
            gender = pref.getString(KEY_USER_GENDER, null);
            birthday = pref.getString(KEY_USER_BIRTHDAY, null);
            longitude = pref.getString(KEY_USER_LOCATION_LONGITUDE, null);
            latitude = pref.getString(KEY_USER_LOCATION_LATITUDE, null);

            User user = new User(id, name, email,gender,birthday,longitude,latitude);
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