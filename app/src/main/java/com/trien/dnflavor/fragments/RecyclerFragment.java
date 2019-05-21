package com.trien.dnflavor.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.trien.dnflavor.MainActivity;
import com.trien.dnflavor.Model.GifImage;
import com.trien.dnflavor.R;
import com.trien.dnflavor.SimpleAdapter;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.trien.dnflavor.MainActivity.GIF_IMAGES;
import static com.trien.dnflavor.SimpleAdapter.getScreenHeight;
import static com.trien.dnflavor.SimpleAdapter.getScreenWidth;

public abstract class RecyclerFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String POSTERS_PATH = "https://media3.giphy.com/media/etQ6w6sKCtQiq7YCbF/giphy.gif";
    // constant values
    public static final float SCALE = 1.1f;
    public static final float Z_VALUE = 100;
    public static final Long SCALE_DURATION = 500L;
    public static final int SCALE_STEP_COUNT = 100;

    // Handler onjects for timing purposes
    Handler handler;
    boolean stopHandler = false;

    // a timer to schedule zooming
    Timer timer = new Timer();

    // recycler view and adapter
    private RecyclerView mRecyclerView;
    private SimpleAdapter mAdapter;

    // an auxiliary variable for the recursive method createRecursiveZoomIn()
    static int count = 0;

    // an auxiliary variable to record previously clicked child view
    View mPreviousView = null;

    // list of gif images retrieved from server
    List<GifImage> gifImages;

    protected abstract RecyclerView.LayoutManager getLayoutManager(int columnCount);

    protected abstract RecyclerView.ItemDecoration getItemDecoration();

    protected abstract SimpleAdapter getAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // fetch the list of gif images url passed from main activity
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            gifImages = bundle.getParcelableArrayList(GIF_IMAGES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        handler = new Handler();

        // set up adapter
        mAdapter = getAdapter();
        mAdapter.refreshAdapter(gifImages);
        mAdapter.setOnItemClickListener(this);

        // set up recycler view
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(false);

        // set up layout manager
        RecyclerView.LayoutManager layoutManager = getLayoutManager(mAdapter.getColumnCount());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(getItemDecoration());
        mRecyclerView.setAdapter(mAdapter);

        // scroll to center of the recycler view
        scrollToCenter();

        return rootView;
    }

    /* clear On Scroll Listener for the recycler view */
    private void clearOnScrollListener() {
        mRecyclerView.clearOnScrollListeners();
    }

    /* add On Scroll Listener for the recycler view */
    private void addOnScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // if Scroll State = idle, and if the child view cutting the center point of the screen,
                // then smooth scroll to the child view
                if (newState == SCROLL_STATE_IDLE) {

                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        View child = recyclerView.getChildAt(i);
                        float childX = child.getX();
                        float childY = child.getY();

                        if ((childX + child.getWidth()) > getScreenWidth() / 2
                                && childX < getScreenWidth() / 2
                                && (childY + child.getHeight()) > getScreenHeight() / 2
                                && childY < getScreenHeight() / 2
                                && !stopHandler) {

                            recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(child));
                        }
                    }
                }

                // if state = settling,
                if (newState == SCROLL_STATE_SETTLING) {
                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        View child = recyclerView.getChildAt(i);
                        float childX = child.getX();
                        float childY = child.getY();

                        if ((childX + child.getWidth()) > getScreenWidth() / 2
                                && childX < getScreenWidth() / 2
                                && (childY + child.getHeight()) > getScreenHeight() / 2
                                && childY < getScreenHeight() / 2
                                && !stopHandler) {

                            if (child.getTag() == null) {

                                View leftChild = recyclerView.getChildAt(i - 1);
                                View rightChild = recyclerView.getChildAt(i + 1);
                                View topChild = recyclerView.getChildAt(i - 3); // 3 is the number of view holders on a horizontal line of the screen
                                View botChild = recyclerView.getChildAt(i + 3); // 3 is the number of view holders on a horizontal line of the screen

                                int paddingChildPosition = recyclerView.getChildAdapterPosition(child);
                                int leftChildPosition = recyclerView.getChildAdapterPosition(leftChild);
                                int rightChildPosition = recyclerView.getChildAdapterPosition(rightChild);

                                if ((leftChild != null ? leftChild.getTag() : null) != null && leftChildPosition == paddingChildPosition - 1) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(leftChild));

                                } else if ((rightChild != null ? rightChild.getTag() : null) != null && rightChildPosition == paddingChildPosition + 1) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(rightChild));

                                } else if ((topChild != null ? topChild.getTag() : null) != null) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(topChild));
                                } else if ((botChild != null ? botChild.getTag() : null) != null) {

                                    recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(botChild));
                                }
                            } else {

                                recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(child));
                            }
                        }
                    }
                }

                if (newState == SCROLL_STATE_DRAGGING) {

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!stopHandler) {
                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        final View child = recyclerView.getChildAt(i);
                        float childX = child.getX();
                        float childY = child.getY();

                        if ((childX + child.getWidth()) > getScreenWidth() / 2
                                && childX < getScreenWidth() / 2
                                && (childY + child.getHeight()) > getScreenHeight() / 2
                                && childY < getScreenHeight() / 2) {

                            if (child.getScaleX() == 1 && child.getTag() != null && child != mPreviousView && count == 0) {
                                mPreviousView = child;

                                zoomInChild(child);

                                count = 0;
                            }
                        } else if (child.getScaleX() == SCALE) {

                            zoomOutChild(child);
                        } else if (child.getTag() != null) {
                            mPreviousView = null;
                        }
                    }
                }
            }
        });
    }

    /* scroll to center of recycler view */
    private void scrollToCenter() {

        int moveStep = 0;
        int delayMilis = 0;

        int scrollingTimes;
        int columnCount = mAdapter.getColumnCount();
        if (columnCount % 2 == 0) {
            scrollingTimes = columnCount / 2;
        } else {
            scrollingTimes = (columnCount - 1) / 2;
        }

        for (int i = 1; i <= scrollingTimes; i++) {

            moveStep = moveStep + columnCount + 1;
            delayMilis = delayMilis + 1000;
            final int finalMoveStep = moveStep;

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    mRecyclerView.smoothScrollToPosition(finalMoveStep);
                }
            }, delayMilis);

            if (i == scrollingTimes) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        View child = mRecyclerView.getChildAt(4);
                        child.animate().z(Z_VALUE).setDuration(1);
                        child.animate().scaleX(SCALE).setDuration(1);
                        child.animate().scaleY(SCALE).setDuration(1).setListener(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                MainActivity activity = (MainActivity) getActivity();
                                if (activity != null) {
                                    activity.hideLoadingIndicator();
                                    activity.showRecyclerView();
                                }
                                addOnScrollListener();
                            }
                        });
                    }
                }, delayMilis + 1000);
            }
        }
    }

    /* override on child click method */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (view.getTag() != null) {
            itemOnClickListener(view, position);
        }
    }

    /* helper method for item click */
    private void itemOnClickListener(View view, int position) {

//        stopHandler = true;
        clearOnScrollListener();

        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {

            View child = mRecyclerView.getChildAt(i);

            if (child == view) {

                if (child.getScaleX() == 1) {

                    // scroll to the child view at the selected position at the center of screen
                    mRecyclerView.smoothScrollToPosition(position);
                    mPreviousView = child;
                    child.animate().z(Z_VALUE).setDuration(SCALE_DURATION);
                    child.animate().scaleX(SCALE).setDuration(SCALE_DURATION);
                    child.animate().scaleY(SCALE).setDuration(SCALE_DURATION).setListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            //stopHandler = false;
                            addOnScrollListener();
                        }

                    });
                } else {

                    //stopHandler = false;
                    String childImgUrl = child.getTag().toString();

                    if (childImgUrl.contains("http")) {
                       /* new ImageViewer.Builder<>(getContext(), getPosters())
                                .setStartPosition(1)
                                .show();*/
                        new ImageViewer.Builder<>(getActivity(), mAdapter.getmGifUrlTrueList())
                                .setStartPosition(mAdapter.getmGifUrlTrueList().indexOf(childImgUrl))
                                .show();


                    }


                    addOnScrollListener();
                    return;
                }
            } else if (child.getScaleX() != 1) {

                child.animate().scaleX(1f).setDuration(SCALE_DURATION);
                child.animate().scaleY(1f).setDuration(SCALE_DURATION);
                child.animate().z(0).setDuration(SCALE_DURATION);
            }
        }
    }

    /* zoom in a child when it crossing the center of the screen */
    private void zoomInChild(View child) {

        //createRecursiveZoomIn(child, SCALE_STEP_COUNT, SCALE);
        for (int i = 1; i <= SCALE_STEP_COUNT; i++) {

            createHandlerForZoomIn(child, SCALE_STEP_COUNT, SCALE, i);
        }
    }

    /* helper method for zooming in child using handlers*/
    private void createHandlerForZoomIn(final View child, final int zoomStepsCount, final float scale, final int i) {

        long delayMilis = SCALE_DURATION * i / zoomStepsCount;

        new Handler().postDelayed(new Runnable() {

            float zoomStepInterval = (scale - 1) * i / zoomStepsCount;
            int zValue = (int) (Z_VALUE * i / zoomStepsCount);

            @Override
            public void run() {

                child.setScaleX(1 + zoomStepInterval);
                child.setScaleY(1 + zoomStepInterval);
                ViewCompat.setZ(child, zValue);
            }
        }, delayMilis);
    }

    /* helper method for zooming in child using recursive way*/
    private void createRecursiveZoomIn(final View child, final int zoomStepsCount, final float scale) {
        count++;
        if (count <= zoomStepsCount) {
            long delayMilis = SCALE_DURATION * count / zoomStepsCount;

            new Handler().postDelayed(new Runnable() {

                float zoomStepInterval = (scale - 1) * count / zoomStepsCount;
                int zValue = (int) (Z_VALUE * count / zoomStepsCount);

                @Override
                public void run() {

                    child.setScaleX(1 + zoomStepInterval);
                    child.setScaleY(1 + zoomStepInterval);
                    ViewCompat.setZ(child, zValue);
                }
            }, delayMilis);
            createRecursiveZoomIn(child, zoomStepsCount, scale);
        }
    }

    /* zoom out a child when it is not crossing the center of the screen */
    private void zoomOutChild(View child) {

        long delayMilisAccumulated = 1;
        for (int i = SCALE_STEP_COUNT - 1; i >= 0; i--) {

            // createHandlerForZoomOut(child, SCALE_STEP_COUNT, SCALE, i, delayMilisAccumulated);
            createTimerForZoomOut(child, SCALE_STEP_COUNT, SCALE, i, delayMilisAccumulated);
            delayMilisAccumulated++;
        }
    }

    /* helper method for zooming out child using handlers*/
    private void createHandlerForZoomOut(final View child, final int zoomStepsCount, final float scale, final int i, final long delayMilisAccumulated) {

        long delayMilis = SCALE_DURATION * delayMilisAccumulated / zoomStepsCount;

        new Handler().postDelayed(new Runnable() {

            float zoomStepInterval = (scale - 1) * i / zoomStepsCount;
            int zValue = (int) (Z_VALUE * i / zoomStepsCount);

            @Override
            public void run() {

                child.setScaleX(1 + zoomStepInterval);
                child.setScaleY(1 + zoomStepInterval);
                ViewCompat.setZ(child, zValue);
            }
        }, delayMilis);
    }

    /* helper method for zooming out child using timer*/
    private void createTimerForZoomOut(final View child, final int zoomStepsCount, final float scale, final int i, final long delayMilisAccumulated) {

        long delayMilis = SCALE_DURATION * delayMilisAccumulated / zoomStepsCount;

        TimerTask timerTask = new TimerTask() {

            float zoomStepInterval = (scale - 1) * i / zoomStepsCount;
            int zValue = (int) (Z_VALUE * i / zoomStepsCount);

            @Override
            public void run() {

                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        child.setScaleX(1 + zoomStepInterval);
                        child.setScaleY(1 + zoomStepInterval);
                        ViewCompat.setZ(child, zValue);
                    }
                });
            }
        };

        timer.schedule(timerTask, delayMilis);
    }



    public static String[] getPosters() {
        return new String[]{
                POSTERS_PATH,
                POSTERS_PATH ,
                POSTERS_PATH,
                POSTERS_PATH ,
                POSTERS_PATH ,
                POSTERS_PATH ,
                POSTERS_PATH ,
                POSTERS_PATH ,
                POSTERS_PATH
        };
    }
}
