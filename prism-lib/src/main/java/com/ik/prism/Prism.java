package com.ik.prism;

import android.support.v4.util.LruCache;

import com.ik.prism.executors.AsyncTaskExecutor;
import com.ik.prism.executors.ImageResourceExecutor;
import com.ik.prism.executors.StringResourceExecutor;
import com.ik.prism.interfaces.BitmapResponseListener;
import com.ik.prism.interfaces.StringResponseListener;
import com.ik.prism.pojo.BitmapCache;
import com.ik.prism.pojo.CacheData;
import com.ik.prism.pojo.StringCache;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ismail.khan2 on 1/30/2017.
 */

public class Prism {

    private static int DEFAULT_MEMORY_CACHE_SIZE = Helper.getDefaultMemoryCacheSize(); //1/7th of the available memory

    private static Prism mSingleton;
    private Helper mHelper;

    private Prism(OkHttpClient httpClient,
                  LruCache<String, CacheData> lruCache,
                  Map<String, List<AsyncTaskExecutor>> executorList) {

        mHelper = new Helper(httpClient, lruCache, executorList);
    }

    public static Prism getInstance() {
        if (mSingleton == null) {
            synchronized (Prism.class) {
                if (mSingleton == null) {
                    mSingleton = new Builder().build(DEFAULT_MEMORY_CACHE_SIZE);
                }
            }
        }

        return mSingleton;
    }

    public ImageResourceExecutor loadBitmapFrom(String url, String requestTag) {
        return new ImageResourceExecutor(mHelper, requestTag, url);
    }

    public ImageResourceExecutor loadBitmapFrom(String url, String requestTag,
                                                BitmapResponseListener listener) {
        return new ImageResourceExecutor(mHelper, url, requestTag, listener);
    }

    public StringResourceExecutor loadStringFrom(String url, String requestTag,
                                                 StringResponseListener listener) {
        return new StringResourceExecutor(mHelper, url, requestTag, listener);
    }

    public void setCacheSize(int newCacheSize) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        if(newCacheSize < maxMemory) {
            Builder.changeLruCache(newCacheSize);
        }else{
            throw new IllegalArgumentException("Cache size should be less than Max Memory: "+maxMemory);
        }
    }

    private static class Builder {

        private static OkHttpClient httpClient;
        private static LruCache<String, CacheData> lruCache;
        private static Map<String, List<AsyncTaskExecutor>> executorList;

        public Builder() {
            httpClient = new OkHttpClient();
            executorList = new HashMap<>();
        }

        public Prism build(int memoryCacheSize) {
            lruCache = new LruCache<String, CacheData>(memoryCacheSize) {
                @Override
                protected int sizeOf(String key, CacheData value) {
                    if (value instanceof BitmapCache) {
                        return ((BitmapCache) value).bitmap.getByteCount();
                    } else if (value instanceof StringCache) {
                        return ((StringCache) value).responseStr.length();
                    } else {
                        return 1;
                    }
                }
            };

            //check if all the required arguments are initialized
            checkArguments();

            return new Prism(httpClient, lruCache, executorList);
        }

        public static void changeLruCache(int cacheSize){
            lruCache.resize(cacheSize);

        }

        private void checkArguments(){
            if(httpClient == null){
                throw new IllegalArgumentException("HttpClient cannot be null");
            }else if(lruCache == null){
                throw new IllegalArgumentException("LruCache cannot be null");
            }else if(executorList == null){
                throw new IllegalArgumentException("AsyncTaskExecutor list cannot be null");
            }

        }
    }
}
