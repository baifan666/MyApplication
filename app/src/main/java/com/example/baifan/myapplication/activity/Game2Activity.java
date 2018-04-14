package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class Game2Activity extends Activity {

    com.tencent.smtt.sdk.WebView tbsContent;
    private String url = "http://h.4399.com/play/191873.htm";
    //private String url = "http://www.baidu.com";
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game2);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tbsContent = (com.tencent.smtt.sdk.WebView)findViewById(R.id.tbsContent);
        tbsContent.loadUrl(url);
        //设置WebView属性，能够执行Javascript脚本
        WebSettings webSettings = tbsContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024*1024*8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info.isAvailable())
        {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else
        {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);//不使用网络，只加载缓存
        }
        //支持离线浏览
        webSettings.setAppCacheEnabled(true);
        tbsContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        back=(ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && tbsContent.canGoBack()) {
            tbsContent.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}