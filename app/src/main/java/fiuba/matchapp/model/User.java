package fiuba.matchapp.model;

import java.io.Serializable;

public class User implements Serializable {
    String id, name, email, birthday, genre, fbId, latitude, longitude;

    public User() {
    }

    public User(String id, String name, String email, String birthday, String genre ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
    }
    public User(String id, String name, String email, String birthday, String genre, String longitude, String latitude ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
        this.longitude = longitude;
        this.latitude = latitude;
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
}
