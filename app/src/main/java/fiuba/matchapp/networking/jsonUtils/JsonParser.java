package fiuba.matchapp.networking.jsonUtils;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fiuba.matchapp.model.ChatRoom;
import fiuba.matchapp.model.Interest;
import fiuba.matchapp.model.Message;
import fiuba.matchapp.model.User;
import fiuba.matchapp.model.UserInterest;

public class JsonParser {

    public static List<ChatRoom> getChatRoomsFromJSONResponse(JSONObject response){
        ArrayList<ChatRoom> chatrooms= new ArrayList<>();

            try {
                JSONArray chats = response.getJSONArray("chats");
                for(int i = 0; i < chats.length(); i++) {
                    try {
                        Log.d("JsonParser", response.toString());
                        JSONObject chatRoomObj = chats.getJSONObject(i);
                        ChatRoom chatRoom = getChatRoomFromJSONresponse(chatRoomObj);
                        if (chatRoom != null) {
                            chatrooms.add(chatRoom);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }

        return chatrooms;
    }

    private static ChatRoom getChatRoomFromJSONresponse(JSONObject chatRoomObj) {
        try {
            String chatRoomId = chatRoomObj.getString("chatroom_id");
            User user = getUserFromJSONresponse(chatRoomObj);
            int unread = chatRoomObj.getInt("Unread");


            if(chatRoomObj.has("LastMessage")){
                JSONObject lastMessageJsonObj = chatRoomObj.getJSONObject("LastMessage");
                String lastMessageId = chatRoomObj.getString("message_id");
                Message lastMessage = getMessageFromJSONresponse(lastMessageJsonObj,lastMessageId );
                ChatRoom chatRoom = new ChatRoom(chatRoomId,user,lastMessage,lastMessage.getTimestamp(), unread);
                return  chatRoom;
            }
            ChatRoom chatRoom = new ChatRoom(chatRoomId,user,null,null, unread);
            return  chatRoom;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Message> getMessagesFromJSONResponse(JSONObject response){
        ArrayList<Message> messages= new ArrayList<>();
        try {
            JSONArray msgs = response.getJSONArray("messages");
            for(int i = 0; i < msgs.length(); i++) {
                try {
                    Log.d("JsonParser", response.toString());
                    JSONObject messajeJsonObj = msgs.getJSONObject(i);

                    Message message = getMessageFromJSONresponse(messajeJsonObj,"0");
                    if (message != null) {
                        messages.add(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return messages;
    }

    private static Message getMessageFromJSONresponse(JSONObject messageObj, String messageId) {
        try {
            String status = "R";
            if(messageObj.has("status")){
                status = messageObj.getString("status");
            }
            String msg = messageObj.getString("message");
            String userId = messageObj.getString("user");
            String time = messageObj.getString("time");
            Message message = new Message(messageId,msg,time,status,userId);
            return  message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

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
