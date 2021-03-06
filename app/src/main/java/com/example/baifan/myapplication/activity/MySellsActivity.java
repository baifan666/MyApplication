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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.SellOrderAdapter;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.model.OrderSpecificInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
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

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class MySellsActivity extends Activity {
    private final int MYREADALL = 1;
    private ImageView back;
    private String account, isfinish="";
    // 物品显示列表
    private ArrayList<OrderSpecificInfo> orderdata = new ArrayList<OrderSpecificInfo>();
    private ListView _listOrders;
    private SellOrderAdapter sellOrderAdapter;
    private NiceSpinner niceSpinner;
    private RefreshLayout refreshLayout;
    private Dialog mDialog;

    private int num = 0,num1 = 0;//用来记录comentsdata中的数据条数
    private int startrow = 0;  //起初页面为第一页
    private int scrollPos; //滑动以后的可见的第一条数据
    private int scrollTop;//滑动以后的第一条item的可见部分距离top的像素值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_sells);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        Intent intent = getIntent();
        account = intent.getStringExtra("username");
        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        mDialog = DialogUtils.createLoadingDialog(MySellsActivity.this, "加载中...");
        List<String> dataset = new LinkedList<>(Arrays.asList("全部", "进行中","已完成"));
        myAll(account,startrow);
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                startrow = 0;
                num1 = 0;
                switch (i) {
                    case 0:
                        isfinish = "";
                        myAll(account,startrow);
                        break;
                    case 1:
                        isfinish = "0";
                        myreadAll(account,isfinish,startrow);
                        break;
                    case 2:
                        isfinish = "1";
                        myreadAll(account,isfinish,startrow);
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
        _listOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderSpecificInfo orderSpecificInfo = orderdata.get(position);
                Intent intent = new Intent(MySellsActivity.this, MySellsSpecificActivity.class);
                intent.putExtra("orderSpecificInfo",orderSpecificInfo); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        //给ListView设置监听器
        _listOrders.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // scrollPos记录当前可见的List顶端的一行的位置
                    scrollPos = _listOrders.getFirstVisiblePosition();
                }
                if (_listOrders != null) {
                    View v=_listOrders .getChildAt(0);
                    scrollTop=(v==null)?0:v.getTop();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

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
                startrow = 0;
                num1 = 0;
                if("".equals(isfinish)) {
                    myAll(account,startrow);
                }else {
                    myreadAll(account,isfinish,startrow);
                }
                sellOrderAdapter.notifyDataSetChanged();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                num1 = orderdata.size();
                startrow = startrow+10;
                if("".equals(isfinish)) {
                    myAll(account,startrow);   //从服务端读取接下来的10个数据
                }else {
                    myreadAll(account,isfinish,startrow); //从服务端读取接下来的10个数据
                }
                sellOrderAdapter.notifyDataSetChanged();
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MYREADALL:
                    String response = (String) msg.obj;
                    if(startrow == 0) {
                        orderdata.clear();
                        refreshLayout.setNoMoreData(false);
                    }
                    parserXml1(response);
                    if (orderdata.size() == 0) {
                        _listOrders.setEmptyView(findViewById(R.id.myText));
                    }
                    num = orderdata.size();
                    if(num == num1) {
                        refreshLayout.setNoMoreData(true);//显示全部加载完成，并不再触发加载更多事件
                    }
                    sellOrderAdapter = new SellOrderAdapter(MySellsActivity.this, R.layout.sell_item, orderdata);
                    _listOrders.setAdapter(sellOrderAdapter);
                    DialogUtils.closeDialog(mDialog);
                    refreshLayout.finishRefresh();
                    _listOrders .setSelectionFromTop(scrollPos, scrollTop);
                    refreshLayout.finishLoadMore();//结束加载
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
            String buyermobile = "";
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
                            ordertime = ordertimeStr.substring(0,ordertimeStr.length()-2);
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
                            finishtime = finishtimeStr.substring(0,finishtimeStr.length()-2);
                        }else if ("buyermobile".equals(nodeName)) {
                            String buyermobileStr = parse.nextText();
                            result +="买家联系方式" + buyermobileStr +", ";
                            buyermobile = buyermobileStr;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
                        // 添加数据
                        OrderSpecificInfo info = new OrderSpecificInfo(id, username, title, publish_time, content,
                                price, mobile, location, path1, path2, orderid,
                                ordertime, buyerid, isfinish, finishtime, buyermobile);
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

    private void myreadAll(String username1,String isfinish1,int startrow1) {
        final String acc = username1;
        final String is = isfinish1;
        final int startrow2 = startrow1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String acc1 = URLEncoder.encode(acc, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS+"/sellerOrders.jsp?account="+acc1+"&isfinish="+is+"&startrow="+startrow2;
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

    private void myAll(String username1,int startrow1) {
        final String acc = username1;
        final int startrow2 = startrow1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String acc1 = URLEncoder.encode(acc, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS+"/sellerOrders.jsp?account="+acc1+"&startrow="+startrow2;
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

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
