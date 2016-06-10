package fiuba.matchapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by german on 4/27/2016.
 */
public class Interest implements Serializable,Parcelable {


    UserInterest interest;
    private boolean selected;

    public Interest(){
        this.interest = new UserInterest();
    }
    public Interest(String id, String category, String description) {
        this.interest = new UserInterest();
        this.interest.setCategory(category);
        this.interest.setId(id);
        this.interest.setDescription(description);
        selected = false;
    }

    protected Interest(Parcel in) {
        this.interest =in.readParcelable(UserInterest.class.getClassLoader());
        selected = in.readByte() != 0;
    }

    public static final Creator<Interest> CREATOR = new Creator<Interest>() {
        @Override
        public Interest createFromParcel(Parcel in) {
            return new Interest(in);
        }

        @Override
        public Interest[] newArray(int size) {
            return new Interest[size];
        }
    };

    public String getId() {
        return this.interest.getId();
    }

    public void setId(String id) {
        this.interest.setId(id);
    }

    public String getCategory() {
        return this.interest.getCategory();
    }

    public void setCategory(String category) {
        this.interest.setCategory(category);
    }

    public String getDescription() {
        return this.interest.getDescription();
    }

    public void setDescription(String description) {
        this.interest.setDescription(description);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected() {
        return selected;
    }

    public UserInterest getUserInterest() {
        return interest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.interest,flags);
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
    }
}
