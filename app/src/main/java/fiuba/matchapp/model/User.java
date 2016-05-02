package fiuba.matchapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class User implements Serializable,Parcelable {
    String id, name, alias, email, birthday, genre, fbId, latitude, longitude;

    public User() {
    }

    public User(String id, String name, String email, String birthday, String genre) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
        this.alias = name;
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        alias = in.readString();
        email = in.readString();
        birthday = in.readString();
        genre = in.readString();
        fbId = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public User(String id, String name, String email, String birthday, String genre, String fbId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
        this.alias = name;
        this.fbId = fbId;
    }

    public User(String id, String name, String email, String birthday, String genre, String longitude, String latitude) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
        this.longitude = longitude;
        this.latitude = latitude;
        this.alias = name;
    }

    public User(String id, String name, String email, String birthday, String genre, String longitude, String latitude, String fbId, String alias) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
        this.longitude = longitude;
        this.latitude = latitude;
        this.alias = alias;
        this.fbId = fbId;
    }

    public User(String id, String name, String email, String birthday, String genre, String longitude, String latitude, String fbId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
        this.longitude = longitude;
        this.latitude = latitude;
        this.fbId = fbId;
    }

    public String getFbProfileImageUrl() {
        if (this.hasFbId()) {
            return "http://graph.facebook.com/" + fbId + "/picture?type=large";
        }
        return "";
    }

    public boolean hasFbId() {

        return fbId != null && !fbId.isEmpty();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.alias);
        dest.writeString(this.email);
        dest.writeString(this.birthday);
        dest.writeString(this.genre);
        dest.writeString(this.fbId);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
    }
}
