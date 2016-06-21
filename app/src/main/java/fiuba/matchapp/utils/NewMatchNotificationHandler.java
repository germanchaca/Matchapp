package fiuba.matchapp.utils;

import android.content.Intent;

import fiuba.matchapp.model.ReceivedMessage;
import fiuba.matchapp.model.User;

/**
 * Created by ger on 21/06/16.
 */
public class NewMatchNotificationHandler {

    public static String getUserMatchedId(Intent intent) {
        if (intent.hasExtra("user_id")) {
            return intent.getStringExtra("user_id");
        }
        return "";
    }
    public static String getChatRoomId(Intent intent) {
        if (intent.hasExtra("chat_room_id")) {
            return intent.getStringExtra("chat_room_id");
        }
        return "";
    }
    public static User getUserMatched(Intent intent) {
        if (intent.hasExtra("new_match_user")) {
            return intent.getParcelableExtra("new_match_user");
        }
        return null;
    }
}
