package com.trien.dnflavor

import android.app.LoaderManager
import android.content.Context
import android.content.Loader
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView

import com.trien.dnflavor.Model.GifImage
import com.trien.dnflavor.Utils.GifLoader
import com.trien.dnflavor.fragments.FixedTwoWayFragment

import java.util.ArrayList

import com.trien.dnflavor.Utils.GiphyHelper.gifImages
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<GifImage>> {

    private lateinit var mGifImages: MutableList<GifImage>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        // making notification bar transparent
        changeStatusBarColor()

        // Container for recycler view fragment
        container!!.visibility = View.INVISIBLE

        mGifImages = ArrayList()

        checkNetworkStatus()
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun checkNetworkStatus() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Get details on the currently active default data network
        val networkInfo = connMgr.activeNetworkInfo

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            val loaderManager = loaderManager
            loaderManager.initLoader(GIPHY_LOADER_ID, null, this)
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator.visibility = View.GONE
            // Update empty state with no connection error message
            emptyTv!!.setText(R.string.no_internet_connection)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<List<GifImage>> {

        return GifLoader(this)
    }

    override fun onLoadFinished(loader: Loader<List<GifImage>>, gifImages: List<GifImage>?) {

        // If there is a valid list of {@link GifImage}s, then pass them to the recycler view fragment
        // This will trigger the RecyclerView to update.
        val handler = Handler()
        handler.postDelayed({
            if (gifImages == null || gifImages.isEmpty()) {

                tryAgain()
            } else {

                // set up views visibility
                emptyTv!!.text = ""

                mGifImages.clear()
                mGifImages.addAll(gifImages)

                val fragmentManager = supportFragmentManager
                val ft = fragmentManager.beginTransaction()
                ft.replace(R.id.container, FixedTwoWayFragment.newInstance(mGifImages))
                ft.commitAllowingStateLoss()

                Log.v("trienff", gifImages.size.toString())
                Log.v("trienff2", mGifImages.size.toString())
            }
        }, 2000) // 2 seconds
    }

    fun tryAgain() {

        val handler = Handler()
        handler.postDelayed({
            if (!gifImages.isEmpty()) {

                // set up views visibility
                emptyTv!!.text = ""

                mGifImages.clear()
                mGifImages.addAll(gifImages)

                val fragmentManager = supportFragmentManager
                val ft = fragmentManager.beginTransaction()
                ft.replace(R.id.container, FixedTwoWayFragment.newInstance(mGifImages))
                ft.commitAllowingStateLoss()

                Log.v("trienff", gifImages.size.toString())
                Log.v("trienff2", mGifImages.size.toString())
            } else {
                // Hide loading indicator
                loadingIndicator.visibility = View.GONE
                // Set empty state text to display "No mGifImages found."
                emptyTv!!.setText(R.string.no_gif_found)
            }
        }, 10000) // 10 seconds
    }

    override fun onLoaderReset(loader: Loader<List<GifImage>>) {

        mGifImages.clear()
    }

    fun showRecyclerView() {
        container!!.visibility = View.VISIBLE
    }

    fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
    }

    companion object {

        // Constant value for the earthquake loader ID which can be any number
        private val GIPHY_LOADER_ID = 1

        // Constant value for GifImages array list
        val GIF_IMAGES = "gif images"
    }
}
