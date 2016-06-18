package fiuba.matchapp.model;

import java.io.Serializable;

public class Message implements Serializable {

    public static final String STATUS_UNREAD = "D";
    public static final String STATUS_READ = "R";
    public static final String STATUS_UNSENT = "U";
    String id;
    String message;
    String createdAt;
    String status;
    String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Message() {
    }

    public Message(String id, String message, String createdAt,String status, String userId) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.userId = userId;
        this.status = status;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }



}
