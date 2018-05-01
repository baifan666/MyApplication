package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.App;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WeatherActivity extends Activity {
    private com.tencent.smtt.sdk.WebView tbsContent;
    //private String url = "http://qq.weather.com.cn";
    //private String url = "http://m.weather.com.cn";
    //private String url = "https://m.hao123.com/a/tianqi";
    private String url = "https://xw.tianqi.qq.com";
    //private String url = "https://x5.tencent.com/tbs/sdk.html";
    private ImageView back,refresh;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tbsContent = (com.tencent.smtt.sdk.WebView)findViewById(R.id.tbsContent);
        bar = (ProgressBar)findViewById(R.id.myProgressBar);

        tbsContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                view.loadUrl("javascript:function setTop(){document.querySelector('#link-back').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('#btn-share').style.display=\"none\";}setTop();");
                super.onProgressChanged(view, newProgress);
            }
        });
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
        webSettings.setSupportZoom(true);
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info.isAvailable())
        {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else
        {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);//不使用网络，只加载缓存
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(0);
        }
        //支持离线浏览
        webSettings.setAppCacheEnabled(true);
        tbsContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //https忽略证书问题
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:function setTop(){document.querySelector('#link-back').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('#btn-share').style.display=\"none\";}setTop();");
            }

        });
        tbsContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(tbsContent.getScrollY() <= 0){
                            //通知父控件不要干扰
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            //通知父控件不要干扰
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(tbsContent.getScrollY() <= 0){
                            //通知父控件不要干扰
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            //通知父控件不要干扰
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    default:
                        break;

                }
                return false;
            }
        });

        back = (ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refresh = (ImageView)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bar.setVisibility(View.VISIBLE);
                tbsContent.reload();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && tbsContent.canGoBack()) {
            tbsContent.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        // 当我们对Activity进行finish的时候，webview持有的页面并不会立即释放，如果页面中有在执行js等其他操作，仅仅进行finish是完全不够的。
        tbsContent.loadUrl("about:blank");
        super.finish();
    }
}
