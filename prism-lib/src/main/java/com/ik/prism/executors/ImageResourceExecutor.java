package com.ik.prism.executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.ik.prism.Helper;
import com.ik.prism.interfaces.BitmapResponseListener;
import com.ik.prism.pojo.BitmapCache;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class handles APIs that return bitmap in response
 */
public class ImageResourceExecutor implements AsyncTaskExecutor.ResponseListener {

    private Helper mHelper;
    private String mUrl;
    private String mRequestTag;
    private ImageView mImageView;
    private BitmapResponseListener mListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /*Constructor with listener*/
    public ImageResourceExecutor(Helper helper, String url, String requestTag,
                                 BitmapResponseListener listener) {
        mHelper = helper;
        mUrl = url;
        mRequestTag = requestTag;
        mListener = listener;

        //check if valid arguments before proceeding
        checkArguments(url, requestTag, listener);

    }

    /*Constructor without listener*/
    public ImageResourceExecutor(Helper helper, String requestTag, String url) {
        mHelper = helper;
        mRequestTag = requestTag;
        mUrl = url;
    }

    /**
     * This method takes image view reference from the client
     * @param imageView
     */
    public ImageResourceExecutor into(ImageView imageView) {
        mImageView = imageView;

        //check if valid arguments before proceeding
        checkArguments(mUrl, mRequestTag, imageView);

        return this;
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

        if (mHelper.getDataFromCache(mUrl) != null) {
            //data present in cache
            if (mImageView != null) {
                //if image view was passed set the bitmap to image view
                mImageView.setImageBitmap(((BitmapCache) mHelper.getDataFromCache(mUrl)).bitmap);
            } else if (mListener != null) {
                //if listener was passed return the bitmap
                mListener.onResponse(((BitmapCache) mHelper.getDataFromCache(mUrl)).bitmap);
            }
        } else {
            //data not present in cache
            //create a request to fetch the bitmap
             AsyncTaskExecutor  taskExe = new AsyncTaskExecutor(mHelper.getOkHttpClient(), this);
            //save the request
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
                InputStream is = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mHelper.saveDataInCache(mUrl, new BitmapCache(bitmap));
                try {
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*post on UI thread*/
                posResponseOnUIThread(bitmap);
            }
        }).start();

    }

    /*AsyncTaskExecutor callback when error occurs executing the api call*/
    @Override
    public void onError(String errorMsg) {
        //remove request
        mHelper.removeRequest(mRequestTag);

        if (mListener != null) {
            //send the error msg
            mListener.onError(errorMsg);
        }
    }

    private void posResponseOnUIThread(final Bitmap bitmap){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    //bitmap is not null
                    if (mImageView != null) {
                        //if image view not null set the bitmap to image view
                        mImageView.setImageBitmap(bitmap);
                    } else if (mListener != null) {
                        //if listener is not null return the bitmap
                        mListener.onResponse(bitmap);
                    }

                } else {
                    //bitmap is null
                    if (mListener != null) {
                        //send the error msg
                        mListener.onError("Null bitmap returned");
                    }
                }
            }
        });
    }
    private void checkArguments(String url, String requestTag, ImageView imageView) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Url cannot be null or empty");
        }
        if (requestTag == null || requestTag.isEmpty()) {
            throw new IllegalArgumentException("RequestTag cannot be null or empty");
        }
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView cannot be null");
        }
    }

    private void checkArguments(String url, String requestTag, BitmapResponseListener listener) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Url cannot be null or empty");
        }
        if (requestTag == null || requestTag.isEmpty()) {
            throw new IllegalArgumentException("RequestTag cannot be null or empty");
        }
        if (listener == null) {
            throw new IllegalArgumentException("ImageResponseListener cannot be null");
        }
    }
}
