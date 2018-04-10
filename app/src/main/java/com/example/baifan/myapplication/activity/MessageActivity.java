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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.MessageAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.MessageInfo;
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

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class MessageActivity extends Activity {
    private Dialog mDialog;
    private ImageView back;
    private String username;
    // 系统消息显示列表
    private ArrayList<MessageInfo> messageData =new ArrayList<MessageInfo>();
    private ListView listMessage;
    private RefreshLayout refreshLayout;
    private MessageAdapter messageAdapter;
    private final int MYREADALL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        mDialog = DialogUtils.createLoadingDialog(MessageActivity.this, "加载中...");
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        listMessage = (ListView)findViewById(R.id.listMessages);
        myreadAll(username);//从服务端读取所有系统消息
        listMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MessageInfo messageInfo = messageData.get(position);
//                Intent intent = new Intent(MessageActivity.this, MyOrdersSpecificActivity.class);
//                intent.putExtra("messageInfo",messageInfo); // 向下一个界面传递信息
//                startActivity(intent);
            }
        });
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                myreadAll(username); //从服务端读取所有物品
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void myreadAll(String username) {
        final String acc = username;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String acc1 = URLEncoder.encode(acc, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS+"/searchMessageByUsername.jsp?username="+acc1;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = MYREADALL;
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

    private void parserXml(String xmlData) {
        String result = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();

            String messageid = "";
            String username = "";
            String messagetime = "";
            String content = "";
            int isdeleted = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取3个参数
                        if ("messageid".equals(nodeName)) {
                            messageid = parse.nextText();
                        } else if ("username".equals(nodeName)) {
                            username = parse.nextText();
                        } else if ("messagetime".equals(nodeName)) {
                            String messagetimeStr = parse.nextText();
                            messagetime = messagetimeStr.substring(0,messagetimeStr.length()-2);
                        } else if ("content".equals(nodeName)) {
                            content = parse.nextText();
                        }else if ("isdeleted".equals(nodeName)) {
                            String isdeletedStr = parse.nextText();
                            isdeleted = Integer.parseInt(isdeletedStr);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
                        // 添加数据
                        MessageInfo info = new MessageInfo(username,messagetime,content,isdeleted,messageid);
                        messageData.add(info);
                        Toast.makeText(MessageActivity.this, messagetime+content, Toast.LENGTH_SHORT).show();
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
                case MYREADALL:
                    String response = (String) msg.obj;
                    messageData.clear();
                    parserXml(response);
//                    if(messageData.size() == 0){
//                        listMessage.setEmptyView(findViewById(R.id.myText));
//                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, R.layout.message_item, messageData);
                    listMessage.setAdapter(messageAdapter);
                    refreshLayout.finishRefresh();//结束刷新
                    DialogUtils.closeDialog(mDialog);
                    break;
            }
        }
    };
}
