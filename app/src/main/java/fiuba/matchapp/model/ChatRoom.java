package fiuba.matchapp.model;

import java.io.Serializable;

public class ChatRoom implements Serializable {
    String id;
    String lastMessage;
    String timestamp;

    public void setName(String name) {
        this.name = name;
    }

    String name;
    int unreadCount;
    User user;

    public ChatRoom() {
    }

    public ChatRoom(String id, User user, String lastMessage, String timestamp, int unreadCount) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
