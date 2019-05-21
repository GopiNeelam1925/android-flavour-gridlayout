package com.trien.dnflavor;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trien.dnflavor.Model.GifImage;
import com.trien.dnflavor.Utils.GifLoader;
import com.trien.dnflavor.fragments.FixedTwoWayFragment;

import java.util.ArrayList;
import java.util.List;

import static com.trien.dnflavor.Utils.GiphyHelper.gifImages;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<GifImage>> {

    // Constant value for the earthquake loader ID which can be any number
    private static final int GIPHY_LOADER_ID = 1;

    // Constant value for GifImages array list
    public static final String GIF_IMAGES = "gif images";

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    // Container for recycler view fragment
    private FrameLayout mContainer;

    List<GifImage> mGifImages;

    // loading indicator
    View mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // making notification bar transparent
        changeStatusBarColor();

        mContainer = findViewById(R.id.container);
        mContainer.setVisibility(View.INVISIBLE);

        mGifImages = new ArrayList<>();

        mLoadingIndicator = findViewById(R.id.loadingIndicator);
        mEmptyStateTextView = findViewById(R.id.emptyTv);

        checkNetworkStatus();
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void checkNetworkStatus() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(GIPHY_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mLoadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<GifImage>> onCreateLoader(int id, Bundle args) {

        return new GifLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<GifImage>> loader, final List<GifImage> gifImages) {

        // If there is a valid list of {@link GifImage}s, then pass them to the recycler view fragment
        // This will trigger the RecyclerView to update.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (gifImages == null || gifImages.isEmpty()) {

                    tryAgain();
                }

                else {

                    // set up views visibility
                    mEmptyStateTextView.setText("");

                    mGifImages.clear();
                    mGifImages.addAll(gifImages);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.container, FixedTwoWayFragment.newInstance(mGifImages));
                    ft.commitAllowingStateLoss();

                    Log.v("trienff", String.valueOf(gifImages.size()));
                    Log.v("trienff2", String.valueOf(mGifImages.size()));
                }
            }
        }, 2000);
    }

    public void tryAgain() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (!gifImages.isEmpty()) {

                    // set up views visibility
                    mEmptyStateTextView.setText("");

                    mGifImages.clear();
                    mGifImages.addAll(gifImages);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.container, FixedTwoWayFragment.newInstance(mGifImages));
                    ft.commitAllowingStateLoss();

                    Log.v("trienff", String.valueOf(gifImages.size()));
                    Log.v("trienff2", String.valueOf(mGifImages.size()));
                } else {
                    // Hide loading indicator
                    mLoadingIndicator.setVisibility(View.GONE);
                    // Set empty state text to display "No mGifImages found."
                    mEmptyStateTextView.setText(R.string.no_gif_found);
                }
            }
        }, 2000);
    }

    @Override
    public void onLoaderReset(Loader<List<GifImage>> loader) {

        mGifImages.clear();
    }

    public void showRecyclerView() {
        mContainer.setVisibility(View.VISIBLE);
    }

    public void hideLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.GONE);
    }
}
