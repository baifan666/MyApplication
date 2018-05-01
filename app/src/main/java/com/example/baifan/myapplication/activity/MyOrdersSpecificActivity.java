package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.model.OrderSpecificInfo;
import com.example.baifan.myapplication.utils.AddMessageUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imkit.RongIM;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;
import static com.youth.banner.BannerConfig.CENTER;
import static com.youth.banner.BannerConfig.CIRCLE_INDICATOR;

public class MyOrdersSpecificActivity extends Activity {
    private OrderSpecificInfo orderSpecificInfo;
    private ImageView back;
    private TextView seller,mobile,isfinish,title,content,price,location,dingdanbianhao,goumaishijian,wanchengshijian;
    private String path1,path2,url1,url2;
    private Button finish,conversation,copy;
    private String result;
    private int isEvaluate = 0;
    private Dialog mDialog,mDialog1,mDialog2;
    private final int FINISHORDER = 1;
    private final int SEARCHBUYEREVALUATE = 2;
    private Banner banner;
    List<String> images= new ArrayList<String>();       //设置图片集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_orders_specific);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        Intent intent = getIntent();
        orderSpecificInfo = (OrderSpecificInfo) intent.getSerializableExtra("orderSpecificInfo");
        seller = (TextView)findViewById(R.id.seller);
        seller.setText(orderSpecificInfo.getUsername());
        mobile = (TextView)findViewById(R.id.mobile);
        mobile.setText(orderSpecificInfo.getMobile());
        isfinish = (TextView)findViewById(R.id.isfinish);
        title = (TextView)findViewById(R.id.title);
        title.setText(orderSpecificInfo.getTitle());
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
        content.setText(orderSpecificInfo.getContent());
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
        location = (TextView)findViewById(R.id.location);
        location.setText(orderSpecificInfo.getLocation());
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
        price = (TextView)findViewById(R.id.price);
        price.setText(String.valueOf(orderSpecificInfo.getPrice()));
        dingdanbianhao = (TextView)findViewById(R.id.dingdanbianhao);
        dingdanbianhao.setText("订单编号："+orderSpecificInfo.getOrderid());
        goumaishijian = (TextView)findViewById(R.id.goumaishijian);
        goumaishijian.setText("购买时间："+orderSpecificInfo.getOrdertime());
        wanchengshijian = (TextView)findViewById(R.id.wanchengshijian);
        path1 = orderSpecificInfo.getPath1().substring(orderSpecificInfo.getPath1().lastIndexOf("/")+1);
        path2 = orderSpecificInfo.getPath2().substring(orderSpecificInfo.getPath2().lastIndexOf("/")+1);
        finish = (Button)findViewById(R.id.finish);
        if("0".equals(orderSpecificInfo.getIsfinish())) {
            isfinish.setText("订单进行中");
            wanchengshijian.setVisibility(View.GONE);
        }else {
            isfinish.setText("订单已完成");
            wanchengshijian.setText("完成时间："+orderSpecificInfo.getFinishtime());
            //finish.setVisibility(View.GONE);
            mDialog = DialogUtils.createLoadingDialog(MyOrdersSpecificActivity.this, "请稍等...");
            searchBuyerEvaluate(orderSpecificInfo.getOrderid());
        }
        if(!"".equals(path1)) {
            mDialog1 = DialogUtils.createLoadingDialog(MyOrdersSpecificActivity.this, "加载中...");
            url1 = SERVER_ADDRESS + "/upload/" + path1;
            images.add(url1);
        }
        if (!"".equals(path2)) {
            mDialog2 = DialogUtils.createLoadingDialog(MyOrdersSpecificActivity.this, "加载中...");
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
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("0".equals(orderSpecificInfo.getIsfinish())) {
                    showDialog();
                }else {
                    Intent i = new Intent();
                    i.setClass(MyOrdersSpecificActivity.this, EvaluateActivity.class);
                    // 传递卖家信息
                    i.putExtra("seller",seller.getText().toString());
                    //传递买家信息
                    i.putExtra("buyer",orderSpecificInfo.getBuyerid());
                    //传递订单编号
                    i.putExtra("orderid",orderSpecificInfo.getOrderid());
                    startActivityForResult(i, 0x1);
                }
            }
        });
        conversation = (Button)findViewById(R.id.conversation);
        conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RongIM.getInstance().startPrivateChat(MyOrdersSpecificActivity.this, orderSpecificInfo.getUsername(), "聊天中");
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
                Intent intent = new Intent(MyOrdersSpecificActivity.this, PictureActivity.class);
                intent.putExtra("url",images.get(position)); // 向下一个界面传递信息
                startActivity(intent);
            }
        });

        copy = (Button)findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", dingdanbianhao.getText().toString().substring(5));
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MyOrdersSpecificActivity.this, "订单号已复制成功！", Toast.LENGTH_LONG).show();
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
        builer.setMessage("确认完成订单吗？请确保你和卖家的线下交易已经完成");
        //当点确定按钮时
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               finishOrder(orderSpecificInfo.getOrderid());
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
                            String scoreStr = parse.nextText();
                            isEvaluate = Integer.parseInt(scoreStr);
                            Log.d("isEvaluate", String.valueOf(isEvaluate));
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
                    case FINISHORDER:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if(result.equals("succeessful")) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrdersSpecificActivity.this);
                            dialog.setMessage("确认成功，订单已完成!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                    //获取当前时间
                                    Date date = new Date(System.currentTimeMillis());
                                    String str = simpleDateFormat.format(date)+"用户"+orderSpecificInfo.getBuyerid()+"确认完成了你的《"+orderSpecificInfo.getTitle()+"》中的物品订单";
                                    //发送系统消息
                                    AddMessageUtils.addMessage(orderSpecificInfo.getUsername(),str);
                                    String str1 = simpleDateFormat.format(date)+"你确认了《"+orderSpecificInfo.getTitle()+"》中的物品订单";
                                    AddMessageUtils.addMessage(orderSpecificInfo.getBuyerid(),str1);

                                    Intent intent=new Intent();
                                    intent.setClass(MyOrdersSpecificActivity.this, MyOrdersActivity.class);
                                    intent.putExtra("username",orderSpecificInfo.getBuyerid()); // 向下一个界面传递信息
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            dialog.show();
                        }
                        else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrdersSpecificActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("对不起，确认完成失败!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            dialog.show();
                        }
                        result = "";
                        break;
                    case SEARCHBUYEREVALUATE:
                        String response1 = (String) msg.obj;
                        parserXml1(response1);
                        if(result.equals("succeessful")) {
                            if(0 == isEvaluate) {
                                finish.setText("去评价");
                            }else {
                                finish.setText("已评价");
                                finish.setEnabled(false);
                            }
                            DialogUtils.closeDialog(mDialog);
                        }
                        else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrdersSpecificActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("当前网络异常，请稍后再试！");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            dialog.show();
                            DialogUtils.closeDialog(mDialog);
                        }
                        result = "";
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void finishOrder(String orderid) {
        final String id = orderid;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/finishOrder.jsp?orderid=" + id;
                Message msg = new Message();
                msg.what = FINISHORDER;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void searchBuyerEvaluate(String orderid) {
        final String o = orderid;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/searchBuyerEvaluate.jsp?orderid=" + o;
                Message msg = new Message();
                msg.what = SEARCHBUYEREVALUATE;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    //设置图片加载监听
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog1);
            DialogUtils.closeDialog(mDialog2);
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog1);
            DialogUtils.closeDialog(mDialog2);
            return false;
        }
    };

    // 响应startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    //处理代码在此地
                    if(!TextUtils.isEmpty(bundle.getString("return"))) {// 得到子窗口ChildActivity的回传数据
                        finish.setText("已评价");
                        finish.setEnabled(false);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
