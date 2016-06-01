package fiuba.matchapp.networking;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;

/**
 * Created by german on 5/25/2016.
 */
public class JsonParser {
    public static String getAppServerTokenFromJSONresponse(JSONObject response){

        String appServerToken= null;
        try {
            appServerToken = response.getString("token");
           // Log.d("JsonParser", "AppServerToken levantado " + appServerToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appServerToken;
    }

    public static User getUserFromJSONresponse(JSONObject response) {
        JSONObject userObj = null;
        try {
            userObj = getUserJsonObject(response, userObj);

            if(userObj != null){

                User loggedUser = new User();

                getUserId(loggedUser, userObj);

                String user_name = getUserName(userObj);
                loggedUser.setName(user_name);

                String user_alias = getUserAlias(userObj);
                loggedUser.setAlias(user_alias);

                String user_email = getUserMail(userObj);
                loggedUser.setEmail(user_email);

                int user_age = getUser_age(userObj);
                loggedUser.setAge(user_age);

                String user_photo = getUserPhoto(userObj);
                loggedUser.setPhotoProfile(user_photo);

                String user_genre = getUserGenre(userObj);
                loggedUser.setGenre(user_genre);

                ArrayList<Interest> user_interests = getUserInterests(userObj);
                loggedUser.setInterests(user_interests);

                JSONObject objLocation;
                double user_latitude;
                double user_longitude;
                objLocation = userObj.getJSONObject("location");
                user_latitude = objLocation.getDouble("latitude");
                user_longitude = objLocation.getDouble("longitude");
                loggedUser.setLatitude(user_latitude);
                loggedUser.setLongitude(user_longitude);

                return loggedUser;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static ArrayList<Interest> getUserInterests(JSONObject userObj) {
        JSONArray jsonArrayInterests = null;
        try {
            jsonArrayInterests = userObj.getJSONArray("interests");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Interest> user_interests = new ArrayList<Interest>();
        for(int i = 0; i < jsonArrayInterests.length(); i++) {
            try {
                JSONObject interestObj = jsonArrayInterests.getJSONObject(i);
                String category = interestObj.getString("category");
                String value = interestObj.getString("value");
                Interest interest = new Interest();
                interest.setCategory(category);
                interest.setDescription(value);
                user_interests.add(interest);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return user_interests;
    }

    private static String getUserGenre(JSONObject userObj) throws JSONException {
        String user_genre = userObj.getString("sex");
        
        return user_genre;
    }

    @Nullable
    private static String getUserPhoto(JSONObject userObj) throws JSONException {
        String user_photo = userObj.getString("photo_profile");
        
        return user_photo;
    }

    private static int getUser_age(JSONObject userObj) throws JSONException {
        int user_age = userObj.getInt("age");
        
        return user_age;
    }

    @Nullable
    private static String getUserMail(JSONObject userObj) throws JSONException {
        String user_email = userObj.getString("email");
        
        return user_email;
    }

    @Nullable
    private static String getUserAlias(JSONObject userObj) throws JSONException {
        String user_alias = userObj.getString("alias");
        return user_alias;
    }

    @Nullable
    private static String getUserName(JSONObject userObj) throws JSONException {
        String user_name = userObj.getString("name");
        return user_name;
    }

    @Nullable
    private static String getUserId(User user, JSONObject userObj) throws JSONException {
        String user_id = null;
        if(userObj.has("id")){
            user.setId(userObj.getString("id"));
        }
        return user_id;
    }

    private static JSONObject getUserJsonObject(JSONObject response, JSONObject userObj) throws JSONException {
        userObj = response.getJSONObject("user");
        return userObj;
    }
}
