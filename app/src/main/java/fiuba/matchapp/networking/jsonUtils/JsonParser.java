package fiuba.matchapp.networking.jsonUtils;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;

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

    public static ArrayList<User> getUsersFromJSONresponse(JSONArray response) {

        ArrayList<User> users = new ArrayList<>();

        for(int i = 0; i < response.length(); i++) {
            try {
                JSONObject userObj = response.getJSONObject(i);
                User user = JsonParser.getUserFromJSONresponse(userObj);
                if(user != null){
                    users.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
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

                ArrayList<UserInterest> user_interests = getUserInterests(userObj);
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

    private static ArrayList<UserInterest> getUserInterests(JSONObject userObj) {
        try {
            JSONArray jsonArrayInterests = userObj.getJSONArray("interests");

            ArrayList<UserInterest> user_interests = new ArrayList<UserInterest>();
            for(int i = 0; i < jsonArrayInterests.length(); i++) {
                try {
                    JSONObject interestObj = jsonArrayInterests.getJSONObject(i);
                    String category = interestObj.getString("category");
                    String value = interestObj.getString("value");
                    UserInterest interest = new UserInterest();
                    interest.setCategory(category);
                    interest.setDescription(value);
                    user_interests.add(interest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return user_interests;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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

    public static List<Interest> getInterestsFromJSONresponse(JSONObject response) {
        JSONArray interestsObj;

        try {
            interestsObj = response.getJSONArray("interests");
            if(interestsObj != null){
                return getInterests(interestsObj);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
    private static ArrayList<Interest> getInterests(JSONArray jsonArrayInterests) {

        ArrayList<Interest> interests = new ArrayList<>();
        for(int i = 0; i < jsonArrayInterests.length(); i++) {
            try {
                JSONObject interestObj = jsonArrayInterests.getJSONObject(i);
                String category = interestObj.getString("category");
                String value = interestObj.getString("value");
                Interest interest = new Interest();
                interest.setCategory(category);
                interest.setDescription(value);
                interest.setSelected(false);
                interests.add(interest);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return interests;
    }
}
