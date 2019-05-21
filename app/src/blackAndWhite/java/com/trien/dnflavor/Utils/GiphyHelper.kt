package com.trien.dnflavor.Utils

import android.content.Context
import android.util.Log

import com.trien.dnflavor.Model.GifImage
import com.trien.dnflavor.R
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.core.network.api.CompletionHandler
import com.giphy.sdk.core.network.api.GPHApi
import com.giphy.sdk.core.network.api.GPHApiClient
import com.giphy.sdk.core.network.response.ListMediaResponse

import java.util.ArrayList

object GiphyHelper {

    val gifImages: MutableList<GifImage> = ArrayList()

    fun getGif(context: Context): List<GifImage> {

        val client = GPHApiClient(context.resources.getString(R.string.giphy_api))

        client.search(context.resources.getString(R.string.app_name), MediaType.gif, 24, null, null, null) { result, e ->
            if (result == null) {
                // Do what you want to do with the error
            } else {
                if (result.data != null) {

                    gifImages.clear()

                    for (gif in result.data) {

                        Log.v("giphy", gif.images.original.gifUrl)
                        gifImages.add(GifImage(gif.images.original.gifUrl))
                    }
                } else {
                    Log.e("giphy error", "No results found")
                }
            }
        }

        return gifImages
    }
}
