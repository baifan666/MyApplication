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
import com.example.baifan.myapplication.adapter.GoodsAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class SearchResultActivity extends Activity {
    private final int SEARCH = 1;

    private ImageView back;
    private String guanjianzi;

    // 物品显示列表
    private ArrayList<GoodsInfo> goodsdata =new ArrayList<GoodsInfo>();
    private ListView _listGoods;

    private GoodsAdapter goodsadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_result);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        guanjianzi = intent.getStringExtra("guanjianzi");
        _listGoods = (ListView)findViewById(R.id.listgoods);
        _listGoods.setEmptyView(findViewById(R.id.myText));
        search(guanjianzi);//查询
        _listGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = goodsdata.get(position);
                Intent intent = new Intent(SearchResultActivity.this, SpecificActivity.class);
                intent.putExtra("goodsInfo",goodsInfo); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                search(guanjianzi);//查询
                //   goodsadapter.refresh(goodsdata);
                goodsadapter.notifyDataSetChanged();
                refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
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
        String result = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String str = String.format(" type = %d, str = %s\n", eventType, parse.getName());
            Log.d("xmlStr", str);

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
                result += nodeName;
                result += ", ";
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取3个参数
                        if ("username".equals(nodeName)) {
                            String usernameStr = parse.nextText();
                            result += "发布用户为：" + usernameStr + ", ";
                            username = usernameStr;
                        } else if ("title".equals(nodeName)) {
                            String titleStr = parse.nextText();
                            result += "标题为" + titleStr + ", ";
                            title = titleStr;
                        } else if ("publish_time".equals(nodeName)) {
                            String publish_timeStr = parse.nextText();
                            result += "发布时间为" + publish_timeStr + ", ";
                            publish_time = publish_timeStr.substring(0,publish_timeStr.length()-2);
                        } else if ("id".equals(nodeName)) {
                            String idStr = parse.nextText();
                            result += "发布id为" + idStr + ", ";
                            id = idStr;
                        }else if ("content".equals(nodeName)) {
                            String contentStr = parse.nextText();
                            result += "内容为" + contentStr + ", ";
                            content = contentStr;
                        }else if ("price".equals(nodeName)) {
                            String priceStr = parse.nextText();
                            result += "价格为" + priceStr + ", ";
                            price = Double.parseDouble(priceStr);
                        }else if ("mobile".equals(nodeName)) {
                            String mobileStr = parse.nextText();
                            result += "联系方式为" + mobileStr + ", ";
                            mobile = mobileStr;
                        }else if ("location".equals(nodeName)) {
                            String locationStr = parse.nextText();
                            result += "地点为" + locationStr + ", ";
                            location = locationStr;
                        }else if ("path1".equals(nodeName)) {
                            String path1Str = parse.nextText();
                            result += "地址1" + path1Str + ", ";
                            path1 = path1Str;
                        }else if ("path2".equals(nodeName)) {
                            String path2Str = parse.nextText();
                            result += "地址2" + path2Str + ", ";
                            path2 = path2Str;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
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
        Log.d("resultStr", result);
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
                    goodsadapter = new GoodsAdapter(SearchResultActivity.this, R.layout.goods_item, goodsdata);
                    _listGoods.setAdapter(goodsadapter);
                    break;
            }
        }
    };
}
