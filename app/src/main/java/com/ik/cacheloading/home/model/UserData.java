package com.ik.cacheloading.home.model;

import android.support.test.espresso.idling.CountingIdlingResource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ik.cacheloading.home.model.pojo.UserInfo;
import com.ik.prism.Prism;
import com.ik.prism.interfaces.StringResponseListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserData {


    private UserDataListener mListener;

    public UserData(){
    }

    public void registerUserDataListener(UserDataListener listener){
        mListener = listener;
    }

    public void unregisterUserDataListener(){
        mListener = null;
    }

    public void fetchUserList(String url, String requestTag){
        Prism.getInstance().loadStringFrom(url, requestTag, new StringResponseListener() {
            @Override
            public void onResponse(final String response) {
                Type listType =
                        new TypeToken<ArrayList<UserInfo>>() {
                        }.getType();
                ArrayList<UserInfo> userInfoList = new Gson().fromJson(response.toString(), listType);

                if(mListener != null) {
                    mListener.onDataFetchSuccess(userInfoList);
                }
            }

            @Override
            public void onError(String errorMsg) {
                if(mListener != null) {
                    mListener.onDataFetchError(errorMsg);
                }
            }
        }).execute();
    }

    public interface UserDataListener{
        void onDataFetchSuccess(ArrayList<UserInfo> userInfoList);
        void onDataFetchError(String errorMsg);
    }
}
