package fiuba.matchapp.model;

import java.io.Serializable;

public abstract class Message implements Serializable {

    String id;
    String message;
    String timestamp;

    public boolean isMine() {
        return mine;
    }

    protected void setMine(boolean mine) {
        this.mine = mine;
    }

    boolean mine;

    public Message(String id, String message, String timestamp){
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
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
