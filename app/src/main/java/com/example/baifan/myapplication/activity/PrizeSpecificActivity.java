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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.PrizeInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class PrizeSpecificActivity extends Activity {
    private ImageView back,img;
    private PrizeInfo prizeInfo;
    private String path,url,result;
    private String coins;
    private String username;
    private TextView prizename,prizecoins,number,address,mobile,name;
    private Dialog mDialog;
    private Button duihuan;
    private final int DUIHUAN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prize_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        mDialog = DialogUtils.createLoadingDialog(PrizeSpecificActivity.this, "加载中...");
        back = (ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        prizeInfo = (PrizeInfo) intent.getSerializableExtra("prizeInfo");
        username = intent.getStringExtra("username");
        coins = intent.getStringExtra("coins");
        prizename = (TextView)findViewById(R.id.prizename);
        prizename.setText(prizeInfo.getPrizename());
        prizecoins = (TextView)findViewById(R.id.prizecoins);
        prizecoins.setText(String.valueOf(prizeInfo.getPrizecoins()));
        number = (TextView)findViewById(R.id.number);
        number.setText("剩余可兑换数量"+String.valueOf(prizeInfo.getNumber())+"个");
        path = prizeInfo.getPictureurl().substring(prizeInfo.getPictureurl().lastIndexOf("/")+1);
        url = SERVER_ADDRESS+"/prize/"+path;
        img = (ImageView)findViewById(R.id.img);
        Glide.with(PrizeSpecificActivity.this).load(url).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                .listener( requestListener )
                .error(R.drawable.error)//图片加载失败后，显示的图片
                .into(img);
        duihuan = (Button)findViewById(R.id.duihuan);
        duihuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText text = new EditText(PrizeSpecificActivity.this);
                LayoutInflater inflater = LayoutInflater.from(PrizeSpecificActivity.this);
                View view1 = inflater.inflate(R.layout.info_window, null);
                name = (TextView)view1.findViewById(R.id.name);
                mobile = (TextView)view1.findViewById(R.id.mobile);
                address = (TextView)view1.findViewById(R.id.address);
                new AlertDialog.Builder(PrizeSpecificActivity.this)
                        .setTitle("请正确输入相关消息")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(view1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 获取输入框的内容
                                Toast.makeText(PrizeSpecificActivity.this, name.getText().toString(), Toast.LENGTH_SHORT).show();
                                addDHJL(username,prizeInfo.getPrizeid(),prizeInfo.getPrizename(),mobile.getText().toString(),
                                        address.getText().toString(),name.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }

    //设置错误监听
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(PrizeSpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
            }
            // important to return false so the error placeholder can be placed
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog);
            return false;
        }
    };


    private void addDHJL(String username,String prizeid,String prizename,String mobile,
                             String address,String name) {
        final String user = username;
        final String prize= prizeid;
        final String prizen = prizename;
        final String mob = mobile;
        final String add = address;
        final String na = name;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String user1 = URLEncoder.encode(user, "UTF-8");
                    String prize1 = URLEncoder.encode(prize, "UTF-8");
                    String prizen1 = URLEncoder.encode(prizen, "UTF-8");
                    String mob1 = URLEncoder.encode(mob, "UTF-8");
                    String add1 = URLEncoder.encode(add, "UTF-8");
                    String na1 = URLEncoder.encode(na, "UTF-8");
                    String url = SERVER_ADDRESS+"/addDHJL.jsp?username=" + user1
                            + "&prizeid=" + prize1 + "&prizename=" + prizen1 + "&mobile=" + mob1
                            + "&address=" + add1 + "&name=" + na1;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = DUIHUAN;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }).start();
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DUIHUAN:
                    String response = (String) msg.obj;
                    parserXml(response);
                    if ("succeessful".equals(result)) {
                        DialogUtils.closeDialog(mDialog);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(PrizeSpecificActivity.this);
                        dialog.setTitle("Success!");
                        dialog.setMessage("兑换成功!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 注册成功后 跳转至登录界面
                                //Intent intent = new Intent(PrizeSpecificActivity.this, MainActivity.class);
                                //startActivity(intent);
                            }
                        });
                        dialog.show();
                    } else {
                        DialogUtils.closeDialog(mDialog);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(PrizeSpecificActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，兑换失败!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    }
                    break;
            }
        }
    };
}
