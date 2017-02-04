package com.ik.prism.interfaces;

import android.graphics.Bitmap;

/**
 * Created by Ismail.Khan2 on 2/4/2017.
 */

public interface BitmapResponseListener {
    void onResponse(Bitmap bitmap);
    void onError(String errorMsg);
}
