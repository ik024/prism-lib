package com.ik.prism;

import android.support.v4.util.LruCache;

import com.ik.prism.executors.AsyncTaskExecutor;
import com.ik.prism.pojo.CacheData;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ismail.Khan2 on 2/4/2017.
 */

public class Helper {

    private OkHttpClient mHttpClient;
    private volatile LruCache<String, CacheData> mLruCache;
    private volatile Map<String, List<AsyncTaskExecutor>> mExecutorMapList;

    public Helper(OkHttpClient httpClient,
                  LruCache<String, CacheData> lruCache,
                  Map<String, List<AsyncTaskExecutor>> mapExecutorList) {

        mHttpClient = httpClient;
        mLruCache = lruCache;
        mExecutorMapList = mapExecutorList;
    }

    public OkHttpClient getOkHttpClient() {
        return mHttpClient;
    }

    /*Executors related methods*/

    public synchronized void addRequest(String requestTag, AsyncTaskExecutor taskExe) {
        if (mExecutorMapList.get(requestTag) == null) {
            List<AsyncTaskExecutor> executorList = new ArrayList<>();
            executorList.add(taskExe);
            mExecutorMapList.put(requestTag, executorList);
        } else {
            List<AsyncTaskExecutor> executorList = mExecutorMapList.get(requestTag);
            executorList.add(taskExe);
            mExecutorMapList.put(requestTag, executorList);
        }
    }

    public synchronized void cancelRequest(String requestTag) {
        if (mExecutorMapList.get(requestTag) != null) {
            for (AsyncTaskExecutor taskExe : mExecutorMapList.get(requestTag)) {
                taskExe.cancel(true);
            }
            mExecutorMapList.remove(requestTag);
        }
    }

    public synchronized void removeRequest(String requestTag) {
        mExecutorMapList.remove(requestTag);
    }

    /*Cache Related methods*/

    public void saveDataInCache(String url, CacheData cacheData) {
        mLruCache.put(url, cacheData);
    }

    public CacheData getDataFromCache(String url) {
        return mLruCache.get(url);
    }


    public int getExecutorListSize(){
        return mExecutorMapList.size();
    }

    public int noOfExecutorsWithTag(String requestTag){
        if(mExecutorMapList.get(requestTag) == null){
            return -1;
        }
        return mExecutorMapList.get(requestTag).size();
    }

    public List<AsyncTaskExecutor> getExecutorListForTag(String requestTag){
        return mExecutorMapList.get(requestTag);
    }

    public LruCache<String, CacheData> getLruCache(){
        return mLruCache;
    }

    public static int getDefaultMemoryCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        return maxMemory / 7;
    }
}
