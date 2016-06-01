package fiuba.matchapp.utils;

import android.os.Bundle;

import fiuba.matchapp.model.User;

/**
 * Created by ger on 31/05/16.
 */
public class FacebookUtils {
    public String getFbProfileImageUrl(String fbId) {
        return "http://graph.facebook.com/" + fbId + "/picture?type=large";
    }

    public static User getUserFromFacebookData(Bundle extras){
        User user = new User();

        if(extras.containsKey("userName")) {
            user.setName(extras.getString("userName"));
        }
        if(extras.containsKey("userName")) {
            user.setAlias(extras.getString("userName"));
        }
        if(extras.containsKey("userGender")) {
            user.setEmail(extras.getString("userGender"));
        }
        if(extras.containsKey("email")){
            user.setGenre(extras.getString("email"));
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
}
