package fiuba.matchapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ChatRoom implements Serializable,Parcelable {
    String id;
    Message lastMessage;
    int unreadCount;
    User otherUser;

    public String getOlderShownMsgId() {
        return olderShownMsgId;
    }

    public void setOlderShownMsgId(String olderShownMsgId) {
        this.olderShownMsgId = olderShownMsgId;
    }

    String  olderShownMsgId;


    protected ChatRoom(Parcel in) {
        id = in.readString();
        lastMessage = (Message) in.readSerializable();
        unreadCount = in.readInt();
        otherUser = in.readParcelable(User.class.getClassLoader());
        olderShownMsgId = in.readString();
    }




    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

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
        if(lastMessage != null){
            this.olderShownMsgId = lastMessage.getId();
        }else {
            this.olderShownMsgId = "0";
        }
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
        if( Integer.parseInt(this.getOlderShownMsgId()) > 0){
            return true;
        }
        return false;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeSerializable(lastMessage);
        dest.writeInt(unreadCount);
        dest.writeParcelable(otherUser, flags);
        dest.writeString(olderShownMsgId);
    }
}
