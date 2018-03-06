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
import com.example.baifan.myapplication.adapter.MyOrderAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.OrderSpecificInfo;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.angmarch.views.NiceSpinner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MyOrdersActivity extends Activity {
    private final int MYREADALL = 1;
    private ImageView back;
    private String account, isfinish="";
    // 物品显示列表
    private ArrayList<OrderSpecificInfo> orderdata = new ArrayList<OrderSpecificInfo>();
    private ListView _listOrders;
    private MyOrderAdapter myorderadapter;
    private NiceSpinner niceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_orders);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        account = intent.getStringExtra("username");
        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("全部", "进行中","已完成"));
        myAll(account);
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        isfinish = "";
                        myAll(account);
                        break;
                    case 1:
                        isfinish = "0";
                        myreadAll(account,isfinish);
                        break;
                    case 2:
                        isfinish = "1";
                        myreadAll(account,isfinish);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        back = (ImageView) findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderdata.clear();
                finish();
            }
        });
        _listOrders = (ListView) findViewById(R.id.listorders);
        _listOrders.setEmptyView(findViewById(R.id.myText));
        _listOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderSpecificInfo orderSpecificInfo = orderdata.get(position);
                Intent intent = new Intent(MyOrdersActivity.this, MyOrdersSpecificActivity.class);
                intent.putExtra("orderSpecificInfo",orderSpecificInfo); // 向下一个界面传递信息
                startActivity(intent);
            }
        });

        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if("".equals(isfinish)) {
                    myAll(account);
                }else {
                    myreadAll(account,isfinish);
                }
                myorderadapter.notifyDataSetChanged();
                refreshlayout.finishRefresh(2000);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MYREADALL:
                    String response2 = (String) msg.obj;
                    orderdata.clear();
                    parserXml1(response2);
                    myorderadapter = new MyOrderAdapter(MyOrdersActivity.this, R.layout.orders_item, orderdata);
                    _listOrders.setAdapter(myorderadapter);
                    break;
            }
        }
    };

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
            String orderid = "";
            String ordertime = "";
            String buyerid = "";
            String finishtime = "";
            String isfinish = "";
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
                        }else if ("orderid".equals(nodeName)) {
                            String orderidStr = parse.nextText();
                            result +="订单号" + orderidStr +", ";
                            orderid = orderidStr;
                        }else if ("ordertime".equals(nodeName)) {
                            String ordertimeStr = parse.nextText();
                            result +="订单时间" + ordertimeStr +", ";
                            ordertime = ordertimeStr;
                        }else if ("buyerid".equals(nodeName)) {
                            String buyeridStr = parse.nextText();
                            result +="买家" + buyeridStr +", ";
                            buyerid = buyeridStr;
                        }else if ("isfinish".equals(nodeName)) {
                            String isfinishStr = parse.nextText();
                            result +="是否完成" + isfinishStr +", ";
                            isfinish = isfinishStr;
                        }else if ("finishtime".equals(nodeName)) {
                            String finishtimeStr = parse.nextText();
                            result +="完成时间" + finishtimeStr +", ";
                            finishtime = finishtimeStr;
                        }


                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
                        // 添加数据
                        OrderSpecificInfo info = new OrderSpecificInfo(id,username,title,publish_time,content,
                        price, mobile, location,path1,path2,orderid, ordertime, buyerid,isfinish, finishtime);
                        orderdata.add(info);
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

    private void myreadAll(String username,String isfinish) {
        final String acc = username;
        final String is = isfinish;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String acc1 = URLEncoder.encode(acc, "UTF-8");
                    // 打开链接
                    String url = "http://111.231.101.251:8080/fuwuduan/myOrders.jsp?account="+acc1+"&isfinish="+is;
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

    private void myAll(String username) {
        final String acc = username;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String acc1 = URLEncoder.encode(acc, "UTF-8");
                    // 打开链接
                    String url = "http://111.231.101.251:8080/fuwuduan/myOrders.jsp?account="+acc1;
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
}

