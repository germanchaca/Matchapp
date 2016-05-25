package fiuba.matchapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by german on 4/27/2016.
 */
public class Interest implements Serializable,Parcelable {
    String id,category, description;
    private boolean selected;

    public Interest(){

    }
    public Interest(String id, String category, String description) {
        this.id = id;
        this.category = category;
        this.description = description;
        selected = false;
    }

    protected Interest(Parcel in) {
        id = in.readString();
        category = in.readString();
        description = in.readString();
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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected() {
        return selected;
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
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
    }
}
