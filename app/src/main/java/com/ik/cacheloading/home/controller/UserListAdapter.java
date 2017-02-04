package com.ik.cacheloading.home.controller;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ik.cacheloading.R;
import com.ik.cacheloading.home.model.pojo.UserInfo;
import com.ik.cacheloading.home.view.UserListItemView;
import com.ik.prism.Prism;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListItemView> {

    private final String TAG_IMAGE_REQUEST = "image";
    private final int MIN_HEIGHT_500 = 500;
    private final int MIN_HEIGHT_800 = 800;

    private ArrayList<UserInfo> mList;

    public UserListAdapter(ArrayList<UserInfo> list){
        mList = list;
    }

    @Override
    public UserListItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.row_user_list, parent, false);
        UserListItemView holder = new UserListItemView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserListItemView holder, int position) {
        String bitmapUrl   = mList.get(position).user.profile_image.large;
        String imageColor = mList.get(position).color;

        //initially set the background color which represent the image color
        holder.imageView.setBackgroundColor(Color.parseColor(imageColor));

        //alternatively change the min height of the image view
        if(position % 2 == 0){
            holder.imageView.setMinimumHeight(MIN_HEIGHT_500);
        }else{
            holder.imageView.setMinimumHeight(MIN_HEIGHT_800);
        }

        //fetch image
        Prism.getInstance()
                .loadBitmapFrom(bitmapUrl, TAG_IMAGE_REQUEST+position)
                .into(holder.imageView)
                .execute();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateList(ArrayList<UserInfo> updatedList) {
        mList = updatedList;
        notifyDataSetChanged();
    }

    public ArrayList<UserInfo> getList(){
        if(mList == null){
            mList = new ArrayList<>();
        }
        return mList;
    }
}
