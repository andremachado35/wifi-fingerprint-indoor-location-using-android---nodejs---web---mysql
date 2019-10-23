package com.example.andre.aplicaocalibracao;
import android.os.Parcel;
import android.os.Parcelable;

public class ListSpaces_RV_Filho_class implements Parcelable {
    private String name;


    public ListSpaces_RV_Filho_class(Parcel in) {
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ListSpaces_RV_Filho_class(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListSpaces_RV_Filho_class> CREATOR = new Creator<ListSpaces_RV_Filho_class>() {
        @Override
        public ListSpaces_RV_Filho_class createFromParcel(Parcel in) {
            return new ListSpaces_RV_Filho_class(in);
        }

        @Override
        public ListSpaces_RV_Filho_class[] newArray(int size) {
            return new ListSpaces_RV_Filho_class[size];
        }
    };
}