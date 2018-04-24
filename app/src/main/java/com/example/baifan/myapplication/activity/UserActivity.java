package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.GoodsAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class UserActivity extends Activity {
    private ImageView back;
    private String username,headurl,result;
    private double buyerscore;
    private CircleImageView head;
    private RatingBar buyerRatingBar;
    private final int SEARCH_USER_SCORE = 1;
    private TextView tv_username;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        mDialog = DialogUtils.createLoadingDialog(UserActivity.this, "加载中...");
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        headurl = intent.getStringExtra("headurl");
        back=(ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_username = (TextView)findViewById(R.id.tv_username);
        tv_username.setText(username);
        head = (CircleImageView)findViewById(R.id.head);
        Glide.with(getApplicationContext()).load(headurl)
                .error(R.drawable.error)
                .into(head);
        buyerRatingBar = (RatingBar)findViewById(R.id.buyerRatingBar);
        searchUserScore(username);
    }

    private void searchUserScore(String username) {
        final String un = username;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/searchUserScore.jsp?username=" + un;
                Message msg = new Message();
                msg.what = SEARCH_USER_SCORE;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCH_USER_SCORE:
                    String response = (String) msg.obj;
                    parserXml(response);
                    if ("succeessful".equals(result)) {
                        if(buyerscore == 0) {
                            buyerRatingBar.setRating(5);
                        }else {
                            buyerRatingBar.setRating((float)buyerscore);
                        }
                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("当前网络状况不好，请稍后再试！");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        dialog.show();
                    }
                    result = "";
                    DialogUtils.closeDialog(mDialog);
                    break;
                default:
                    break;
            }
        }
    };

    private void parserXml(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取2个参数
                        if ("count".equals(nodeName)) {
                            String scoreStr = parse.nextText();
                            buyerscore = Double.parseDouble(scoreStr);
                        }
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                            Log.d("result", result);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parse.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
