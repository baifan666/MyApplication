package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.utils.DialogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.example.baifan.myapplication.common.ServerAddress.ZAFU_ADDRESS;

public class NoticeSpecificActivity extends Activity {
    private ImageView back;
    private String NoticeUrl,str;
    private final int READALL = 1;
    private TextView content,title,url;
    private Dialog mDialog;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice_specific);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        intent = getIntent();
        NoticeUrl = intent.getStringExtra("NoticeUrl");
        content = (TextView) findViewById(R.id.content);
        title = (TextView)findViewById(R.id.title);
        url = (TextView)findViewById(R.id.url);
        back = (ImageView) findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mDialog = DialogUtils.createLoadingDialog(NoticeSpecificActivity.this, "加载中...");
        getNotices();//从网页抓取信息
    }

    private void getNotices(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect(NoticeUrl).get();
                    str = "";
                    //获得p的元素集合
                    Elements elements = doc.select("#vsb_content > p");
                    if(TextUtils.isEmpty(elements.text())) {
                        elements = doc.select("#vsb_content > font > p");
                    }
                    if(TextUtils.isEmpty(elements.text())) {
                        elements = doc.select("#vsb_content > span > span > p");
                    }
                    for (Element element : elements) {
                        str += element.text();
                        str +="\n";
                    }

                    Message msg = new Message();
                    msg.what = READALL;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READALL:
                    content.setText(str);
                    title.setText(intent.getStringExtra("title"));
                    url.setText(NoticeUrl);
                    DialogUtils.closeDialog(mDialog);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
