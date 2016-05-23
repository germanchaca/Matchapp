package fiuba.matchapp.model;

import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import fiuba.matchapp.app.MyApplication;

public class User implements Serializable, Parcelable {
    String id;
    String name;
    String alias;
    String email;
    String birthday;
    String genre;
    String fbId;
    String latitude;
    String longitude;
    String photoProfile;

    List<Interest> interests;

    public User() {
    }

    public User(String id, String name, String alias, String email, String birthday, String genre) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.genre = genre;
        this.alias = alias;
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

    public String getFbProfileImageUrl() {
        if (this.hasFbId()) {
            return "http://graph.facebook.com/" + fbId + "/picture?type=large";
        }
        return "";
    }

    public boolean hasFbId() {
        return fbId != null && !fbId.isEmpty();
    }

    public boolean hasLatitude() {
        return latitude != null && !latitude.isEmpty();
    }

    public boolean hasLongitude() {
        return longitude != null && !longitude.isEmpty();
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

    public String getPhotoProfile() {
        return photoProfile;
    }
    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
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
        dest.writeString(this.photoProfile);
        dest.writeTypedList(this.interests);
    }

    public String getParsedAddress() {
        String parsedAddress = "";
        if (!hasLatitude() || !hasLongitude()) {
            return parsedAddress;
        }
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String country = addresses.get(0).getCountryName();
            String city = addresses.get(0).getLocality();
            parsedAddress = city + ", " + country;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsedAddress;
    }
}
