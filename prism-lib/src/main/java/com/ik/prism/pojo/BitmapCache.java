package com.ik.prism.pojo;

import android.graphics.Bitmap;

/**
 * Created by Ismail.Khan2 on 1/31/2017.
 */

public class BitmapCache extends CacheData{
    public Bitmap bitmap;

    public BitmapCache(Bitmap bitmap){
        this.bitmap = bitmap;
    }
}
