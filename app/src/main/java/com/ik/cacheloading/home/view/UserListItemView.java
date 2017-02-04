package com.ik.cacheloading.home.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ik.cacheloading.R;

/**
 * Created by Ismail.Khan2 on 2/3/2017.
 */

public class UserListItemView extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public UserListItemView(View view) {
        super(view);
        imageView = (ImageView) view.findViewById(R.id.iv_row_user_img);

    }
}
