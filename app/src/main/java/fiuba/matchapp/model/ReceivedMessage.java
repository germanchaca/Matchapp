package fiuba.matchapp.model;

import android.text.TextUtils;

import java.io.Serializable;

public class ReceivedMessage extends  Message implements Serializable {


    public ReceivedMessage(String id, String message, String timestamp) {
        super(id,message,timestamp);
        super.setMine(false);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }



}
