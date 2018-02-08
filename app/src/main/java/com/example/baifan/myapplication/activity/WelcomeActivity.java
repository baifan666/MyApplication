package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.content.Intent;
import android.view.Window;

import com.example.baifan.myapplication.R;

/**
 * Created by baifan on 2017/11/13.
 */

public class WelcomeActivity extends Activity {
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,
                        MainActivity.class));
                finish();
            }
        }, 3000);
    }
}
