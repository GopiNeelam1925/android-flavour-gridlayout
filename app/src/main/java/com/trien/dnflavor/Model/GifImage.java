package com.trien.dnflavor.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class GifImage implements Parcelable {

    private String mUrl;

    public GifImage(String mUrl) {
        this.mUrl = mUrl;
    }

    /**
     * Use when reconstructing Sizzle object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    public GifImage(Parcel in) {

        this.mUrl = in.readString();
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {

        dest.writeString(mUrl);
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     *
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<GifImage> CREATOR = new Parcelable.Creator<GifImage>() {

        public GifImage createFromParcel(Parcel in) {
            return new GifImage(in);
        }

        public GifImage[] newArray(int size) {
            return new GifImage[size];
        }
    };
}
