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
import android.widget.RatingBar;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class Evaluate2Activity extends Activity {
    private ImageView back;
    private RatingBar ratingBar1,ratingBar2;
    private double score,score1,score2;
    private final int SETUSERSCORE = 1;
    private final int SETCOINS = 2;
    private String result,seller,buyer,amount,orderid;
    private Button send;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_evaluate2);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        seller = intent.getStringExtra("seller");
        buyer = intent.getStringExtra("buyer");
        orderid = intent.getStringExtra("orderid");
        back = (ImageView) findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        score1 = ratingBar1.getRating();
        ratingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                score1 = rating;
            }
        });
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        score2 = ratingBar1.getRating();
        ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                score2 = rating;
            }
        });

        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                score = score1+score2;
                mDialog = DialogUtils.createLoadingDialog(Evaluate2Activity.this, "请稍等...");
                setUserScore(buyer,score,orderid);
            }
        });
    }

    private void setUserScore(String username, double score,String orderid) {
        final String id = username;
        final String s = String.valueOf(score);
        final String o = orderid;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/setUserScore.jsp?username=" + id+"&score="+s+
                        "&orderid="+o;
                Message msg = new Message();
                msg.what = SETUSERSCORE;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void setCoins(String username, String amount) {
        final String id = username;
        final String a = amount;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/setCoins.jsp?username=" + id+"&amount="+a;
                Message msg = new Message();
                msg.what = SETCOINS;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private Handler handler;

    {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SETUSERSCORE:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if(result.equals("succeessful")) {
                            amount = String.valueOf((int)(Math.random()*500+1));
                            setCoins(seller, amount);
                            AlertDialog.Builder dialog = new AlertDialog.Builder(Evaluate2Activity.this);
                            dialog.setMessage("评价成功!随机获得"+amount+"个金币.");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(EvaluateActivity.this, "", Toast.LENGTH_SHORT).show();

//                                    Intent intent=new Intent();
//                                    intent.setClass(EvaluateActivity.this, MyOrdersSpecificActivity.class);
//                                    intent.putExtra("username",orderSpecificInfo.getBuyerid()); // 向下一个界面传递信息
//                                    startActivity(intent);
                                    finish();
                                }
                            });
                            dialog.show();
                        }
                        else {
                        }
                        result = "";
                        break;
                    case SETCOINS:
                        String response4 = (String) msg.obj;
                        parserXml(response4);
                        if(result.equals("succeessful")) {
                            DialogUtils.closeDialog(mDialog);
                        }
                        else {
                        }
                        result = "";
                        break;
                    default:
                        break;
                }
            }
        };
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
}
