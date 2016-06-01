package fiuba.matchapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by german on 4/27/2016.
 */
public class UserInterest implements Serializable,Parcelable {
    String id,category, description;

    public UserInterest(){

    }
    public UserInterest(String id, String category, String description) {
        this.id = id;
        this.category = category;
        this.description = description;
    }

    protected UserInterest(Parcel in) {
        id = in.readString();
        category = in.readString();
        description = in.readString();
    }

    public static final Creator<UserInterest> CREATOR = new Creator<UserInterest>() {
        @Override
        public UserInterest createFromParcel(Parcel in) {
            return new UserInterest(in);
        }

        @Override
        public UserInterest[] newArray(int size) {
            return new UserInterest[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.category);
        dest.writeString(this.description);
    }
}
