package com.ik.cacheloading.home.controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.ik.cacheloading.R;
import com.ik.cacheloading.home.model.UserData;
import com.ik.cacheloading.home.model.pojo.UserInfo;
import com.ik.cacheloading.home.view.HomeView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements
        UserData.UserDataListener,
        HomeView.RecyclerFirstAndLastViewListener{

    private final String TAG = HomeActivity.class.getSimpleName();
    private final int PULL_TO_REFRESH_THRESHOLD = 300;

    /*These are used to store RecyclerView state and Adapter list on orientation change*/
    private final String KEY_LIST_STATE = "listState";
    private final String KEY_ADAPTER_LIST = "listAdapter";

    /*Model for the HomePage*/
    private UserData mUserData;

    /*Adapter*/
    private UserListAdapter mAdapter;

    /*Holds all the views related to HomePage*/
    private HomeView mHomeView;

    /*Variable for handling pull to refresh*/
    private float initialTouchY;
    private boolean scrolling, shouldRefresh, firstViewVisible, isLoading;

    /*This is used for testing*/
    public CountingIdlingResource idlingResource = new CountingIdlingResource("API_CALL");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHomeView = new HomeView(LayoutInflater.from(this));
        setContentView(mHomeView.getRootView());
        setSupportActionBar(mHomeView.getToolbar());
        mHomeView.setToolbarTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        mUserData = new UserData();

        int orientation = this.getResources().getConfiguration().orientation;
        mHomeView.adjustViewListBasedOnOrientation(orientation);

        if (savedInstanceState == null) {
            callUserListAPI();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserData.registerUserDataListener(this);
        mHomeView.registerRecyclerViewPosListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserData.unregisterUserDataListener();
        mHomeView.unregisterRecyclerViewPosListener();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && bundleContainsAllKeys(savedInstanceState)) {
            //if saved state present, restore state!
            restoreState(savedInstanceState);
        }else{
            //if no saved state, fetch user list
            callUserListAPI();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mHomeView != null && mAdapter != null) {
            Parcelable recycleViewState = mHomeView.getRecyclerViewState();
            outState.putParcelable(KEY_LIST_STATE, recycleViewState);
            outState.putParcelableArrayList(KEY_ADAPTER_LIST, mAdapter.getList());
        }
    }

    /**
     * This method calls the API to fetch user details
     */
    private void callUserListAPI(){
        if(isNetworkAvailable(this)) {
            isLoading = true;
            idlingResource.increment();

            mHomeView.setTextInfo(getString(R.string.msg_loading));

            mUserData.fetchUserList(getString(R.string.url_user_list),
                    getString(R.string.tag_user_list));
        }else{
            mHomeView.setTextInfo(getString(R.string.msg_no_internet));
            mHomeView.showSnackBar(getString(R.string.snack_msg_no_internet));
        }
    }

     /* This is the callback method of UserData model class, when network fetch is success */
    @Override
    public void onDataFetchSuccess(ArrayList<UserInfo> userInfoList) {
        isLoading = false;

        mHomeView.hideTextInfo();
        addDataToRecyclerView(userInfoList);

        idlingResource.decrement();
    }

    /* This is the callback method of UserData model class, when network fetch fails */
    @Override
    public void onDataFetchError(String errorMsg) {
        isLoading = false;

        mHomeView.setTextInfo(getString(R.string.msg_error));
        mHomeView.showSnackBar(errorMsg);

        idlingResource.decrement();
    }

    /**
     * This method either creates adapter instance(if adapter not attached to recycler view)
     * or updates the adapter (if adapter already attached to recycle view).
     *
     * @param userList user data fetched
     */
    private void addDataToRecyclerView(ArrayList<UserInfo> userList) {
        if (mAdapter == null) {
            mAdapter = new UserListAdapter(userList);
            mHomeView.setRecyclerViewAdapter(mAdapter);
        } else {
            mAdapter.updateList(userList);
        }
    }

    /**
     * This method fetches recycler view state and adapter list from the bundle obtained in
     * onRestoreInstanceState callback method.
     *
     * @param savedBundle bundle retrieved from onSaveInstanceState callback
     */
    private void restoreState(Bundle savedBundle){
        Parcelable recycleViewState = savedBundle.getParcelable(KEY_LIST_STATE);
        ArrayList<UserInfo> list = savedBundle.getParcelableArrayList(KEY_ADAPTER_LIST);
        mAdapter = new UserListAdapter(list);
        mHomeView.setRecyclerViewAdapter(mAdapter);
        mHomeView.setRecyclerViewState(recycleViewState);
    }

    /**
     * This method makes sure if all the required keys are present in the bundle.
     *
     * @param savedBundle bundle retrieved from onSaveInstanceState callback
     * @return true if all keys present, false if any one is missing
     */
    private boolean bundleContainsAllKeys(Bundle savedBundle) {
        if (!savedBundle.containsKey(KEY_LIST_STATE)) {
            return false;
        } else if (!savedBundle.containsKey(KEY_ADAPTER_LIST)) {
            return false;
        }

        return true;
    }

    /**
     * Identifies pull to refresh gesture
     * @param event Obtained from Android System
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(firstViewVisible && !isLoading) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //on finger pressed
                initialTouchY = event.getY();
                scrolling = true;
                shouldRefresh = false;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                //on finger moved
                if (initialTouchY < event.getY()) {
                    //if scrolling downwards
                    toggleRefreshText(initialTouchY, event.getY());
                    shouldRefresh = true;
                    mHomeView.moveRecyclerView(event.getY() - initialTouchY);
                } else {
                    //scrolling upwards
                    shouldRefresh = false;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                //on finger lifted
                float diffY = getDiff(initialTouchY, event.getY());
                if (shouldRefresh && diffY >= PULL_TO_REFRESH_THRESHOLD) {
                    isLoading = true;
                    callUserListAPI();
                    mHomeView.showSnackBar("Refreshing!");
                }

                //reset values
                initialTouchY = 0;
                shouldRefresh = false;
                mHomeView.moveRecyclerView(0);
                mHomeView.hideRefreshText();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void toggleRefreshText(float initialTouchY, float finalTouchY){
        float diffY = getDiff(initialTouchY, finalTouchY);
        if(diffY >= PULL_TO_REFRESH_THRESHOLD){
            mHomeView.showRefreshText();
        }else{
            mHomeView.hideRefreshText();
        }
    }

    private float getDiff(float initialTouchY, float finalTouchY){
        return Math.abs(finalTouchY - initialTouchY);
    }

    @Override
    public void onFirstViewVisible() {
        firstViewVisible = true;
    }

    @Override
    public void onFirstViewNotVisible() {
        firstViewVisible = false;
    }

    @Override
    public void onLastViewVisible() {
        mHomeView.showSnackBar(getString(R.string.last_view_reached));
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
