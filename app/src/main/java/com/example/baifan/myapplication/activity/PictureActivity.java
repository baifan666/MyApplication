package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.github.chrisbanes.photoview.PhotoView;

public class PictureActivity extends Activity {

    private PhotoView photoView;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
        getWindow().setAttributes(p);

        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        photoView = (PhotoView)findViewById(R.id.large_image);
        Glide.with(this).load(url).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                .error(R.drawable.error)//图片加载失败后，显示的图片
                .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
