package com.trien.dnflavor.Model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

class GifImage : Parcelable {

    private var mUrl: String? = null

    constructor(mUrl: String) {
        this.mUrl = mUrl
    }

    /**
     * Use when reconstructing Sizzle object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    constructor(`in`: Parcel) {

        this.mUrl = `in`.readString()
    }

    fun getmUrl(): String? {
        return mUrl
    }

    fun setmUrl(mUrl: String) {
        this.mUrl = mUrl
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, i: Int) {

        dest.writeString(mUrl)
    }

    companion object {

        /**
         * This field is needed for Android to be able to
         * create new objects, individually or as arrays
         *
         * If you don’t do that, Android framework will through exception
         * Parcelable protocol requires a Parcelable.Creator object called CREATOR
         */
        @SuppressLint("ParcelCreator")
        val CREATOR: Parcelable.Creator<GifImage> = object : Parcelable.Creator<GifImage> {

            override fun createFromParcel(`in`: Parcel): GifImage {
                return GifImage(`in`)
            }

            override fun newArray(size: Int): Array<GifImage?> {
                return arrayOfNulls(size)
            }
        }
    }
}
