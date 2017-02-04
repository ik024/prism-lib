package com.ik.prism.executors;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.ik.prism.Helper;
import com.ik.prism.interfaces.StringResponseListener;
import com.ik.prism.pojo.StringCache;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * This class handles APIs that return String in response
 */
public class StringResourceExecutor implements AsyncTaskExecutor.ResponseListener {

    private Helper mHelper;
    private String mUrl;
    private String mRequestTag;
    private StringResponseListener mListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public StringResourceExecutor(Helper helper, String url, String requestTag,
                                  StringResponseListener listener) {
        mHelper = helper;
        mUrl = url;
        mRequestTag = requestTag;
        mListener = listener;

        //check if valid arguments before proceeding
        checkArguments(url, requestTag, listener);
    }

    /**
     * This method is used by the client to fetch the data
     */
    public void execute(){
        fetchData();
    }

    /**
     * This method either fetches the data from the cache or over the network
     */
    private void fetchData() {
        if (mHelper.getDataFromCache(mUrl) != null){
            //data present in the cache
            if (mListener != null) {
                mListener.onResponse(((StringCache) mHelper.getDataFromCache(mUrl)).responseStr);
            }
        } else {
            //data not present in the cache
            //create request to fetch data
            AsyncTaskExecutor taskExe = new AsyncTaskExecutor(mHelper.getOkHttpClient(), this);
            //save request
            mHelper.addRequest(mRequestTag, taskExe);
            //make asynchronous network call
            taskExe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl, mRequestTag);
        }
    }

    /*AsyncTaskExecutor callback when a response is received from the api call*/
    @Override
    public void onResponse(final Response response) {
        //remove request
        mHelper.removeRequest(mRequestTag);

        /*process the response in the background and return the bitmap*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //decode the response and return as string
                    final String strResponse = response.body().string();
                    mHelper.saveDataInCache(mUrl, new StringCache(strResponse));
                    try {
                        response.body().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /*post on UI thread*/
                    postResponseOnUIThread(strResponse);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /*AsyncTaskExecutor callback when error occurs executing the api call*/
    @Override
    public void onError(String errorMsg) {
        //remove request
        mHelper.removeRequest(mRequestTag);
        //send error msg
        mListener.onError(errorMsg);
    }

    private void postResponseOnUIThread(final String strResponse){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(strResponse != null || strResponse.isEmpty()) {
                    //return the string response
                    mListener.onResponse(strResponse);
                }else{
                    //send error msg
                    mListener.onError("Empty string response");
                }
            }
        });
    }

    private void checkArguments(String url, String requestTag, StringResponseListener listener) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Url cannot be null or empty");
        }
        if (requestTag == null || requestTag.isEmpty()) {
            throw new IllegalArgumentException("Request Tag cannot be null or empty");
        }
        if (listener == null) {
            throw new IllegalArgumentException("StringResponseListener cannot be null");
        }
    }

}
