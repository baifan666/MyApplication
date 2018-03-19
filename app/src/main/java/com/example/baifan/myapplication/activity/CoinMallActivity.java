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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

public class CoinMallActivity extends Activity {
    private ImageView back;
    private Button qiandao;
    private String result,count;
    private String username;
    private Dialog mDialog;
    private TextView textView,textView1,textView2,textView3,textView4;
    private final int ISSIGN = 1;
    private final int DISMISS = 2;
    private final int SIGN = 3;
    private final int SEARCHSIGN = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coin_mall);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        back = (ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        textView = (TextView)findViewById(R.id.textview);
        textView1 = (TextView)findViewById(R.id.textview1);
        textView2 = (TextView)findViewById(R.id.textview2);
        textView3 = (TextView)findViewById(R.id.textview3);
        textView4 = (TextView)findViewById(R.id.textview4);
        mDialog = DialogUtils.createLoadingDialog(CoinMallActivity.this, "正在获取签到信息...");
        isSign(username);
        searchSign(username);
        qiandao = (Button)findViewById(R.id.qiandao);
        qiandao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = DialogUtils.createLoadingDialog(CoinMallActivity.this, "正在签到...");
                userSign(username);
            }
        });
    }

    private void parserXml(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String str = String.format(" type = %d, str = %s\n", eventType, parse.getName());
            Log.d("xmlStr", str);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                            Log.d("result", result);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d("end_tag", "节点结束");
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

    private void parserXml1(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String str = String.format(" type = %d, str = %s\n", eventType, parse.getName());
            Log.d("xmlStr", str);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取2个参数
                        if ("count".equals(nodeName)) {
                            String countStr = parse.nextText();
                            count = countStr;
                            Log.d("count", countStr);
                        }
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                            Log.d("result", result);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d("end_tag", "节点结束");
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

    private Handler handler;

    {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ISSIGN:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if(result.equals("succeessful")) {
                            qiandao.setText("今日未签到");
                            handler.sendEmptyMessage(DISMISS);
                        }
                        else {
                            qiandao.setText("今日已签到");
                            qiandao.setVisibility(View.GONE);
                            handler.sendEmptyMessage(DISMISS);
                        }
                        break;
                    case DISMISS:
                        DialogUtils.closeDialog(mDialog);
                        break;
                    case SIGN:
                        String response1 = (String) msg.obj;
                        parserXml(response1);
                        if(result.equals("succeessful")) {
                            qiandao.setText("今日已签到");
                            qiandao.setVisibility(View.GONE);
                            handler.sendEmptyMessage(DISMISS);
                            AlertDialog.Builder dialog = new AlertDialog.Builder(CoinMallActivity.this);
                            dialog.setTitle("success");
                            dialog.setMessage("签到成功!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    searchSign(username);
                                    mDialog = DialogUtils.createLoadingDialog(CoinMallActivity.this, "正在获取签到信息...");
                                }
                            });
                            dialog.show();
                        }
                        else {
                            handler.sendEmptyMessage(DISMISS);
                            AlertDialog.Builder dialog = new AlertDialog.Builder(CoinMallActivity.this);
                            dialog.setTitle("failed");
                            dialog.setMessage("当前网络不稳定，签到失败。");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            dialog.show();
                        }
                        break;
                    case SEARCHSIGN:
                        String response3 = (String) msg.obj;
                        parserXml1(response3);
                        if(result.equals("succeessful")) {
                            textView.setText("已连续签到"+count+"天");
                            textView1.setText("第"+count+"天");
                            textView2.setText("第"+count+"天");
                            textView3.setText("第"+count+"天");
                            textView4.setText("第"+count+"天");
                            handler.sendEmptyMessage(DISMISS);
                        }
                        else {
                            handler.sendEmptyMessage(DISMISS);
                            AlertDialog.Builder dialog = new AlertDialog.Builder(CoinMallActivity.this);
                            dialog.setTitle("failed");
                            dialog.setMessage("当前网络不稳定，获取签到数据失败。");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            dialog.show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void isSign(String username) {
        final String id = username;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = "http://111.231.101.251:8080/fuwuduan/isSign.jsp?username=" + id;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void userSign(String username) {
        final String id = username;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = "http://111.231.101.251:8080/fuwuduan/isSign.jsp?username=" + id;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void searchSign(String username) {
        final String id = username;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = "http://111.231.101.251:8080/fuwuduan/searchSign.jsp?username=" + id;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void getCoins(String username) {
        final String id = username;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = "http://111.231.101.251:8080/fuwuduan/getUser.jsp?account=" + id;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

}
