package com.ik.prism;

import android.graphics.Bitmap;
import android.support.test.espresso.Espresso;
import android.util.LruCache;

import com.ik.prism.executors.AsyncTaskExecutor;
import com.ik.prism.executors.ImageResourceExecutor;
import com.ik.prism.pojo.CacheData;
import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by ismail.khan2 on 2/3/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class PrismTest {

    String imageUrl = "https://images.unsplash.com/profile-1464495186405-68089dcd96c3?ixlib=rb-0.3.5\u0026q=80\u0026fm=jpg\u0026crop=faces\u0026fit=crop\u0026h=32\u0026w=32\u0026s=63f1d805cffccb834cf839c719d91702";
    String jsonUrl = "http://pastebin.com/raw/wgkJgazE";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getInstanceTest() {
        Prism prism = Prism.getInstance();
        assertNotNull(prism);
    }

}
