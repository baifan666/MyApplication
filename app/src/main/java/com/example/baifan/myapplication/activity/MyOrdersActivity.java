package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.baifan.myapplication.R;

public class MyOrdersActivity extends Activity {
    private final int MYREADALL = 1;
    private ImageView back;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
    }
}
