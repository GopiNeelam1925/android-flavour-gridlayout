package com.trien.dnflavor.Utils

import android.content.AsyncTaskLoader
import android.content.Context
import android.content.SharedPreferences

import com.trien.dnflavor.Model.GifImage

/**
 * Loads a list of sizzles by using an AsyncTask to perform the
 * network request to the given URL.
 */

class GifLoader
/**
 * Constructs a new [GifLoader].
 *
 * @param context of the activity
 */
(context: Context) : AsyncTaskLoader<List<GifImage>>(context) {

    private val sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    private val mGifImages: List<GifImage>? = null

    override fun onStartLoading() {
        forceLoad()
    }

    /**
     * This is on a background thread.
     */
    override fun loadInBackground(): List<GifImage> {

        // Perform the network request, parse the response, and extract a list of gif images.

        return GiphyHelper.getGif(context)
    }

    companion object {

        /** Tag for log messages  */
        private val LOG_TAG = GifLoader::class.java.name
    }
}
