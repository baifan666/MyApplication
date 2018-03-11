package com.example.baifan.myapplication.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by baifan on 2018/2/13.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        RongIM.init(this);//初始化
        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }

}
