package com.ik.prism;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.util.LruCache;

import com.ik.prism.executors.AsyncTaskExecutor;
import com.ik.prism.pojo.BitmapCache;
import com.ik.prism.pojo.CacheData;
import com.ik.prism.pojo.StringCache;
import com.squareup.okhttp.OkHttpClient;
import java.lang.String;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ismail.Khan2 on 2/4/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class HelperCacheTest {

    @Mock
    Bitmap bitmap;
    String imageUrl = "https://images.unsplash.com/profile-1464495186405-68089dcd96c3?ixlib=rb-0.3.5\u0026q=80\u0026fm=jpg\u0026crop=faces\u0026fit=crop\u0026h=32\u0026w=32\u0026s=63f1d805cffccb834cf839c719d91702";
    String stringUrl = "http://pastebin.com/raw/wgkJgazE";

    Helper mHelper;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Before
    public void setup() {
        OkHttpClient okHttpClient = new OkHttpClient();
        LruCache<String, CacheData> lruCache =  new LruCache<>(Helper.getDefaultMemoryCacheSize());
        Map<String, List<AsyncTaskExecutor>> executorList = new HashMap<>();

        mHelper = new Helper(okHttpClient, lruCache, executorList);
    }

    @Test
    public void bitmapCacheTest() {
        mHelper.saveDataInCache(imageUrl, new BitmapCache(bitmap));

        int count = mHelper.getLruCache().size();
        assert count == 1;

        assert bitmap == ((BitmapCache)mHelper.getLruCache().get(imageUrl)).bitmap;
    }

    @Test
    public void stringCacheTest(){
        String strResponse = "test response";

        mHelper.saveDataInCache(stringUrl, new StringCache(strResponse));

        int count = mHelper.getLruCache().size();
        assert  count == 1;

        assert  strResponse == ((StringCache)mHelper.getLruCache().get(stringUrl)).responseStr;
    }

}
