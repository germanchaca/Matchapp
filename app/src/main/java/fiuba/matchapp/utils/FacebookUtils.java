package fiuba.matchapp.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

/**
 * Created by ger on 31/05/16.
 */
public class FacebookUtils {

    public static User getUserFromFacebookData(Intent extras){
        User user = new User();

        if(extras.hasExtra("name")) {
            user.setName(extras.getStringExtra("name"));
        }
        if(extras.hasExtra("alias")) {
            user.setAlias(extras.getStringExtra("alias"));
        }
        if(extras.hasExtra("gender")) {
            user.setEmail(extras.getStringExtra("gender"));
        }
        if(extras.hasExtra("email")){
            user.setGenre(extras.getStringExtra("email"));
        }
        if(extras.hasExtra("age")){
            user.setAge(extras.getIntExtra("age",0));
        }


        //String userBirthday = extras.getString("userBirthday");
        return user;
    }

    public static String getFbId(Bundle extras){
        if(extras.containsKey("fbId")){
           return  extras.getString("fbId");
        }else{
            return "";
        }
    }
    public static String getProfilePhotoUri(Bundle extras){
        if(extras.containsKey("profile_image")){
            return  extras.getString("profile_image");
        }else{
            return "";
        }
    }

    public String getFbProfileImageUrl(String fbId) {
        return "http://graph.facebook.com/" + fbId + "/picture?type=large";
    }

    public static void fillIntentWithUserDataFromFaceebookResponse(JSONObject object,  Intent i, String facebookId, String firstName, String full_name, String profile_image) {
        try {
            Log.d("FacebookUtils", object.toString() );

            String email = object.getString("email");
            //String gender = object.getString("gender");
            String birthday = object.getString("birthday");//"1/1/1995 format"
            int age = AgeUtils.getAgeFromBirthDay(birthday);

            //String[] sexos = MyApplication.getInstance().getResources().getStringArray(R.array.sex_array);
           /* if (gender.contentEquals("male")) {
                gender = sexos[0];
            } else {
                gender = sexos[1];
            }
            */Log.d("FacebookUtils","FBGraphCallSuccess: " + email  + " " + age +facebookId + full_name + firstName + profile_image);

            i.putExtra("fbId", facebookId);
            i.putExtra("name", full_name);
            i.putExtra("alias", firstName);

            i.putExtra("profile_image",profile_image);

            i.putExtra("email", email);
            //i.putExtra("gender", gender);
            i.putExtra("age", age);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
//String id = object.getString("id");
//String firstName = object.getString("first_name");
//String lastName = object.getString("last_name");
//String userName = new StringBuilder(firstName).append(" ").append(lastName).toString();