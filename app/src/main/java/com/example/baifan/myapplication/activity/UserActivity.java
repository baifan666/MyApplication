package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends Activity {
    private ImageView back;
    private Dialog mDialog;
    private String username,headurl;
    private CircleImageView head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = new Intent();
        username = intent.getStringExtra("username");
        headurl = intent.getStringExtra("headurl");
        Toast.makeText(UserActivity.this,username,Toast.LENGTH_LONG).show();
        Toast.makeText(UserActivity.this,headurl,Toast.LENGTH_LONG).show();
        back=(ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        head = (CircleImageView)findViewById(R.id.head);
        Glide.with(getApplicationContext()).load(headurl)
                .into(head);
    }
}
