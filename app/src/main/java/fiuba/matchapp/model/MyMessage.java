package fiuba.matchapp.model;

import android.text.TextUtils;

import java.io.Serializable;

public class MyMessage extends  Message implements Serializable {

    public static final String STATUS_SENT = "D";
    public static final String STATUS_READ = "R";
    public static final String STATUS_UNSENT = "U";
    public static final String STATUS_ERROR = "E";
    public static final String SENT_ID = "-1";

    String status;

    public int getPositionInAdapter() {
        return positionInAdapter;
    }

    public void setPositionInAdapter(int positionInAdapter) {
        this.positionInAdapter = positionInAdapter;
    }

    int positionInAdapter;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public MyMessage(String id, String message, String timestamp, String status) {
        super(id,message,timestamp);
        this.status = status;
        super.setMine(true);
    }
    public MyMessage(String message, String timestamp) {
        this(SENT_ID,message,timestamp,STATUS_UNSENT);
    }

    public void setStatusSent(){
        this.status = STATUS_SENT;
    }

    public void setStatusRead(){
        this.status = STATUS_READ;
    }
    public void setStatusError() {
        this.status = STATUS_ERROR;
    }

    public boolean isSent(){
        return (TextUtils.equals(this.status,STATUS_SENT));
    }
    public boolean isUnSent(){
        return (TextUtils.equals(this.status,STATUS_UNSENT));
    }
    public boolean wasRead(){
        return (TextUtils.equals(this.status,STATUS_READ));
    }
    public boolean hasError(){
        return (TextUtils.equals(this.status,STATUS_ERROR));
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
