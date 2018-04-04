package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.MyDHJLAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.DHJLInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class RecordActivity extends Activity {
    private ImageView back;
    private Dialog mDialog;
    private ListView listrecord;
    private String username;
    private int flag = 0;//倘若前一页面有flag传递进来，表示是管理员登陆
    private RefreshLayout refreshLayout;

    // 物品显示列表
    private ArrayList<DHJLInfo> data =new ArrayList<DHJLInfo>();
    private final int GETMYDHJL = 1;
    private final int GETALL = 2;
    private MyDHJLAdapter myDHJLAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        flag = intent.getIntExtra("flag",0);
        mDialog = DialogUtils.createLoadingDialog(RecordActivity.this, "加载中...");
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listrecord = (ListView)findViewById(R.id.listrecord);
        if(flag == 0 ) {
            myreadAll(username);//从服务端读取当前用户所有兑换记录
        }else {
            readAll();   //从服务端读取所有用户的兑换记录
        }
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if(flag == 0 ) {
                    myreadAll(username);//从服务端读取当前用户所有兑换记录
                }else {
                    readAll();   //从服务端读取所有用户的兑换记录
                }
                myDHJLAdapter.notifyDataSetChanged();
            }
        });

    }

    private void readAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 打开链接
                String url = SERVER_ADDRESS + "/searchDHJL.jsp";
                // 发送消息
                Message msg = new Message();
                msg.what = GETMYDHJL;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
        // 2）解析数据：xml-->ArrayList
    }

    private void myreadAll(String username) {
        final String u = username;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String u1 = URLEncoder.encode(u, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS + "/searchDHJLByName.jsp?username="+u1;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = GETMYDHJL;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                    // Handler
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        // 2）解析数据：xml-->ArrayList
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETMYDHJL:
                    String response = (String) msg.obj;
                    data.clear();
                    parserXml(response);
                    if(data.size() == 0) {
                        listrecord.setEmptyView(findViewById(R.id.myText));
                    }
                    myDHJLAdapter = new MyDHJLAdapter(RecordActivity.this, R.layout.record_item, data);
                    listrecord.setAdapter(myDHJLAdapter);
                    DialogUtils.closeDialog(mDialog);
                    refreshLayout.finishRefresh();//结束刷新
                    break;
                case GETALL:
                    String response1 = (String) msg.obj;
                    data.clear();
                    parserXml(response1);
                    if(data.size() == 0) {
                        listrecord.setEmptyView(findViewById(R.id.myText));
                    }
                    myDHJLAdapter = new MyDHJLAdapter(RecordActivity.this, R.layout.record_item, data);
                    listrecord.setAdapter(myDHJLAdapter);
                    DialogUtils.closeDialog(mDialog);
                    refreshLayout.finishRefresh();//结束刷新
                    break;
                default:
                    break;
            }
        }
    };

    private void parserXml(String xmlData) {
        String result = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String username = "";
            String prizename = "";
            String mobile = "";
            String name = "";
            String address = "";
            String dhtime = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                result += nodeName;
                result += ", ";
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取3个参数
                        if ("username".equals(nodeName)) {
                            String usernameStr = parse.nextText();
                            result += "用户为：" + usernameStr + ", ";
                            username = usernameStr;
                        } else if ("prizename".equals(nodeName)) {
                            String prizenameStr = parse.nextText();
                            result += "商品名称为：" + prizenameStr + ", ";
                            prizename = prizenameStr;
                        } else if ("dhtime".equals(nodeName)) {
                            String dhtimeStr = parse.nextText();
                            result += "兑换时间为：" + dhtimeStr + ", ";
                            dhtime = dhtimeStr.substring(0,dhtimeStr.length()-2);
                        } else if ("mobile".equals(nodeName)) {
                            String mobileStr = parse.nextText();
                            result += "电话号码为：" + mobileStr + ", ";
                            mobile = mobileStr;
                        } else if ("address".equals(nodeName)) {
                            String addressStr = parse.nextText();
                            result += "地址为：" + addressStr + ", ";
                            address = addressStr;
                        } else if ("name".equals(nodeName)) {
                            String nameStr = parse.nextText();
                            result += "姓名为：" + nameStr + ", ";
                            name = nameStr;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
                        // 添加数据
                        DHJLInfo info = new DHJLInfo(username,prizename,dhtime,mobile,
                                address,name);
                        data.add(info);
                        break;
                    default:
                        break;
                }
                eventType = parse.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d("resultStr", result);
    }
}
