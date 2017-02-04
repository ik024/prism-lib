package com.ik.prism.executors;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

/**
 * This class asynchronously fetches data over the network
 */
public class AsyncTaskExecutor extends AsyncTask<String, Void, Response> {

    private OkHttpClient mClient;
    private ResponseListener mListener;


    public AsyncTaskExecutor(OkHttpClient client, ResponseListener listener){
        mClient = client;
        mListener = listener;
    }

    @Override
    protected Response doInBackground(String... params) {

        String url = params[0];
        String tag = params[1];

        Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        try {
            Response response = mClient.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response response) {
        if(response != null){
            if(response.code() == 200) {
                mListener.onResponse(response);
            }else {
                mListener.onError("Error code: "+response.code());
            }
        }else{
            mListener.onError("Error: Response is null");
        }
    }

    public interface ResponseListener{
        void onResponse(Response response);
        void onError(String errorMsg);
    }
}
