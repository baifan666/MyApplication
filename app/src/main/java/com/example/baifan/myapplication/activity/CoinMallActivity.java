package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;

public class CoinMallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coin_mall);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
    }
}
