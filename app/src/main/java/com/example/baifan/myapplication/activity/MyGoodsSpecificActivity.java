package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



public class MyGoodsSpecificActivity extends Activity {
    private ImageView back,imageView1,imageView2;
    TextView username,publishtime,title,content,price,location,mobile;
    private String path1,path2,url1,url2;
    private Button shanchu,xiugai;
    private GoodsInfo goodsInfo;
    private int flag1 = 0,flag2 = 0; //图片加载标记，0是加载中，1加载成功，2加载失败
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
        username = (TextView)findViewById(R.id.username);
        username.setText(goodsInfo.getUsername());
        publishtime = (TextView)findViewById(R.id.publish_time);
        publishtime.setText(String.valueOf(goodsInfo.getPublish_time()));
        title = (TextView)findViewById(R.id.title);
        title.setText(String.valueOf(goodsInfo.getTitle()));
        content = (TextView)findViewById(R.id.content);
        content.setText(String.valueOf(goodsInfo.getContent()));
        price = (TextView)findViewById(R.id.price);
        price.setText(String.valueOf(goodsInfo.getPrice()));
        location = (TextView)findViewById(R.id.location);
        location.setText(String.valueOf(goodsInfo.getLocation()));
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
                    Glide.with(MyGoodsSpecificActivity.this).load(url1).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                            .listener( requestListener1 )
                            .error(R.drawable.error)//图片加载失败后，显示的图片
                            .into(imageView1);
                }else if (flag1 == 1) {
                    LayoutInflater inflater = LayoutInflater.from(MyGoodsSpecificActivity.this);
                    View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                    final AlertDialog dialog = new AlertDialog.Builder(MyGoodsSpecificActivity.this).create();
                    ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                    Glide.with(MyGoodsSpecificActivity.this).load(url1).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                            .into(img);
                    dialog.show();
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    // 设置dialog的宽高为屏幕的宽高
                    ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
                    dialog.setContentView(imgEntryView, layoutParams);
                    imgEntryView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View paramView) {
                            dialog.cancel();
                        }
                    });
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag2 == 2) {
                    Glide.with(MyGoodsSpecificActivity.this).load(url2).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                            .listener( requestListener2 )
                            .error(R.drawable.error)//图片加载失败后，显示的图片
                            .into(imageView2);
                }else if (flag2 == 1) {
                    LayoutInflater inflater = LayoutInflater.from(MyGoodsSpecificActivity.this);
                    View imgEntryView = inflater.inflate(R.layout.dialog_photo_entry, null); // 加载自定义的布局文件
                    final AlertDialog dialog = new AlertDialog.Builder(MyGoodsSpecificActivity.this).create();
                    ImageView img = (ImageView)imgEntryView.findViewById(R.id.large_image);
                    Glide.with(MyGoodsSpecificActivity.this).load(url2).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                            .into(img);
                    dialog.show();
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    // 设置dialog的宽高为屏幕的宽高
                    ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(dm.widthPixels, dm.heightPixels);
                    dialog.setContentView(imgEntryView, layoutParams);
                    imgEntryView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View paramView) {
                            dialog.cancel();
                        }
                    });
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
    private RequestListener<String, GlideDrawable> requestListener1 = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(MyGoodsSpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
                flag1= 2;
            } else if (e.toString() == "")
                Toast.makeText(MyGoodsSpecificActivity.this,e.toString(),Toast.LENGTH_LONG).show();
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
                Toast.makeText(MyGoodsSpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
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
                                    intent.setClass(MyGoodsSpecificActivity.this, MyGoodsActivity.class);
                                    intent.putExtra("username",username.getText().toString()); // 向下一个界面传递信息
                                    startActivity(intent);
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
                String url = "http://111.231.101.251:8080/fuwuduan/deleteGoods.jsp?id=" + id;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

}