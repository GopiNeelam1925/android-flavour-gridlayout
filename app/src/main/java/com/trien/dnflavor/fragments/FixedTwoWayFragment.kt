package com.trien.dnflavor.fragments

import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.trien.dnflavor.FixedGridLayoutManager
import com.trien.dnflavor.MainActivity.Companion.GIF_IMAGES
import com.trien.dnflavor.Model.GifImage
import com.trien.dnflavor.Utils.InsetDecoration
import com.trien.dnflavor.SimpleAdapter

import java.util.ArrayList

/* the Fixed Two Way Fragment class that allows freely scrolling in any direction in a 2D space*/
class FixedTwoWayFragment : RecyclerFragment() {
    override val adapter: SimpleAdapter = SimpleAdapter(context!!)
    override val itemDecoration = InsetDecoration(activity!!)

    /** @param columnCount pass in a fixed number of column as desired/calculated
     */
    override fun getLayoutManager(columnCount: Int): RecyclerView.LayoutManager {
        val manager = context?.let { FixedGridLayoutManager(it) }
        if (manager != null) {
            manager.setTotalColumnCount(columnCount)
        }

        return manager!!
    }

    companion object {

        fun newInstance(gifImages: List<GifImage>): FixedTwoWayFragment {
            val fragment = FixedTwoWayFragment()

            // create a bundle to pass in the array of Gif Images objects
            val args = Bundle()
            args.putParcelableArrayList(GIF_IMAGES, gifImages as ArrayList<out Parcelable>)
            fragment.arguments = args
            return fragment
        }
    }
}
