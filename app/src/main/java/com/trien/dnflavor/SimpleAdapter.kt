package com.trien.dnflavor

import android.content.Context
import android.content.res.Resources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.trien.dnflavor.Model.GifImage
import com.facebook.drawee.view.SimpleDraweeView

import java.util.ArrayList

class SimpleAdapter(private val mContext: Context) : RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder>() {

    private val mGifUrlInclusiveList: MutableList<String>

    private val mGifUrlTrueList = ArrayList<String>()

    var columnCount: Int = 0
        private set

    private var mOnItemClickListener: AdapterView.OnItemClickListener? = null

    init {
        mGifUrlInclusiveList = ArrayList()
    }

    /*
     * A common adapter modification or reset mechanism. As with ListAdapter,
     * calling notifyDataSetChanged() will trigger the RecyclerView to update
     * the view. However, this method will not trigger any of the RecyclerView
     * animation features.
     */
    fun refreshAdapter(gifImages: List<GifImage>) {
        mGifUrlInclusiveList.clear()
        mGifUrlInclusiveList.addAll(generateAdapterData(gifImages))

        notifyDataSetChanged()
    }

    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemRemoved(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    fun removeItem(position: Int) {
        if (position >= mGifUrlInclusiveList.size) return

        mGifUrlInclusiveList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): VerticalItemHolder {
        val inflater = LayoutInflater.from(container.context)
        val root = inflater.inflate(R.layout.grid_item, container, false)

        val verticalItemHolder = VerticalItemHolder(root, this)

        //set child view width and height in proportion to screen size
        val childWidth = (CELL_WIDTH_HEIGHT_RATE * screenWidth).toInt()
        val params = ConstraintLayout.LayoutParams(childWidth, childWidth) // (width, height)
        verticalItemHolder.container.layoutParams = params

        return verticalItemHolder
    }

    override fun onBindViewHolder(itemHolder: VerticalItemHolder, position: Int) {

        val imageUrl = mGifUrlInclusiveList[position]

        setCellBackgroundColor(itemHolder.itemView, itemHolder.imageView, itemHolder.draweeView, imageUrl)
    }

    override fun getItemCount(): Int {
        return mGifUrlInclusiveList.size
    }

    fun setOnItemClickListener(onItemClickListener: AdapterView.OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    private fun onItemHolderClick(itemHolder: VerticalItemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(null, itemHolder.itemView,
                    itemHolder.adapterPosition, itemHolder.itemId)
        }
    }

    class VerticalItemHolder(itemView: View, private val mAdapter: SimpleAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var container: ConstraintLayout
        internal var imageView: ImageView? = null
        internal var draweeView: SimpleDraweeView

        init {
            itemView.setOnClickListener(this)
            container = itemView.findViewById<View>(R.id.itemContainer) as ConstraintLayout
            //imageView = (ImageView) itemView.findViewById(R.id.cellImg);
            draweeView = itemView.findViewById<View>(R.id.my_image_view) as SimpleDraweeView
        }

        override fun onClick(v: View) {
            mAdapter.onItemHolderClick(this)
        }
    }

    fun setCellBackgroundColor(itemView: View, imageView: ImageView?, draweeView: SimpleDraweeView, url: String) {

        if (url == PADDING) {
            draweeView.setImageResource(0)
            // imageView.setImageDrawable(null);
            itemView.tag = null
        } else if (url == MIDDLE_POINT) {

            draweeView.setImageResource(R.drawable.center)
            // imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.center));
            itemView.tag = MIDDLE_POINT
        } else {

            //draweeView.setImageURI(url);
            // imageView.setImageDrawable(null);
            Glide.with(mContext).load(url).into(draweeView)

            if (url.contains("http")) {
                itemView.tag = url
            }
        }
    }

    fun generateAdapterData(gifImages: List<GifImage>): List<String> {

        val itemCountPlusOne = gifImages.size + 1

        for (gif in gifImages) {
            gif.getmUrl()?.let { mGifUrlTrueList.add(it) }
        }

        val columnCount = (Math.ceil(Math.sqrt(itemCountPlusOne.toDouble())) + 2).toInt()
        // int columnCount = (int) (Math.ceil(Math.sqrt(gifImages.size())) + 2);
        setmColumnCount(columnCount)

        val cellCount = columnCount * columnCount

        // Create an inclusive list of string urls
        val gifUrlList = ArrayList<String>()

        for (i in 0 until cellCount) {

            gifUrlList.add(PADDING)
        }

        if ((cellCount - 1) % 2 == 0) {

            gifUrlList[(cellCount - 1) / 2] = MIDDLE_POINT
        } else {

            gifUrlList[cellCount / 2 + columnCount / 2] = MIDDLE_POINT
        }

        var counter = 0
        for (i in 0 until cellCount) {

            if (i < columnCount) {

                continue
            } else if (i >= cellCount - columnCount) {

                continue
            } else if (i % columnCount == 0) {

                continue
            } else if (i % columnCount == columnCount - 1) {

                continue
            } else if (gifUrlList[i] == MIDDLE_POINT) {

                continue
            } else {

                if (counter < gifImages.size) {

                    gifUrlList[i] = gifImages[counter].getmUrl().toString()
                    counter++
                }
            }
        }

        Log.d("triencount", columnCount.toString())

        for (aaa in gifUrlList) {

            Log.d("trieny", aaa)
        }

        Log.d("trieny2", gifUrlList.size.toString())

        return gifUrlList
    }

    fun setmColumnCount(mColumnCount: Int) {
        this.columnCount = mColumnCount
    }

    fun getmGifUrlTrueList(): ArrayList<String> {
        return mGifUrlTrueList
    }

    companion object {

        val PADDING = "padding"
        val NON_PADDING = "non_padding"
        val MIDDLE_POINT = "middle_point"
        val CELL_WIDTH_HEIGHT_RATE = 0.7f

        val screenWidth: Int
            get() = Resources.getSystem().displayMetrics.widthPixels

        val screenHeight: Int
            get() = Resources.getSystem().displayMetrics.heightPixels
    }
}
