package com.trien.dnflavor;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trien.dnflavor.Model.GifImage;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {

    public static final String PADDING = "padding";
    public static final String NON_PADDING = "non_padding";
    public static final String MIDDLE_POINT = "middle_point";
    public static final float CELL_WIDTH_HEIGHT_RATE = 0.7f;

    private List<String> mGifUrlInclusiveList;

    private ArrayList<String> mGifUrlTrueList = new ArrayList<>();

    private Context mContext;

    private int mColumnCount;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public SimpleAdapter(Context context) {
        mGifUrlInclusiveList = new ArrayList<>();
        mContext = context;
    }

    /*
     * A common adapter modification or reset mechanism. As with ListAdapter,
     * calling notifyDataSetChanged() will trigger the RecyclerView to update
     * the view. However, this method will not trigger any of the RecyclerView
     * animation features.
     */
    public void refreshAdapter(List<GifImage> gifImages) {
        mGifUrlInclusiveList.clear();
        mGifUrlInclusiveList.addAll(generateAdapterData(gifImages));

        notifyDataSetChanged();
    }

    /*
     * Inserting a new item at the head of the list. This uses a specialized
     * RecyclerView method, notifyItemRemoved(), to trigger any enabled item
     * animations in addition to updating the view.
     */
    public void removeItem(int position) {
        if (position >= mGifUrlInclusiveList.size()) return;

        mGifUrlInclusiveList.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public VerticalItemHolder onCreateViewHolder(@NonNull ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.grid_item, container, false);

        VerticalItemHolder verticalItemHolder = new VerticalItemHolder(root, this);

        //set child view width and height in proportion to screen size
        int childWidth = (int) (CELL_WIDTH_HEIGHT_RATE * getScreenWidth());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(childWidth, childWidth); // (width, height)
        verticalItemHolder.container.setLayoutParams(params);

        return verticalItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalItemHolder itemHolder, int position) {

        String imageUrl = mGifUrlInclusiveList.get(position);

        setCellBackgroundColor(itemHolder.itemView, itemHolder.imageView, itemHolder.draweeView, imageUrl);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;

    }

    @Override
    public int getItemCount() {
        return mGifUrlInclusiveList.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(VerticalItemHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public static class VerticalItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private SimpleAdapter mAdapter;

        ConstraintLayout container;
        ImageView imageView;
        SimpleDraweeView draweeView;

        public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);

            mAdapter = adapter;
            container = (ConstraintLayout) itemView.findViewById(R.id.itemContainer);
            //imageView = (ImageView) itemView.findViewById(R.id.cellImg);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.my_image_view);
        }

        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }
    }

    public void setCellBackgroundColor(View itemView, ImageView imageView, SimpleDraweeView draweeView, String url) {

        if (url.equals(PADDING)) {
            draweeView.setImageResource(0);
            // imageView.setImageDrawable(null);
            itemView.setTag(null);
        } else if (url.equals(MIDDLE_POINT)) {

            draweeView.setImageResource(R.drawable.center);
            // imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.center));
            itemView.setTag(MIDDLE_POINT);
        } else {

            //draweeView.setImageURI(url);
            // imageView.setImageDrawable(null);
            Glide.with(mContext).load(url).into(draweeView);

            if (url.contains("http")) {
                itemView.setTag(url);
            }
        }
    }

    public List<String> generateAdapterData(List<GifImage> gifImages) {

        int itemCountPlusOne = gifImages.size() + 1;

        for (GifImage gif : gifImages) {
            mGifUrlTrueList.add(gif.getmUrl());
        }

        int columnCount = (int) (Math.ceil(Math.sqrt(itemCountPlusOne)) + 2);
        // int columnCount = (int) (Math.ceil(Math.sqrt(gifImages.size())) + 2);
        setmColumnCount(columnCount);

        int cellCount = columnCount * columnCount;

        // Create an inclusive list of string urls
        List<String> gifUrlList = new ArrayList<>();

        for (int i = 0; i < cellCount; i++) {

            gifUrlList.add(PADDING);
        }

        if ((cellCount - 1) % 2 == 0) {

            gifUrlList.set((cellCount - 1) / 2, MIDDLE_POINT);
        } else {

            gifUrlList.set(cellCount / 2 + columnCount / 2, MIDDLE_POINT);
        }

        int counter = 0;
        for (int i = 0; i < cellCount; i++) {

            if (i < columnCount) {

                continue;
            } else if (i >= cellCount - columnCount) {

                continue;
            } else if (i % columnCount == 0) {

                continue;
            } else if (i % columnCount == columnCount - 1) {

                continue;
            } else if (gifUrlList.get(i).equals(MIDDLE_POINT)) {

                continue;
            } else {

                if (counter < gifImages.size()) {

                    gifUrlList.set(i, gifImages.get(counter).getmUrl());
                    counter++;
                }
            }
        }

        Log.d("triencount", String.valueOf(columnCount));

        for (String aaa : gifUrlList) {

            Log.d("trieny", aaa);
        }

        Log.d("trieny2", String.valueOf(gifUrlList.size()));

        return gifUrlList;
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public void setmColumnCount(int mColumnCount) {
        this.mColumnCount = mColumnCount;
    }

    public ArrayList<String> getmGifUrlTrueList() {
        return mGifUrlTrueList;
    }
}
