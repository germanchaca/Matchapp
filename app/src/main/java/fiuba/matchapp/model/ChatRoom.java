package fiuba.matchapp.model;

import java.io.Serializable;

public class ChatRoom implements Serializable {
    String id;
    Message lastMessage;
    int unreadCount;
    User otherUser;


    public User getOtherUser() {
        return otherUser;
    }

    public void setOtheUser(User otherUser) {
        this.otherUser = otherUser;
    }


    public ChatRoom(String id, User otherUser){
        this(id,otherUser,0);
    }
    public ChatRoom(String id, User otherUser, int unreadCount) {
        this(id,otherUser,unreadCount,null);
    }
    public ChatRoom(String id, User otherUser, int unreadCount,Message lastMessage) {
        this.id = id;
        this.otherUser = otherUser;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean hasMessages(){
        if(this.getLastMessage() == null){
            return false;
        }
        return true;
    }

    public boolean hasOlderMessages(){
        if(this.hasMessages()){
            if( Integer.parseInt(this.getLastMessage().getId()) > 0){
                return true;
            }
        }
        return false;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
