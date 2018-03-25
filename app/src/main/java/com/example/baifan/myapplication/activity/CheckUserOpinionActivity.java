package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.UserOpinionAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.UserOpinionInfo;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class CheckUserOpinionActivity extends Activity {
    private ImageView back;
    private final int READALL = 1;
    // 物品显示列表
    private ArrayList<UserOpinionInfo> userOpinionData =new ArrayList<UserOpinionInfo>();
    private ListView _listOpinion;

    private UserOpinionAdapter userOpinionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_check_user_opinion);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        back = (ImageView) findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        _listOpinion = (ListView)findViewById(R.id.list);
        _listOpinion.setEmptyView(findViewById(R.id.myText));
        readAll();//从服务端读取所有用户反馈
        _listOpinion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserOpinionInfo userOpinionInfo = userOpinionData.get(position);
                Intent intent = new Intent(CheckUserOpinionActivity.this, UserOpinionSpecificActivity.class);
                intent.putExtra("UserOpinionInfo",userOpinionInfo); // 向下一个界面传递信息
                startActivity(intent);
            }
        });

        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                readAll();//从服务端读取所有用户反馈
                userOpinionAdapter.notifyDataSetChanged();
                refreshlayout.finishRefresh(1000);
            }
        });
    }


    private void readAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                    // 打开链接
                    String url = SERVER_ADDRESS+"/searchFeedback.jsp?";
                    // 发送消息
                    Message msg = new Message();
                    msg.what = READALL;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                    // Handler
            }
        }).start();
        // 2）解析数据：xml-->ArrayList
    }

    private void parserXml(String xmlData) {
        String result = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String str = String.format(" type = %d, str = %s\n", eventType, parse.getName());
            Log.d("xmlStr", str);

            String username = "";
            String content = "";
            String feedbacktime = "";
            String contacts = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                result += nodeName;
                result += ", ";
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取3个参数
                        if ("username".equals(nodeName)) {
                            String usernameStr = parse.nextText();
                            result += "反馈用户为：" + usernameStr + ", ";
                            username = usernameStr;
                        } else if ("content".equals(nodeName)) {
                            String contentStr = parse.nextText();
                            result += "反馈内容为" + contentStr + ", ";
                            content = contentStr;
                        } else if ("feedbacktime".equals(nodeName)) {
                            String feedbacktimeStr = parse.nextText();
                            result += "反馈时间为" + feedbacktimeStr + ", ";
                            feedbacktime = feedbacktimeStr.substring(0,feedbacktimeStr.length()-2);
                        } else if ("contacts".equals(nodeName)) {
                            String contactsStr = parse.nextText();
                            result += "联系方式为" + contactsStr + ", ";
                            contacts = contactsStr;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
                        // 添加数据
                        UserOpinionInfo info = new UserOpinionInfo(username,content,feedbacktime,contacts);
                        userOpinionData.add(info);
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READALL:
                    String response2 = (String) msg.obj;
                    userOpinionData.clear();
                    parserXml(response2);
                    userOpinionAdapter = new UserOpinionAdapter(CheckUserOpinionActivity.this, R.layout.fankui_item, userOpinionData);
                    _listOpinion.setAdapter(userOpinionAdapter);
                    break;
            }
        }
    };
}
