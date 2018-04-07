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
import com.example.baifan.myapplication.model.OrderSpecificInfo;
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

import io.rong.imkit.RongIM;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;
import static com.youth.banner.BannerConfig.CENTER;
import static com.youth.banner.BannerConfig.CIRCLE_INDICATOR;

public class MySellsSpecificActivity extends Activity {
    private OrderSpecificInfo orderSpecificInfo;
    private ImageView back;
    private TextView buyer,mobile,isfinish,title,content,price,location,dingdanbianhao,shoushushijian,wanchengshijian;
    private String path1,path2,url1,url2;
    private Button evaluate,conversation,copy;
    private String result;
    private int isEvaluate = 0;
    private Dialog mDialog,mDialog1,mDialog2;
    private final int SEARCHSELLEREVALUATE = 1;
    private Banner banner;
    List<String> images= new ArrayList<String>();       //设置图片集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_sells_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        orderSpecificInfo = (OrderSpecificInfo) intent.getSerializableExtra("orderSpecificInfo");
        buyer = (TextView)findViewById(R.id.buyer);
        buyer.setText(orderSpecificInfo.getBuyerid());
        mobile = (TextView)findViewById(R.id.mobile);
        mobile.setText(orderSpecificInfo.getBuyermobile());
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
        wanchengshijian = (TextView)findViewById(R.id.wanchengshijian);
        shoushushijian = (TextView)findViewById(R.id.shouchushijian);
        shoushushijian.setText("售出时间："+orderSpecificInfo.getOrdertime());
        path1 = orderSpecificInfo.getPath1().substring(orderSpecificInfo.getPath1().lastIndexOf("/")+1);
        path2 = orderSpecificInfo.getPath2().substring(orderSpecificInfo.getPath2().lastIndexOf("/")+1);
        evaluate = (Button)findViewById(R.id.evaluate);
        if("0".equals(orderSpecificInfo.getIsfinish())) {
            isfinish.setText("订单进行中");
            wanchengshijian.setVisibility(View.GONE);
            evaluate.setEnabled(false);
        }else {
            isfinish.setText("订单已完成");
            wanchengshijian.setText("完成时间："+orderSpecificInfo.getFinishtime());
            //finish.setVisibility(View.GONE);
            mDialog = DialogUtils.createLoadingDialog(MySellsSpecificActivity.this, "请稍等...");
            searchSellerEvaluate(orderSpecificInfo.getOrderid());
        }
        if(!"".equals(path1)) {
            mDialog1 = DialogUtils.createLoadingDialog(MySellsSpecificActivity.this, "加载中...");
            url1 = SERVER_ADDRESS + "/upload/" + path1;
            images.add(url1);
        }
        if (!"".equals(path2)) {
            mDialog2 = DialogUtils.createLoadingDialog(MySellsSpecificActivity.this, "加载中...");
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

        evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(MySellsSpecificActivity.this, Evaluate2Activity.class);
                // 传递买家信息
                i.putExtra("buyer",buyer.getText().toString());
                //传递卖家信息
                i.putExtra("seller",orderSpecificInfo.getUsername());
                //传递订单编号
                i.putExtra("orderid",orderSpecificInfo.getOrderid());
                startActivity(i);
            }
        });

        conversation = (Button)findViewById(R.id.conversation);
        conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RongIM.getInstance().startPrivateChat(MySellsSpecificActivity.this, orderSpecificInfo.getBuyerid(), "聊天中");
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
                Intent intent = new Intent(MySellsSpecificActivity.this, PictureActivity.class);
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
                ClipData mClipData = ClipData.newPlainText("Label", dingdanbianhao.getText().toString().substring(dingdanbianhao.getText().toString().length()-14));
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MySellsSpecificActivity.this, "订单号已复制成功！", Toast.LENGTH_LONG).show();
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
                    case SEARCHSELLEREVALUATE:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if(result.equals("succeessful")) {
                            if(0 == isEvaluate) {
                                evaluate.setText("去评价");
                            }else {
                                evaluate.setText("已评价");
                                evaluate.setEnabled(false);
                            }
                            DialogUtils.closeDialog(mDialog);
                        }
                        else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MySellsSpecificActivity.this);
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

    private void searchSellerEvaluate(String orderid) {
        final String o = orderid;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/searchSellerEvaluate.jsp?orderid=" + o;
                Message msg = new Message();
                msg.what = SEARCHSELLEREVALUATE;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    //设置错误监听
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(MySellsSpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
            }
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
}
