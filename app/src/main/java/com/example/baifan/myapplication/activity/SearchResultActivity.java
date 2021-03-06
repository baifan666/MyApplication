package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.GoodsAdapter;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.model.GoodsInfo;
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

public class SearchResultActivity extends Activity {
    private final int SEARCH = 1;

    private ImageView back;
    private String guanjianzi,account,headurl;
    private RefreshLayout refreshLayout;
    // 物品显示列表
    private ArrayList<GoodsInfo> goodsdata =new ArrayList<GoodsInfo>();
    private ListView _listGoods;

    private GoodsAdapter goodsadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_result);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        guanjianzi = intent.getStringExtra("guanjianzi");
        account = intent.getStringExtra("account");
        headurl = intent.getStringExtra("headurl");

        _listGoods = (ListView)findViewById(R.id.listgoods);
        search(guanjianzi);//查询
        _listGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = goodsdata.get(position);
                Intent intent = new Intent(SearchResultActivity.this, SpecificActivity.class);
                intent.putExtra("goodsInfo",goodsInfo); // 向下一个界面传递信息
                intent.putExtra("account",account);
                intent.putExtra("headurl", headurl);
                startActivity(intent);
            }
        });

        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setDisableContentWhenRefresh(true);//是否在刷新的时候禁止列表的操作
        refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
        refreshLayout.setFooterHeight(80);//Footer标准高度（显示上拉高度>=标准高度 触发加载）
        refreshLayout.setEnableAutoLoadMore(false);//是否启用列表惯性滑动到底部时自动加载更多
        refreshLayout.setEnableScrollContentWhenLoaded(false);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);//是否在列表不满一页时候开启上拉加载功能
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                search(guanjianzi);//查询
                goodsadapter.notifyDataSetChanged();
            }
        });

    }

    private void search(String sousuostr) {
        final String str = sousuostr;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str1 = URLEncoder.encode(str, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS+"/searchGoods.jsp?str="+str1;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = SEARCH;
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

    private void parserXml1(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();

            String id = "";
            String username = "";
            String publish_time = "";
            String title = "";
            String content = "";
            double price = 0.0;
            String mobile = "";
            String location = "";
            String path1 = "";
            String path2 = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取3个参数
                        if ("username".equals(nodeName)) {
                            username = parse.nextText();
                        } else if ("title".equals(nodeName)) {
                            title = parse.nextText();
                        } else if ("publish_time".equals(nodeName)) {
                            String publish_timeStr = parse.nextText();
                            publish_time = publish_timeStr.substring(0,publish_timeStr.length()-2);
                        } else if ("id".equals(nodeName)) {
                            id = parse.nextText();
                        }else if ("content".equals(nodeName)) {
                            content = parse.nextText();
                        }else if ("price".equals(nodeName)) {
                            String priceStr = parse.nextText();
                            price = Double.parseDouble(priceStr);
                        }else if ("mobile".equals(nodeName)) {
                            mobile = parse.nextText();
                        }else if ("location".equals(nodeName)) {
                            location = parse.nextText();
                        }else if ("path1".equals(nodeName)) {
                            path1 = parse.nextText();
                        }else if ("path2".equals(nodeName)) {
                            path2 = parse.nextText();
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        // 添加数据
                        GoodsInfo info = new GoodsInfo(id, username, title, publish_time, content, price, mobile, location, path1, path2);
                        goodsdata.add(info);
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCH:
                    String response1 = (String) msg.obj;
                    goodsdata.clear();
                    parserXml1(response1);
                    if(goodsdata.size() == 0){
                        _listGoods.setEmptyView(findViewById(R.id.myText));
                    }
                    goodsadapter = new GoodsAdapter(SearchResultActivity.this, R.layout.goods_item, goodsdata);
                    refreshLayout.finishRefresh();
                    _listGoods.setAdapter(goodsadapter);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
