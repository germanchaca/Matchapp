package fiuba.matchapp.networking;

import android.support.annotation.Nullable;

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appServerToken;
    }

    public static User getUserFromJSONresponse(JSONObject response) {
        JSONObject userObj = null;
        userObj = getUserJsonObject(response, userObj);

        String user_id = getUserId(userObj);
        String user_name = getUserName(userObj);
        String user_alias = getUserAlias(userObj);
        String user_email = getUserMail(userObj);
        int user_age = getUser_age(userObj);
        String user_photo = getUserPhoto(userObj);
        String user_genre = getUserGenre(userObj);
        ArrayList<Interest> user_interests = getUserInterests(userObj);

        JSONObject objLocation = null;
        String user_latitude = null;
        String user_longitude = null;
        try {
            objLocation = userObj.getJSONObject("location");
            user_latitude = objLocation.getString("latitude");
            user_longitude = objLocation.getString("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        User loggedUser = new User();
        loggedUser.setId(user_id);
        loggedUser.setName(user_name);
        loggedUser.setAlias(user_alias);
        loggedUser.setEmail(user_email);
        loggedUser.setPhotoProfile(user_photo);
        loggedUser.setAge(user_age);
        loggedUser.setGenre(user_genre);
        loggedUser.setInterests(user_interests);
        loggedUser.setLatitude(user_latitude);
        loggedUser.setLongitude(user_longitude);

        return loggedUser;
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

    private static String getUserGenre(JSONObject userObj) {
        String user_genre = null;
        try {
            user_genre = userObj.getString("sex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_genre;
    }

    @Nullable
    private static String getUserPhoto(JSONObject userObj) {
        String user_photo = null;
        try {
            user_photo = userObj.getString("photo_profile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_photo;
    }

    private static int getUser_age(JSONObject userObj) {
        int user_age = 0;
        try {
            user_age = userObj.getInt("age");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_age;
    }

    @Nullable
    private static String getUserMail(JSONObject userObj) {
        String user_email = null;
        try {
            user_email = userObj.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_email;
    }

    @Nullable
    private static String getUserAlias(JSONObject userObj) {
        String user_alias = null;
        try {
            user_alias = userObj.getString("alias");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_alias;
    }

    @Nullable
    private static String getUserName(JSONObject userObj) {
        String user_name = null;
        try {
            user_name = userObj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_name;
    }

    @Nullable
    private static String getUserId(JSONObject userObj) {
        String user_id = null;
        try {
            user_id = userObj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_id;
    }

    private static JSONObject getUserJsonObject(JSONObject response, JSONObject userObj) {
        try {
            userObj = response.getJSONObject("user");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userObj;
    }
}
