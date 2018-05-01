package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.RankinglistAdapter;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.model.UserSignInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class RankinglistActivity extends Activity {
    private ImageView back;
    private List<UserSignInfo> data = new ArrayList<UserSignInfo>();
    private RankinglistAdapter rankinglistAdapter;
    private ListView rankinglist;
    private Dialog mDialog;
    private final int GETSIGNLIST = 1;
    private RefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rankinglist);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        back=(ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rankinglist = (ListView)findViewById(R.id.rankinglist);
        getSignList();
        mDialog = DialogUtils.createLoadingDialog(RankinglistActivity.this, "加载中...");
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getSignList();
                rankinglistAdapter.notifyDataSetChanged();

            }
        });
    }


    private void getSignList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 打开链接
                String url = SERVER_ADDRESS+"/getSignList.jsp";
                // 发送消息
                Message msg = new Message();
                msg.what = GETSIGNLIST;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
        // 2）解析数据：xml-->ArrayList
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETSIGNLIST:
                    String response = (String) msg.obj;
                    data.clear();
                    parserXml(response);
                    rankinglistAdapter = new RankinglistAdapter(RankinglistActivity.this, R.layout.rankinglist_item, data);
                    rankinglist.setAdapter(rankinglistAdapter);
                    DialogUtils.closeDialog(mDialog);
                    refreshLayout.finishRefresh();//结束刷新
                    break;
            }
        }
    };

    private void parserXml(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String username = "";
            int signcount = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("username".equals(nodeName)) {
                            username = parse.nextText();

                        } else if ("signcount".equals(nodeName)) {
                            String signcountStr = parse.nextText();
                            signcount = Integer.parseInt(signcountStr);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        // 添加数据
                        UserSignInfo info = new UserSignInfo(username,signcount);
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
    }

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
