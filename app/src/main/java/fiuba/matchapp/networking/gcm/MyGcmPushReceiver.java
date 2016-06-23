package fiuba.matchapp.networking.gcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import fiuba.matchapp.controller.activity.MainActivity;

/**
 * Created by german on 4/21/2016.
 * formato del mensaje recivido
 *
 * https://fcm.googleapis.com/fcm/send
 * Content-Type:application/json
 * Authorization:key=AIzaSyZ-1u...0GBYzPu7Udno5aA // Es la Server API Key de Firebase
 *  {
     "to" : "APA91bHun4MxP5egoKMwt2KZFBaFUH-1RYqx...",
     "notification" : {
         "title" : "Nuevo match!",
         "body" : "Camila está interesada en vos",
         },
     "data" : {
         "type" : "1", //Corresponde a un nuevo match
         "user_id" : "12"
         }
    }

   https://fcm.googleapis.com/fcm/send
 * Content-Type:application/json
 * Authorization:key=AIzaSyZ-1u...0GBYzPu7Udno5aA // Es la Server API Key de Firebase
 *  {
     "to" : "APA91bHun4MxP5egoKMwt2KZFBaFUH-1RYqx...",
     "notification" : {
         "title" : "Nuevo mensaje de Camila",
         "body" : "Hola, como andas?",
         },
     "data" : {
         "type" : "2", //Corresponde a un nuevo mensaje
         "user_id" : "12",
         "message": "Hola, como andas?"
         }
 }
 */
public class MyGcmPushReceiver extends FirebaseMessagingService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage message) {

        RemoteMessage.Notification notification = message.getNotification();
        String notificationBody = "";
        String notificationTitle = "";
        String notifitacionTimestamp = "";

        if(notification != null){
            notificationBody = message.getNotification().getBody();
            notificationTitle = message.getNotification().getTitle();
            notifitacionTimestamp = Long.toString(message.getSentTime());
        }

        Map<String, String> data = message.getData();

        processDataPayload(data, notificationBody, notificationTitle, notifitacionTimestamp );
    }

    private void processDataPayload( Map<String, String> data, String notificationBody, String notificationTitle, String notifitacionTimestamp ) {

        String type = data.get("type");
        if(type == null){
            return;
        }
        switch (type) {
            case Config.PUSH_TYPE_NEW_MESSAGE:
                processChatNewMessageNotification(data, notificationBody, notificationTitle, notifitacionTimestamp);
                break;
            case Config.PUSH_TYPE_NEW_MATCH:
                processNewMatchReceived(data,notificationBody,notificationTitle,notifitacionTimestamp);
                break;
            case Config.PUSH_TYPE_NEW_READ_MESSAGE:
                processReadMessageNotification(data);
                break;
        }
    }

    private void processNewMatchReceived(Map<String, String> data, String notificationBody, String notificationTitle, String notifitacionTimestamp) {
        String userId = data.get("user_id");
        String chatRoomId = data.get("chat_room_id");

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("type", Config.PUSH_TYPE_NEW_MATCH);
            pushNotification.putExtra("user_id", userId);
            pushNotification.putExtra("chat_room_id", chatRoomId);


            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            Log.d(TAG,"New match received from user_id: " + userId );
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else {
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("type", Config.PUSH_TYPE_NEW_MATCH);
            resultIntent.putExtra("user_id", userId);
            resultIntent.putExtra("chat_room_id", chatRoomId);


            showNotificationMessage(getApplicationContext(), notificationTitle, notificationBody, notifitacionTimestamp, resultIntent);
        }
    }

    private void processChatNewMessageNotification(Map<String, String> data, String notificationBody, String notificationTitle, String notifitacionTimestamp) {
        String chatMessage = data.get("message");
        String chatRoomId = data.get("chat_room_id");
        String message_id = data.get("message_id");
        String created_at = data.get("created_at");

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);

            pushNotification.putExtra("type", Config.PUSH_TYPE_NEW_MESSAGE);
            pushNotification.putExtra("message", chatMessage);
            pushNotification.putExtra("chat_room_id", chatRoomId);
            pushNotification.putExtra("created_at", created_at);
            pushNotification.putExtra("message_id", message_id);


            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // app is in background

            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

            resultIntent.putExtra("type", Config.PUSH_TYPE_NEW_MESSAGE);
            resultIntent.putExtra("message", chatMessage);
            resultIntent.putExtra("chat_room_id", chatRoomId);
            resultIntent.putExtra("created_at", created_at);
            resultIntent.putExtra("message_id", message_id);
            showNotificationMessage(getApplicationContext(), notificationTitle, notificationBody, notifitacionTimestamp, resultIntent);
        }
        return;
    }
    private void processReadMessageNotification(Map<String, String> data) {
        String chatRoomId = data.get("chatroom_id");
        String message_id = data.get("message_id");

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);

            pushNotification.putExtra("type", Config.PUSH_TYPE_NEW_READ_MESSAGE);
            pushNotification.putExtra("chat_room_id", chatRoomId);
            pushNotification.putExtra("message_id", message_id);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

        }
        return;
    }
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
