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
import com.example.baifan.myapplication.adapter.CommentAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.CommentInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class CommentActivity extends Activity {
    private final int READALL = 1;
    private final int DELETE_COMMENT = 2;
    private Dialog mDialog;
    private ImageView back;
    private String username,goodsid;

    // 物品显示列表
    private ArrayList<CommentInfo> comentsdata =new ArrayList<CommentInfo>();
    private ListView _listcomments;
    private RefreshLayout refreshLayout;
    private CommentAdapter commentAdapter;
    private int num = 0,num1 = 0;//用来记录comentsdata中的数据条数
    private int startrow = 0;  //起初页面为第一页
    private int scrollPos; //滑动以后的可见的第一条数据
    private int scrollTop;//滑动以后的第一条item的可见部分距离top的像素值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("account");
        goodsid = intent.getStringExtra("goodsid");

        mDialog = DialogUtils.createLoadingDialog(CommentActivity.this, "加载中...");
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comentsdata.clear();
                finish();
            }
        });

        _listcomments = (ListView)findViewById(R.id.listcomments);
        readAll(goodsid,startrow);//从服务端读取当前物品所有评论
        _listcomments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //给ListView设置监听器
        _listcomments.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // scrollPos记录当前可见的List顶端的一行的位置
                    scrollPos = _listcomments.getFirstVisiblePosition();
                }
                if (_listcomments != null) {
                    View v=_listcomments .getChildAt(0);
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
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                readAll(goodsid,startrow); //从服务端读取所有物品
                commentAdapter.notifyDataSetChanged();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                num1 = comentsdata.size();
                startrow = startrow+10;
                readAll(goodsid,startrow); //从服务端读取接下来的10个数据
            }
        });
    }
    private void readAll(String goodsid1,int startrow1) {
        final String goodsid2 = goodsid1;
        final int startrow2 = startrow1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String goodsid3 = URLEncoder.encode(goodsid2, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS+"/getComment.jsp?goodsid="+goodsid3+"&startrow="+startrow2;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = READALL;
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

    private void deleteComment(String commentid) {
        final String commentid1 = commentid;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String commentid2 = URLEncoder.encode(commentid1, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS+"/deleteComment.jsp?goodsid="+commentid2;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = DELETE_COMMENT;
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

            String commentid = "";
            String username = "";
            String goodsid = "";
            String headurl = "";
            String local = "";
            String content = "";
            String commenttime = "";
            String replyed = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                result += nodeName;
                result += ", ";
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取3个参数
                        if ("commentid".equals(nodeName)) {
                            commentid = parse.nextText();
                        } else if ("username".equals(nodeName)) {
                            username = parse.nextText();
                        } else if ("goodsid".equals(nodeName)) {
                            goodsid = parse.nextText();
                        } else if ("headurl".equals(nodeName)) {
                            headurl = parse.nextText();
                        }else if ("local".equals(nodeName)) {
                            local = parse.nextText();
                        }else if ("content".equals(nodeName)) {
                            content = parse.nextText();
                        }else if ("commenttime".equals(nodeName)) {
                            String commenttimeStr = parse.nextText();
                            commenttime = commenttimeStr.substring(0,commenttimeStr.length()-2);
                        }else if ("replyed".equals(nodeName)) {
                            replyed = parse.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
                        // 添加数据
                        CommentInfo info = new CommentInfo(commentid,username,goodsid,headurl,local,content,commenttime,replyed);
                        comentsdata.add(info);
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
                    String response = (String) msg.obj;
                    if(startrow == 0) {
                        comentsdata.clear();
                        refreshLayout.setNoMoreData(false);
                    }
                    parserXml1(response);
                    num = comentsdata.size();
                    if(num == num1) {
                        refreshLayout.setNoMoreData(true);//显示全部加载完成，并不再触发加载更事件
                    }
                    commentAdapter = new CommentAdapter(CommentActivity.this, R.layout.comment_item, comentsdata,username);
                    _listcomments.setAdapter(commentAdapter);
                    refreshLayout.finishRefresh();//结束刷新
                    _listcomments .setSelectionFromTop(scrollPos, scrollTop);
                    refreshLayout.finishLoadMore();//结束加载
                    DialogUtils.closeDialog(mDialog);
                    //ListView item 中的删除按钮的点击事件
                    commentAdapter.setOnItemDeleteClickListener(new CommentAdapter.onItemDeleteListener() {
                        @Override
                        public void onDeleteClick(String i) {
                            deleteComment(i);
                        }
                    });
                    break;
                case DELETE_COMMENT:

                    break;
                default:
                    break;
            }
        }
    };
}
