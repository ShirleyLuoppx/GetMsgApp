package com.ppx.getmsgapp;

import android.app.Application;
import android.content.Context;

/**
 * @Author: LuoXia
 * @Date: 2021/6/29 17:45
 * @Description:
 */
public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
