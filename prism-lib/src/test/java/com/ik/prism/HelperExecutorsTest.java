package com.ik.prism;

import android.support.v4.util.LruCache;

import com.ik.prism.executors.AsyncTaskExecutor;
import com.ik.prism.pojo.CacheData;
import com.ik.prism.pojo.StringCache;
import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ismail.Khan2 on 2/4/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class HelperExecutorsTest {

    String TAG1 = "tag1";
    String TAG2 = "tag2";
    String TAG3 = "tag3";

    AsyncTaskExecutor dummyTaskExe1, dummyTaskExe2, dummyTaskExe3;

    Helper mHelper;
    @Before
    public void setup(){
        OkHttpClient okHttpClient = new OkHttpClient();
        LruCache<String, CacheData> lruCache = new LruCache<>(Helper.getDefaultMemoryCacheSize());
        Map<String, List<AsyncTaskExecutor>> executorList = new HashMap<>();

        dummyTaskExe1 = new AsyncTaskExecutor(okHttpClient, null);
        dummyTaskExe2 = new AsyncTaskExecutor(okHttpClient, null);
        dummyTaskExe3 = new AsyncTaskExecutor(okHttpClient, null);

        mHelper = new Helper(okHttpClient, lruCache, executorList);
    }

    @Test
    public void addRequestWithNewTag(){
        addRequest(TAG1, dummyTaskExe1);
        addRequest(TAG2, dummyTaskExe2);
        int count = mHelper.getExecutorListSize();
        assert count == 2;
    }

    @Test
    public void addRequestWithSameTag(){
        addRequest(TAG1, dummyTaskExe1);
        addRequest(TAG1, dummyTaskExe2);
        int count = mHelper.getExecutorListSize();
        assert count == 1;
    }

    @Test
    public void checkNoOfExecutorForSameTag(){
        addRequest(TAG3, dummyTaskExe1);
        addRequest(TAG3, dummyTaskExe2);
        addRequest(TAG3, dummyTaskExe3);
        int count = mHelper.noOfExecutorsWithTag(TAG3);
        assert count == 3;
    }

    @Test
    public void verifyAsyncTaskExecutorObjectsAdded(){
        addRequest(TAG1, dummyTaskExe1);
        addRequest(TAG2, dummyTaskExe2);
        addRequest(TAG3, dummyTaskExe2);

        List<AsyncTaskExecutor> exeList = mHelper.getExecutorListForTag(TAG1);
        assert exeList != null;
        assert exeList.size() == 1;
        assert exeList.get(0) == dummyTaskExe1;

        List<AsyncTaskExecutor> exeList1 = mHelper.getExecutorListForTag(TAG2);
        assert exeList1 != null;
        assert exeList1.size() == 1;
        assert exeList1.get(0) == dummyTaskExe2;

        List<AsyncTaskExecutor> exeList2 = mHelper.getExecutorListForTag(TAG2);
        assert exeList2 != null;
        assert exeList2.size() == 1;
        assert exeList2.get(0) == dummyTaskExe2;
    }


    private void addRequest(String tag, AsyncTaskExecutor taskExe){
        mHelper.addRequest(tag, taskExe);
    }

    @Test
    public void stringCacheTest(){
        String strResponse = "test response";
        mHelper.saveDataInCache("url", new StringCache(strResponse));

        int count = mHelper.getLruCache().size();
        assert  count == 1;

        assert  strResponse == ((StringCache)mHelper.getLruCache().get("url")).responseStr;
    }
}
