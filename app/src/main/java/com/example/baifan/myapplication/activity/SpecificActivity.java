package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.GlideImageLoader;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.youth.banner.Banner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;
import static com.youth.banner.BannerConfig.CENTER;
import static com.youth.banner.BannerConfig.CIRCLE_INDICATOR;


public class SpecificActivity extends Activity {
    private ImageView back,imageView1,imageView2;
    private TextView username,publishtime,title,content,price,location,mobile;
    private String account,path1,path2,url1,url2,result;
    private Button conversation,buy;
    private GoodsInfo goodsInfo;
    private final int ADD_SUCCEESS = 1;
    private final int SEARCHSELLERSCORE = 2;
    private int flag1 = 0,flag2 = 0; //图片加载标记，0是加载中，1加载成功，2加载失败
    private RatingBar ratingBar;
    private double score;
    private Dialog mDialog;
    private Banner banner;
    List<String> images= new ArrayList<String>();       //设置图片集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        mDialog = DialogUtils.createLoadingDialog(SpecificActivity.this, "加载中...");
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
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        searchSellerScore(goodsInfo.getUsername());

        if(!path1.equals("")) {
            url1 = SERVER_ADDRESS+"/upload/"+path1;
            images.add(url1);
            Glide.with(this).load(url1).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                    .listener( requestListener1 )
                    .error(R.drawable.error)//图片加载失败后，显示的图片
                    .into(imageView1);
        }else {
            DialogUtils.closeDialog(mDialog);
            Glide.with(this).load(R.drawable.good).into(imageView1);
        }
        if(!path2.equals("")) {
            url2 = SERVER_ADDRESS+"/upload/"+path2;
            images.add(url2);
            Glide.with(this).load(url2).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                    .listener( requestListener2 )
                    .error(R.drawable.error)//图片加载失败后，显示的图片
                    .into(imageView2);
        }else {
            DialogUtils.closeDialog(mDialog);
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
                RongIM.getInstance().startPrivateChat(SpecificActivity.this, goodsInfo.getUsername(), goodsInfo.getUsername());
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        banner = (Banner) findViewById(R.id.banner);
        //BannerConfig.NOT_INDICATOR	不显示指示器和标题	setBannerStyle
        //BannerConfig.CIRCLE_INDICATOR	显示圆形指示器	setBannerStyle
        //BannerConfig.NUM_INDICATOR	显示数字指示器	setBannerStyle
        //BannerConfig.NUM_INDICATOR_TITLE	显示数字指示器和标题	setBannerStyle
        //BannerConfig.CIRCLE_INDICATOR_TITLE	显示圆形指示器和标题（垂直显示）	setBannerStyle
        //BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE	显示圆形指示器和标题（水平显示）	setBannerStyle
        //BannerConfig.LEFT	指示器居左	setIndicatorGravity
        //BannerConfig.CENTER	指示器居中	setIndicatorGravity
        //BannerConfig.RIGHT	指示器居右	setIndicatorGravity
        banner.setBannerStyle(CIRCLE_INDICATOR);
        banner.setIndicatorGravity(CENTER);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片资源:url或本地资源
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

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
            DialogUtils.closeDialog(mDialog);
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
                case SEARCHSELLERSCORE:
                    String response1 = (String) msg.obj;
                    parserXml1(response1);
                    if ("succeessful".equals(result)) {
                        if(score == 0) {
                            ratingBar.setRating(5);
                        }else {
                            ratingBar.setRating((float)score);
                        }
                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SpecificActivity.this);
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
                    break;
                default:
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
                        // 简单的判断是否成功
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
                            String scoreStr = parse.nextText();
                            score = Double.parseDouble(scoreStr);
                            Log.d("score", String.valueOf(score));
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
                    String url = SERVER_ADDRESS+"/addOrders.jsp?goodsid=" + goodsid
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

    private void searchSellerScore(String username) {
        final String un = username;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/searchSellerScore.jsp?username=" + un;
                Message msg = new Message();
                msg.what = SEARCHSELLERSCORE;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

}
