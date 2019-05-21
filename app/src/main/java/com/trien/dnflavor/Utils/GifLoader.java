package com.trien.dnflavor.Utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;

import com.trien.dnflavor.Model.GifImage;

import java.util.List;

/**
 * Loads a list of sizzles by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class GifLoader extends AsyncTaskLoader<List<GifImage>> {

    /** Tag for log messages */
    private static final String LOG_TAG = GifLoader.class.getName();

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    private List<GifImage> mGifImages;
    /**
     * Constructs a new {@link GifLoader}.
     *
     * @param context of the activity
     */
    public GifLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<GifImage> loadInBackground() {

        // Perform the network request, parse the response, and extract a list of gif images.
        List<GifImage> gifImages = GiphyHelper.getGif(getContext());

        return gifImages;
    }
}
