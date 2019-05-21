package com.trien.dnflavor.Utils

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

import com.trien.dnflavor.R

/**
 * ItemDecoration implementation that applies an inset margin
 * around each child of the RecyclerView. The inset value is controlled
 * by a dimension resource.
 */
class InsetDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val mInsets: Int

    init {
        mInsets = context.resources.getDimensionPixelSize(R.dimen.card_insets)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        //We can supply forced insets for each item view here in the Rect
        outRect.set(mInsets, mInsets, mInsets, mInsets)

    }
}
