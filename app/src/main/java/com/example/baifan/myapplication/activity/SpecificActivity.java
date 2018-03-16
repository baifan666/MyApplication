package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.rong.imkit.RongIM;


public class SpecificActivity extends Activity {
    private ImageView back,imageView1,imageView2;
    TextView username,publishtime,title,content,price,location,mobile;
    private String account,path1,path2,url1,url2;
    private Button conversation,buy;
    private GoodsInfo goodsInfo;
    private final int ADD_SUCCEESS = 1;
    private int flag1 = 0,flag2 = 0; //图片加载标记，0是加载中，1加载成功，2加载失败
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        goodsInfo = (GoodsInfo) intent.getSerializableExtra("goodsInfo");
        username = (TextView)findViewById(R.id.username);
        username.setText(goodsInfo.getUsername());
        publishtime = (TextView)findViewById(R.id.publish_time);
        publishtime.setText(String.valueOf(goodsInfo.getPublish_time()));
        title = (TextView)findViewById(R.id.title);
        title.setText(String.valueOf(goodsInfo.getTitle()));
        title.setMovementMethod(ScrollingMovementMethod.getInstance());
        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
        content = (TextView)findViewById(R.id.content);
        content.setText(String.valueOf(goodsInfo.getContent()));
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
        price = (TextView)findViewById(R.id.price);
        price.setText(String.valueOf(goodsInfo.getPrice()));
        location = (TextView)findViewById(R.id.location);
        location.setText(String.valueOf(goodsInfo.getLocation()));
        location.setMovementMethod(ScrollingMovementMethod.getInstance());
        location.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    //通知父控件不要干扰
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
        mobile = (TextView)findViewById(R.id.mobile);
        mobile.setText(String.valueOf(goodsInfo.getMobile()));
        path1 = goodsInfo.getPath1().substring(goodsInfo.getPath1().lastIndexOf("/")+1);
        path2 = goodsInfo.getPath2().substring(goodsInfo.getPath2().lastIndexOf("/")+1);
        imageView1 = (ImageView)findViewById(R.id.image_view1);
        imageView2 = (ImageView)findViewById(R.id.image_view2);
        if(!path1.equals("")) {
            url1 = "http://111.231.101.251:8080/fuwuduan/upload/"+path1;
            Glide.with(this).load(url1).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                    .listener( requestListener1 )
                    .error(R.drawable.error)//图片加载失败后，显示的图片
                    .into(imageView1);
        }else {
            Glide.with(this).load(R.drawable.good).into(imageView1);
        }
        if(!path2.equals("")) {
            url2 = "http://111.231.101.251:8080/fuwuduan/upload/"+path2;
            Glide.with(this).load(url2).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                    .listener( requestListener2 )
                    .error(R.drawable.error)//图片加载失败后，显示的图片
                    .into(imageView2);
        }else {
            Glide.with(this).load(R.drawable.good).into(imageView2);
        }
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag1 == 2) {
                    Glide.with(SpecificActivity.this).load(url1).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                            .listener( requestListener1 )
                            .error(R.drawable.error)//图片加载失败后，显示的图片
                            .into(imageView1);
                }else if (flag1 == 1) {
                    Intent intent=new Intent();
                    intent.setClass(SpecificActivity.this, PictureActivity.class);
                    intent.putExtra("url",url1); // 向下一个界面传递信息
                    startActivity(intent);
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag2 == 2) {
                    Glide.with(SpecificActivity.this).load(url2).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                            .listener( requestListener2 )
                            .error(R.drawable.error)//图片加载失败后，显示的图片
                            .into(imageView2);
                }else if (flag2 == 1) {
                    Intent intent=new Intent();
                    intent.setClass(SpecificActivity.this, PictureActivity.class);
                    intent.putExtra("url",url2); // 向下一个界面传递信息
                    startActivity(intent);
                }
            }
        });

        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        conversation = (Button)findViewById(R.id.conversation);
        buy = (Button)findViewById(R.id.buy);

        if(goodsInfo.getUsername().equals(account)) {
            conversation.setEnabled(false);
            buy.setEnabled(false);
        }
        conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RongIM.getInstance().startPrivateChat(SpecificActivity.this, goodsInfo.getUsername(), "聊天中");
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    /*
  *
  * 弹出对话框
  *
  * 弹出对话框的步骤：
  *  1.创建alertDialog的builder.
  *  2.要给builder设置属性, 对话框的内容,样式,按钮
  *  3.通过builder 创建一个对话框
  *  4.对话框show()出来
  */
    protected void showDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("二次确认");
        builer.setMessage("确定要购买吗？请务必和发布人确认清楚哦！");
        //当点确定按钮时
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addOredrs(goodsInfo.getId(), account, goodsInfo.getUsername());
            }
        });

        //当点取消按钮时
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    //设置错误监听
    private RequestListener<String, GlideDrawable> requestListener1 = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(SpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
                flag1= 2;
            } else if (e.toString() == "")
            Toast.makeText(SpecificActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            // important to return false so the error placeholder can be placed
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            flag1 = 1;
            return false;
        }
    };
    private RequestListener<String, GlideDrawable> requestListener2 = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(SpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
                flag2= 2;
            }
            //Toast.makeText(SpecificActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            // important to return false so the error placeholder can be placed
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            flag2 = 1;
            return false;
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_SUCCEESS:
                    String response = (String) msg.obj;
                    if (parserXml(response)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SpecificActivity.this);
                        dialog.setTitle("success");
                        dialog.setMessage("购买成功!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 购买成功后 跳转回首页
                                Intent intent = new Intent(SpecificActivity.this, SearchActivity.class);
                                intent.putExtra("account", account);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SpecificActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，系统出错了！");
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

    private boolean parserXml(String xmlData) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String result = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                        }
                        // 简单的判断物品是否发布成功
                        if (result.equals("succeessful"))
                            return true;
                        else if (result.equals("failed"))
                            return false;
                        break;
                }
                eventType = parse.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private void addOredrs(String a, String b, String c) {
        final String goods = a;
        final String buyer= b;
        final String seller = c;

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String goodsid = URLEncoder.encode(goods, "UTF-8");
                    String buyerid = URLEncoder.encode(buyer, "UTF-8");
                    String sellerid = URLEncoder.encode(seller, "UTF-8");
                    String url = "http://111.231.101.251:8080/fuwuduan/addOrders.jsp?goodsid=" + goodsid
                            + "&buyerid=" + buyerid + "&sellerid=" + sellerid;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = ADD_SUCCEESS;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
