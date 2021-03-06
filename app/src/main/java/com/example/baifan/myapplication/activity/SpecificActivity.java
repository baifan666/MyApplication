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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.utils.AddMessageUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imkit.RongIM;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;
import static com.youth.banner.BannerConfig.CENTER;
import static com.youth.banner.BannerConfig.CIRCLE_INDICATOR;


public class SpecificActivity extends Activity {
    private ImageView back,share,comment;
    private TextView username,publishtime,title,content,price,location,mobile;
    private EditText usermobile;
    private String account,path1,path2,url1="",url2="",result,buyermobile,headurl;
    private Button conversation,buy;
    private GoodsInfo goodsInfo;
    private final int ADD_SUCCEESS = 1;
    private final int SEARCHSELLERSCORE = 2;
    private RatingBar ratingBar;
    private double score;
    private Dialog mDialog,mDialog1;
    private Banner banner;
    private List<String> images= new ArrayList<String>();       //设置图片集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_specific);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);

        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        headurl = intent.getStringExtra("headurl");
        goodsInfo = (GoodsInfo) intent.getSerializableExtra("goodsInfo");
        username = (TextView)findViewById(R.id.username);
        username.setText(goodsInfo.getUsername());
        publishtime = (TextView)findViewById(R.id.publish_time);
        publishtime.setText(String.valueOf(goodsInfo.getPublish_time()));
        title = (TextView)findViewById(R.id.title);
        title.setText(String.valueOf(goodsInfo.getTitle()));
        title.setMovementMethod(ScrollingMovementMethod.getInstance());
        title.post(new Runnable() {
            @Override
            public void run() {
                if (title.getLineCount() > 1) {
                    title.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                //通知父控件不要干扰
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                                //通知父控件不要干扰
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                view.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            return false;
                        }
                    });
                }
            }
        });

        content = (TextView)findViewById(R.id.content);
        content.setText(String.valueOf(goodsInfo.getContent()));
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        content.post(new Runnable() {
            @Override
            public void run() {
                if (content.getLineCount() > 9) {
                    content.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                //通知父控件不要干扰
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                                //通知父控件不要干扰
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                view.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            return false;
                        }
                    });
                }
            }
        });
        price = (TextView)findViewById(R.id.price);
        price.setText(String.valueOf(goodsInfo.getPrice()));
        location = (TextView) findViewById(R.id.location);
        location.setText(String.valueOf(goodsInfo.getLocation()));
        location.setMovementMethod(ScrollingMovementMethod.getInstance());
        location.post(new Runnable() {
            @Override
            public void run() {
                if (location.getLineCount() > 1) {
                    location.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                //通知父控件不要干扰
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                                //通知父控件不要干扰
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                view.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            return false;
                        }
                    });
                }
            }
        });
        mobile = (TextView)findViewById(R.id.mobile);
        mobile.setText(String.valueOf(goodsInfo.getMobile()));
        path1 = goodsInfo.getPath1().substring(goodsInfo.getPath1().lastIndexOf("/") + 1);
        path2 = goodsInfo.getPath2().substring(goodsInfo.getPath2().lastIndexOf("/") + 1);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        searchSellerScore(goodsInfo.getUsername());
        if (!"".equals(path1)) {
            mDialog = DialogUtils.createLoadingDialog(SpecificActivity.this, "加载中...");
            url1 = SERVER_ADDRESS + "/upload/" + path1;
            images.add(url1);
        }
        if (!"".equals(path2)) {
            mDialog1 = DialogUtils.createLoadingDialog(SpecificActivity.this, "加载中...");
            url2 = SERVER_ADDRESS + "/upload/" + path2;
            images.add(url2);
        }

        back = (ImageView) findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        conversation = (Button) findViewById(R.id.conversation);
        buy = (Button) findViewById(R.id.buy);

        if (goodsInfo.getUsername().equals(account)) {
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
                LayoutInflater inflater = LayoutInflater.from(SpecificActivity.this);
                View view1 = inflater.inflate(R.layout.mobile_window, null);
                usermobile = (EditText) view1.findViewById(R.id.mobile);
                new AlertDialog.Builder(SpecificActivity.this)
                        .setTitle("请正确输入联系方式")
                        .setIcon(R.drawable.zhuyi)
                        .setView(view1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 获取输入框的内容
                                if ("".equals(usermobile.getText().toString())) {
                                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(SpecificActivity.this);
                                    dialog1.setTitle("This is a warnining!");
                                    dialog1.setMessage("请确保每一个信息已输入！");
                                    dialog1.setCancelable(false);
                                    dialog1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    dialog1.show();
                                } else {
                                    buyermobile = usermobile.getText().toString();
                                    showDialog();
                                }
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

        banner = (Banner)findViewById(R.id.banner);
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
        banner.setDelayTime(3500);
        //自定义图片加载框架
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                String p = String.valueOf(path);
                Glide.with(SpecificActivity.this).load(p).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                        .listener(requestListener)
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
                Intent intent = new Intent(SpecificActivity.this, PictureActivity.class);
                intent.putExtra("url", images.get(position)); // 向下一个界面传递信息
                startActivity(intent);
            }
        });


        share = (ImageView) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecificActivity.this, ShareSelectActivity.class);
                intent.putExtra("title",goodsInfo.getTitle().toString() ); // 向下一个界面传递信息
                intent.putExtra("content",goodsInfo.getContent().toString() ); // 向下一个界面传递信息
                intent.putExtra("url1",url1 ); // 向下一个界面传递信息
                intent.putExtra("url2",url2 ); // 向下一个界面传递信息
                startActivity(intent);
            }
        });

        comment = (ImageView) findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecificActivity.this, CommentActivity.class);
                intent.putExtra("account",account); // 向下一个界面传递信息
                intent.putExtra("goodsid",goodsInfo.getId()); // 向下一个界面传递信息
                intent.putExtra("headurl",headurl);
                intent.putExtra("title",goodsInfo.getTitle());
                intent.putExtra("seller",goodsInfo.getUsername());
                startActivity(intent);
            }
        });

    }


    //设置错误监听
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog);
            DialogUtils.closeDialog(mDialog1);
            banner.startAutoPlay();//开始轮播
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
        builer.setMessage("请务必确认手机号正确，并且和物品发布人确认清楚！");
        //当点确定按钮时
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addOredrs(goodsInfo.getId(), account, goodsInfo.getUsername(),buyermobile);
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
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                //获取当前时间
                                Date date = new Date(System.currentTimeMillis());
                                String str = simpleDateFormat.format(date)+"用户"+account+"购买了你的《"+goodsInfo.getTitle()+"》中的物品";
                                //发送系统消息
                                AddMessageUtils.addMessage(goodsInfo.getUsername(),str);
                                String str1 = simpleDateFormat.format(date)+"你购买了《"+goodsInfo.getTitle()+"》中的物品";
                                AddMessageUtils.addMessage(account,str1);
                                // 购买成功后 跳转回首页
                                Intent intent = new Intent(SpecificActivity.this, SearchActivity.class);
                                intent.putExtra("account", account);
                                intent.putExtra("headurl",headurl);
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

    private void addOredrs(String a, String b, String c,String d) {
        final String goods = a;
        final String buyer= b;
        final String seller = c;
        final String buyermobile = d;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String goodsid = URLEncoder.encode(goods, "UTF-8");
                    String buyerid = URLEncoder.encode(buyer, "UTF-8");
                    String sellerid = URLEncoder.encode(seller, "UTF-8");
                    String buyermobile1 = URLEncoder.encode(buyermobile,"UTF-8");
                    String url = SERVER_ADDRESS+"/addOrders.jsp?goodsid=" + goodsid
                            + "&buyerid=" + buyerid + "&sellerid=" + sellerid+"&buyermobile="+buyermobile1;
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
    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(null);
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(null);
        }
        share = null;
        comment = null;
        username = null;
        publishtime = null;
        title = null;
        content = null;
        price = null;
        location = null;
        mobile = null;
        usermobile = null;
        conversation = null;
        buy = null;
        goodsInfo = null;
        ratingBar = null;
        banner = null;
        images = null;
        Glide.get(SpecificActivity.this).clearMemory();
        super.finish();
    }
}
