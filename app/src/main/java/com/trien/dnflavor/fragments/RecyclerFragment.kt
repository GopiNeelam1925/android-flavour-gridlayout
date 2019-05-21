package com.trien.dnflavor.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.FragmentActivity

import com.trien.dnflavor.MainActivity
import com.trien.dnflavor.Model.GifImage
import com.trien.dnflavor.R
import com.trien.dnflavor.SimpleAdapter
import com.stfalcon.frescoimageviewer.ImageViewer
import java.util.Objects
import java.util.Timer
import java.util.TimerTask

import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING
import com.trien.dnflavor.MainActivity.Companion.GIF_IMAGES
import com.trien.dnflavor.SimpleAdapter.Companion.screenHeight
import com.trien.dnflavor.SimpleAdapter.Companion.screenWidth

abstract class RecyclerFragment : Fragment(), AdapterView.OnItemClickListener {

    // Handler onjects for timing purposes
    internal lateinit var handler: Handler
    internal var stopHandler = false

    // a timer to schedule zooming
    internal var timer = Timer()

    // recycler view and adapter
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SimpleAdapter? = null

    // an auxiliary variable to record previously clicked child view
    internal var mPreviousView: View? = null

    // list of gif images retrieved from server
    internal var gifImages: List<GifImage>? = null

    protected abstract val itemDecoration: RecyclerView.ItemDecoration

    protected abstract val adapter: SimpleAdapter

    protected abstract fun getLayoutManager(columnCount: Int): RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // fetch the list of gif images url passed from main activity
        val bundle = this.arguments
        if (bundle != null) {
            gifImages = bundle.getParcelableArrayList(GIF_IMAGES)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_recycler, container, false)

        handler = Handler()

        // set up adapter
        mAdapter = adapter
        mAdapter!!.refreshAdapter(gifImages!!)
        mAdapter!!.setOnItemClickListener(this)

        // set up recycler view
        mRecyclerView = rootView.findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(false)

        // set up layout manager
        val layoutManager = getLayoutManager(mAdapter!!.columnCount)
        mRecyclerView!!.layoutManager = layoutManager
        mRecyclerView!!.addItemDecoration(itemDecoration)
        mRecyclerView!!.adapter = mAdapter

        // scroll to center of the recycler view
        scrollToCenter()

        return rootView
    }

    /* clear On Scroll Listener for the recycler view */
    private fun clearOnScrollListener() {
        mRecyclerView!!.clearOnScrollListeners()
    }

    /* add On Scroll Listener for the recycler view */
    private fun addOnScrollListener() {
        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // if Scroll State = idle, and if the child view cutting the center point of the screen,
                // then smooth scroll to the child view
                if (newState == SCROLL_STATE_IDLE) {

                    for (i in 0 until recyclerView.childCount) {
                        val child = recyclerView.getChildAt(i)
                        val childX = child.x
                        val childY = child.y

                        if (childX + child.width > screenWidth / 2
                                && childX < screenWidth / 2
                                && childY + child.height > screenHeight / 2
                                && childY < screenHeight / 2
                                && !stopHandler) {

                            recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(child))
                        }
                    }
                }

                // if state = settling,
                if (newState == SCROLL_STATE_SETTLING) {
                    for (i in 0 until recyclerView.childCount) {
                        val child = recyclerView.getChildAt(i)
                        val childX = child.x
                        val childY = child.y

                        if (childX + child.width > screenWidth / 2
                                && childX < screenWidth / 2
                                && childY + child.height > screenHeight / 2
                                && childY < screenHeight / 2
                                && !stopHandler) {

                            if (child.tag == null) {

                                val leftChild = recyclerView.getChildAt(i - 1)
                                val rightChild = recyclerView.getChildAt(i + 1)
                                val topChild = recyclerView.getChildAt(i - 3) // 3 is the number of view holders on a horizontal line of the screen
                                val botChild = recyclerView.getChildAt(i + 3) // 3 is the number of view holders on a horizontal line of the screen

                                val paddingChildPosition = recyclerView.getChildAdapterPosition(child)
                                val leftChildPosition = recyclerView.getChildAdapterPosition(leftChild!!)
                                val rightChildPosition = recyclerView.getChildAdapterPosition(rightChild!!)

                                if (leftChild?.tag != null && leftChildPosition == paddingChildPosition - 1) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(leftChild))

                                } else if (rightChild?.tag != null && rightChildPosition == paddingChildPosition + 1) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(rightChild))

                                } else if (topChild?.tag != null) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(topChild))
                                } else if (botChild?.tag != null) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(botChild))
                                }
                            } else {

                                recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(child))
                            }
                        }
                    }
                }

                if (newState == SCROLL_STATE_DRAGGING) {

                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!stopHandler) {
                    for (i in 0 until recyclerView.childCount) {
                        val child = recyclerView.getChildAt(i)
                        val childX = child.x
                        val childY = child.y

                        if (childX + child.width > screenWidth / 2
                                && childX < screenWidth / 2
                                && childY + child.height > screenHeight / 2
                                && childY < screenHeight / 2) {

                            if (child.scaleX == 1f && child.tag != null && child !== mPreviousView && count == 0) {
                                mPreviousView = child

                                zoomInChild(child)

                                count = 0
                            }
                        } else if (child.scaleX == SCALE) {

                            zoomOutChild(child)
                        } else if (child.tag != null) {
                            mPreviousView = null
                        }
                    }
                }
            }
        })
    }

    /* scroll to center of recycler view */
    private fun scrollToCenter() {

        var moveStep = 0
        var delayMilis = 0

        val scrollingTimes: Int
        val columnCount = mAdapter!!.columnCount
        if (columnCount % 2 == 0) {
            scrollingTimes = columnCount / 2
        } else {
            scrollingTimes = (columnCount - 1) / 2
        }

        for (i in 1..scrollingTimes) {

            moveStep = moveStep + columnCount + 1
            delayMilis = delayMilis + 1000
            val finalMoveStep = moveStep

            Handler().postDelayed({ mRecyclerView!!.smoothScrollToPosition(finalMoveStep) }, delayMilis.toLong())

            if (i == scrollingTimes) {

                Handler().postDelayed({
                    val child = mRecyclerView!!.getChildAt(4)
                    child.animate().z(Z_VALUE).duration = 1
                    child.animate().scaleX(SCALE).duration = 1
                    child.animate().scaleY(SCALE).setDuration(1).setListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {
                            val activity = activity as MainActivity?
                            if (activity != null) {
                                activity.hideLoadingIndicator()
                                activity.showRecyclerView()
                            }
                            addOnScrollListener()
                        }
                    })
                }, (delayMilis + 1000).toLong())
            }
        }
    }

    /* override on child click method */
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        if (view.tag != null) {
            itemOnClickListener(view, position)
        }
    }

    /* helper method for item click */
    private fun itemOnClickListener(view: View, position: Int) {

        //        stopHandler = true;
        clearOnScrollListener()

        for (i in 0 until mRecyclerView!!.childCount) {

            val child = mRecyclerView!!.getChildAt(i)

            if (child === view) {

                if (child.scaleX == 1f) {

                    // scroll to the child view at the selected position at the center of screen
                    mRecyclerView!!.smoothScrollToPosition(position)
                    mPreviousView = child
                    child.animate().z(Z_VALUE).duration = SCALE_DURATION
                    child.animate().scaleX(SCALE).duration = SCALE_DURATION
                    child.animate().scaleY(SCALE).setDuration(SCALE_DURATION).setListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(animation: Animator) {

                            //stopHandler = false;
                            addOnScrollListener()
                        }

                    })
                } else {

                    //stopHandler = false;
                    val childImgUrl = child.tag.toString()

                    if (childImgUrl.contains("http")) {
                        /* new ImageViewer.Builder<>(getContext(), getPosters())
                                .setStartPosition(1)
                                .show();*/
                        ImageViewer.Builder(activity, mAdapter!!.getmGifUrlTrueList())
                                .setStartPosition(mAdapter!!.getmGifUrlTrueList().indexOf(childImgUrl))
                                .show()


                    }


                    addOnScrollListener()
                    return
                }
            } else if (child.scaleX != 1f) {

                child.animate().scaleX(1f).duration = SCALE_DURATION
                child.animate().scaleY(1f).duration = SCALE_DURATION
                child.animate().z(0f).duration = SCALE_DURATION
            }
        }
    }

    /* zoom in a child when it crossing the center of the screen */
    private fun zoomInChild(child: View) {

        //createRecursiveZoomIn(child, SCALE_STEP_COUNT, SCALE);
        for (i in 1..SCALE_STEP_COUNT) {

            createHandlerForZoomIn(child, SCALE_STEP_COUNT, SCALE, i)
        }
    }

    /* helper method for zooming in child using handlers*/
    private fun createHandlerForZoomIn(child: View, zoomStepsCount: Int, scale: Float, i: Int) {

        val delayMilis = SCALE_DURATION * i / zoomStepsCount

        Handler().postDelayed(object : Runnable {

            internal var zoomStepInterval = (scale - 1) * i / zoomStepsCount
            internal var zValue = (Z_VALUE * i / zoomStepsCount).toInt()

            override fun run() {

                child.scaleX = 1 + zoomStepInterval
                child.scaleY = 1 + zoomStepInterval
                ViewCompat.setZ(child, zValue.toFloat())
            }
        }, delayMilis)
    }

    /* helper method for zooming in child using recursive way*/
    private fun createRecursiveZoomIn(child: View, zoomStepsCount: Int, scale: Float) {
        count++
        if (count <= zoomStepsCount) {
            val delayMilis = SCALE_DURATION * count / zoomStepsCount

            Handler().postDelayed(object : Runnable {

                internal var zoomStepInterval = (scale - 1) * count / zoomStepsCount
                internal var zValue = (Z_VALUE * count / zoomStepsCount).toInt()

                override fun run() {

                    child.scaleX = 1 + zoomStepInterval
                    child.scaleY = 1 + zoomStepInterval
                    ViewCompat.setZ(child, zValue.toFloat())
                }
            }, delayMilis)
            createRecursiveZoomIn(child, zoomStepsCount, scale)
        }
    }

    /* zoom out a child when it is not crossing the center of the screen */
    private fun zoomOutChild(child: View) {

        var delayMilisAccumulated: Long = 1
        for (i in SCALE_STEP_COUNT - 1 downTo 0) {

            // createHandlerForZoomOut(child, SCALE_STEP_COUNT, SCALE, i, delayMilisAccumulated);
            createTimerForZoomOut(child, SCALE_STEP_COUNT, SCALE, i, delayMilisAccumulated)
            delayMilisAccumulated++
        }
    }

    /* helper method for zooming out child using handlers*/
    private fun createHandlerForZoomOut(child: View, zoomStepsCount: Int, scale: Float, i: Int, delayMilisAccumulated: Long) {

        val delayMilis = SCALE_DURATION * delayMilisAccumulated / zoomStepsCount

        Handler().postDelayed(object : Runnable {

            internal var zoomStepInterval = (scale - 1) * i / zoomStepsCount
            internal var zValue = (Z_VALUE * i / zoomStepsCount).toInt()

            override fun run() {

                child.scaleX = 1 + zoomStepInterval
                child.scaleY = 1 + zoomStepInterval
                ViewCompat.setZ(child, zValue.toFloat())
            }
        }, delayMilis)
    }

    /* helper method for zooming out child using timer*/
    private fun createTimerForZoomOut(child: View, zoomStepsCount: Int, scale: Float, i: Int, delayMilisAccumulated: Long) {

        val delayMilis = SCALE_DURATION * delayMilisAccumulated / zoomStepsCount

        val timerTask = object : TimerTask() {

            internal var zoomStepInterval = (scale - 1) * i / zoomStepsCount
            internal var zValue = (Z_VALUE * i / zoomStepsCount).toInt()

            override fun run() {

                Objects.requireNonNull<FragmentActivity>(activity).runOnUiThread(Runnable {
                    child.scaleX = 1 + zoomStepInterval
                    child.scaleY = 1 + zoomStepInterval
                    ViewCompat.setZ(child, zValue.toFloat())
                })
            }
        }

        timer.schedule(timerTask, delayMilis)
    }

    companion object {
        private val POSTERS_PATH = "https://media3.giphy.com/media/etQ6w6sKCtQiq7YCbF/giphy.gif"
        // constant values
        val SCALE = 1.1f
        val Z_VALUE = 100f
        val SCALE_DURATION = 500L
        val SCALE_STEP_COUNT = 100

        // an auxiliary variable for the recursive method createRecursiveZoomIn()
        internal var count = 0


        val posters: Array<String>
            get() = arrayOf(POSTERS_PATH, POSTERS_PATH, POSTERS_PATH, POSTERS_PATH, POSTERS_PATH, POSTERS_PATH, POSTERS_PATH, POSTERS_PATH, POSTERS_PATH)
    }
}
