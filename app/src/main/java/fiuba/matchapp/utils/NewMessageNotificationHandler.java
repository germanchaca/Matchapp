package fiuba.matchapp.utils;

import android.content.Intent;
import android.util.Log;

import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.ReceivedMessage;

/**
 * Created by ger on 21/06/16.
 */
public class NewMessageNotificationHandler {

    public static String getChatRoomId(Intent intent) {
        if (intent.hasExtra("chat_room_id")) {
            String chatRoomId = intent.getStringExtra("chat_room_id");
            return chatRoomId;
        }
        return "";
    }

    public static ReceivedMessage getMessage(Intent intent){
        if (intent.hasExtra("message_id")) {
            String message_id = intent.getStringExtra("message_id");
            if (intent.hasExtra("message")) {
                String messageBody = intent.getStringExtra("message");
                if (intent.hasExtra("created_at")) {
                    String timestamp = intent.getStringExtra("created_at");

                    ReceivedMessage message = new ReceivedMessage(message_id,messageBody,timestamp);

                    return message;
                }
            }
        }

        return null;
    }
}
