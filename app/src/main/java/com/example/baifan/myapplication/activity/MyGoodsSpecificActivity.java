package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.example.baifan.myapplication.utils.HttpUtils;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;
import static com.youth.banner.BannerConfig.CENTER;
import static com.youth.banner.BannerConfig.CIRCLE_INDICATOR;


public class MyGoodsSpecificActivity extends Activity {
    private ImageView back;
    private TextView username,publishtime,title,content,price,location,mobile;
    private String path1,path2,url1,url2,goodsid;
    private Button shanchu,xiugai;
    private GoodsInfo goodsInfo;
    private int flag1 = 0,flag2 = 0; //图片加载标记，0是加载中，1加载成功，2加载失败
    private Dialog mDialog,mDialog1;
    private Banner banner;
    private List<String> images= new ArrayList<String>();       //设置图片集合
    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_goods_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        goodsInfo = (GoodsInfo) intent.getSerializableExtra("goodsInfo");
        goodsid = goodsInfo.getId();
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
        if(!"".equals(path1)) {
            mDialog = DialogUtils.createLoadingDialog(MyGoodsSpecificActivity.this, "加载中...");
            url1 = SERVER_ADDRESS + "/upload/" + path1;
            images.add(url1);
        }
        if (!"".equals(path2)) {
            mDialog1 = DialogUtils.createLoadingDialog(MyGoodsSpecificActivity.this, "加载中...");
            url2 = SERVER_ADDRESS+"/upload/"+path2;
            images.add(url2);
        }

        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        shanchu = (Button)findViewById(R.id.shanchu);
        shanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        xiugai = (Button)findViewById(R.id.xiugai);
        xiugai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyGoodsSpecificActivity.this, AlterGoodsActivity.class);
                intent.putExtra("title",title.getText().toString()); // 向下一个界面传递信息  标题
                intent.putExtra("content",content.getText().toString());  //描述
                intent.putExtra("price",price.getText().toString());   //价格
                intent.putExtra("location",location.getText().toString());    //地点
                intent.putExtra("mobile",mobile.getText().toString());      //联系方式
                intent.putExtra("path1",goodsInfo.getPath1());    //图片数据库原始字段
                intent.putExtra("path2",goodsInfo.getPath2());    //图片数据库原始字段
                intent.putExtra("url1",url1);    //图片url
                intent.putExtra("url2",url2);    //图片url
                intent.putExtra("goodsid",goodsid);    //发布内容数据库对应的主键id
                startActivity(intent);
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
        banner.setDelayTime(3000);
        //自定义图片加载框架
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                String p = String.valueOf(path);
                Glide.with(getApplicationContext()).load(p).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                        .listener( requestListener )
                        .error(R.drawable.error)//图片加载失败后，显示的图片
                        .into(imageView);
            }
        });
        //设置图片资源:url或本地资源
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(MyGoodsSpecificActivity.this, PictureActivity.class);
                intent.putExtra("url",images.get(position)); // 向下一个界面传递信息
                startActivity(intent);
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
        builer.setMessage("确定要删除吗？删除后目前系统无法恢复哦！");
        //当点确定按钮时
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteGoods(goodsInfo.getId());
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
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(MyGoodsSpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
            }
            DialogUtils.closeDialog(mDialog);
            DialogUtils.closeDialog(mDialog1);
            // important to return false so the error placeholder can be placed
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog);
            DialogUtils.closeDialog(mDialog1);
            return false;
        }
    };

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

    private Handler handler;

    {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if(result.equals("succeessful")) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MyGoodsSpecificActivity.this);
                            dialog.setMessage("删除成功!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent();
                                   // intent.setClass(MyGoodsSpecificActivity.this, MyGoodsActivity.class);
                                    intent.putExtra("username",username.getText().toString());
                                    MyGoodsSpecificActivity.this.setResult(1, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理
                                    //startActivity(intent);
                                    finish();
                                }
                            });
                            dialog.show();
                        }
                        else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MyGoodsSpecificActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("对不起，删除失败!");
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

    private void deleteGoods(String goodsid) {
        final String id = goodsid;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/deleteGoods.jsp?id=" + id;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

}
