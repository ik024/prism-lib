package com.ik.cacheloading.home.model.pojo;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Represents the fields present in the API response
 */
public class UserInfo implements Parcelable{

    public User user;
    public String color;

    public class User{
        public ProfileImage profile_image;

        public class ProfileImage{
            public String small;
            public String medium;
            public String large;
        }
    }

    protected UserInfo(Parcel in) {
        color = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(color);
    }

}
