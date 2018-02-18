package com.example.baifan.myapplication.application;

import android.app.Application;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by baifan on 2018/2/13.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);//初始化
    }

}
