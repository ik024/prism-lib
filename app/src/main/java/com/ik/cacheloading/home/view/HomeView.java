package com.ik.cacheloading.home.view;

import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ik.cacheloading.R;

/**
 * This class contains the view of HomeActivity and its operations
 */
public class HomeView implements View.OnClickListener {

    private final int DEFAULT_ANIMATION_DURATION = 500;

    private View mRootView;
    private RecyclerView mRvUserList;
    private TextView mTvInfo, mTvRefresh;
    private Toolbar mToolbar;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFab;
    private StaggeredGridLayoutManager mLayoutManager;
    private RecyclerFirstAndLastViewListener mViewPosListener;

    /*This constructor inflates and initializes all the views*/
    public HomeView(LayoutInflater inflater) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.activity_main, null);
            mCoordinatorLayout = (CoordinatorLayout) mRootView.findViewById(R.id.coordinatorLayout);
            mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
            mRvUserList = (RecyclerView) mRootView.findViewById(R.id.rv_user_list);
            mTvInfo = (TextView) mRootView.findViewById(R.id.tv_info);
            mTvRefresh = (TextView) mRootView.findViewById(R.id.tv_refresh);
            mFab = (FloatingActionButton) mRootView.findViewById(R.id.fab);

            registerListeners();
        }
    }

    private void registerListeners() {
        addRecyclerViewScrollListener();
        mFab.setOnClickListener(this);
    }

    public View getRootView() {
        return mRootView;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        mRvUserList.setAdapter(adapter);
    }

    public void setRecyclerViewState(Parcelable listState) {
        mRvUserList.getLayoutManager().onRestoreInstanceState(listState);
    }

    public Parcelable getRecyclerViewState() {
        return mRvUserList.getLayoutManager().onSaveInstanceState();
    }

    public void adjustViewListBasedOnOrientation(int orientation) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRvUserList.setLayoutManager(mLayoutManager);
        } else {
            mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mRvUserList.setLayoutManager(mLayoutManager);
        }

        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
    }

    public void showSnackBar(String msg) {
        final Snackbar snackbar = Snackbar.make(mCoordinatorLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                showSnackBar("Hello, I am snack bar");
                break;
            default:
                break;
        }
    }

    public void moveRecyclerView(float value) {
        if (value != 0) {
            mRvUserList.setTranslationY(value);
        } else {
            mRvUserList.animate().translationY(0).setDuration(DEFAULT_ANIMATION_DURATION);
        }
    }

    public void setTextInfo(String msg) {
        showTextInfo();
        mTvInfo.setText(msg);
    }

    private void showTextInfo() {
        mTvInfo.setVisibility(View.VISIBLE);
    }

    public void hideTextInfo() {
        mTvInfo.setVisibility(View.GONE);
    }

    public void setToolbarTitleTextColor(int color) {
        mToolbar.setTitleTextColor(color);
    }

    public void showRefreshText() {
        mTvRefresh.setVisibility(View.VISIBLE);
    }

    public void hideRefreshText() {
        mTvRefresh.setVisibility(View.GONE);
    }

    public void addRecyclerViewScrollListener() {
        mRvUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //fist position which is completely visible on screen
                int firstVisibleItemPosition[] = mLayoutManager.findFirstCompletelyVisibleItemPositions(null);
                //last position which is completely visible on screen
                int lastVisibleItemPosition[] = mLayoutManager.findLastCompletelyVisibleItemPositions(null);

                if (firstVisibleItemPosition[0] == 0) {
                    if (mViewPosListener != null) {
                        mViewPosListener.onFirstViewVisible();
                    }

                } else {
                    if (mViewPosListener != null) {
                        mViewPosListener.onFirstViewNotVisible();
                    }

                    //since in portrait mode has 2 columns and landscape mode has 3 columns
                    //the bottom row may vary depending on the available space
                    //so check all the integers returned for last visible pos
                    //if it matches with count-1
                    for (Integer i : lastVisibleItemPosition) {
                        if ((i == mRvUserList.getAdapter().getItemCount() - 1) && mViewPosListener != null) {
                            mViewPosListener.onLastViewVisible();
                        }
                    }
                }
            }
        });
    }

    public void registerRecyclerViewPosListener(RecyclerFirstAndLastViewListener listener) {
        mViewPosListener = listener;
    }

    public void unregisterRecyclerViewPosListener() {
        mViewPosListener = null;
    }

    public interface RecyclerFirstAndLastViewListener {
        void onFirstViewVisible();

        void onFirstViewNotVisible();

        void onLastViewVisible();
    }
}
