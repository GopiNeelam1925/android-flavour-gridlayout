package com.trien.dnflavor.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.recyclerview.widget.RecyclerView;
import com.trien.dnflavor.FixedGridLayoutManager;
import com.trien.dnflavor.Model.GifImage;
import com.trien.dnflavor.Utils.InsetDecoration;
import com.trien.dnflavor.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.trien.dnflavor.MainActivity.GIF_IMAGES;

/* the Fixed Two Way Fragment class that allows freely scrolling in any direction in a 2D space*/
public class FixedTwoWayFragment extends RecyclerFragment{

    public static FixedTwoWayFragment newInstance(List<GifImage> gifImages) {
        FixedTwoWayFragment fragment = new FixedTwoWayFragment();

        // create a bundle to pass in the array of Gif Images objects
        Bundle args = new Bundle();
        args.putParcelableArrayList(GIF_IMAGES, (ArrayList<? extends Parcelable>) gifImages);
        fragment.setArguments(args);
        return fragment;
    }

    /** @param columnCount pass in a fixed number of column as desired/calculated*/
    @Override
    protected RecyclerView.LayoutManager getLayoutManager(int columnCount) {
        FixedGridLayoutManager manager = new FixedGridLayoutManager(getContext());
        manager.setTotalColumnCount(columnCount);

        return manager;
    }

    /** add decoration for the recycler view*/
    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new InsetDecoration(getActivity());
    }

    @Override
    protected SimpleAdapter getAdapter() {
        return new SimpleAdapter(getContext());
    }
}
