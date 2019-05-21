package com.trien.dnflavor.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.trien.dnflavor.Model.GifImage;
import com.trien.dnflavor.R;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListMediaResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GiphyHelper {

    public static final List<GifImage> gifImages = new ArrayList<>();

    public static List<GifImage> getGif(Context context) {

        GPHApi client = new GPHApiClient(context.getResources().getString(R.string.giphy_api));

        client.trending(MediaType.gif, 22, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                if (result == null) {
                    // Do what you want to do with the error
                } else {
                    if (result.getData() != null) {

                        gifImages.clear();

                        for (Media gif : result.getData()) {

                            Log.v("giphy", gif.getImages().getOriginal().getGifUrl());
                            gifImages.add(new GifImage(gif.getImages().getOriginal().getGifUrl()));
                        }
                    } else {
                        Log.e("giphy error", "No results found");
                    }
                }
            }
        });

        return gifImages;
    }
}
