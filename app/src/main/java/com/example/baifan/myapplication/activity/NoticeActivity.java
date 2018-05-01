package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.NoticeAdapter;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.model.NoticeInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static com.example.baifan.myapplication.common.ServerAddress.ZAFU_ADDRESS;

public class NoticeActivity extends Activity {
    private Dialog mDialog;
    private ImageView back;
    private NoticeAdapter noticeAdapter;
    // 通知显示列表
    private ArrayList<NoticeInfo> noticeData =new ArrayList<NoticeInfo>();
    private ListView listNotice;
    private RefreshLayout refreshLayout;
    private final int READALL = 1;
    private final int GETNEXT = 2;
    private String firsturl = "/tzgg.htm";
    private int scrollPos; //滑动以后的可见的第一条数据
    private int scrollTop;//滑动以后的第一条item的可见部分距离top的像素值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listNotice = (ListView)findViewById(R.id.listNotice);
        mDialog = DialogUtils.createLoadingDialog(NoticeActivity.this, "加载中...");
        getNotices();//从网页抓取信息
        listNotice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeInfo noticeInfo = noticeData.get(position);
                Intent intent = new Intent(NoticeActivity.this, NoticeSpecificActivity.class);
                String NoticeUrl = ZAFU_ADDRESS+"/"+noticeInfo.getNoticeUrl();
                intent.putExtra("NoticeUrl",NoticeUrl); // 向下一个界面传递信息
                intent.putExtra("title",noticeInfo.getNoticeTitle()); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        //给ListView设置监听器
        listNotice.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // scrollPos记录当前可见的List顶端的一行的位置
                    scrollPos = listNotice.getFirstVisiblePosition();
                }
                if (listNotice != null) {
                    View v=listNotice .getChildAt(0);
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
                firsturl = "/tzgg.htm";
                noticeData.clear();
                getNotices();//从网页抓取信息
                noticeAdapter.notifyDataSetChanged();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
//                page = goodsdata.size();
//                startrow = startrow+10;
//                readAll(startrow); //从服务端读取接下来的10个数据
                //refreshlayout.finishLoadmore(2000);
                getNext();
            }
        });

    }

    private void getNotices(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect(ZAFU_ADDRESS+firsturl).get();
                    //获得li的元素集合
                    Elements elements = doc.select("body > div.content > div.ny_news > div.ny_news_f > div.ny_news_lb > ul > li");
                    for (Element element : elements) {
                        String noticeTitle = element.select("a").first().text();
                        String noticeUrl = element.select("a").first().attr("href");
                        String noticeTime = element.select("span").first().text();
                        NoticeInfo news = new NoticeInfo(noticeTitle, noticeTime, noticeUrl);
                        noticeData.add(news);
                    }
                    Message msg = new Message();
                    msg.what = READALL;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getNext(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect(ZAFU_ADDRESS+firsturl).get();
                    //获得li的元素集合
                    Element element = doc.select("body > div.content > div.ny_news > div.ny_news_f > div.ny_news_lb > div > table > tbody > tr > td > table > tbody > tr > td:nth-child(2) > div > a:nth-child(3)").first();
                    String u = element.select("a").first().attr("href");
                    if(TextUtils.isEmpty(u)) {
                        firsturl = "0";
                    }else {
                        firsturl = "/tzgg/"+u.substring(u.lastIndexOf("/")+1);
                    }
                    Message msg = new Message();
                    msg.what = GETNEXT;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

        Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READALL:
                    if(noticeData.size() == 0){
                        listNotice.setEmptyView(findViewById(R.id.myText));
                    }
                    noticeAdapter = new NoticeAdapter(NoticeActivity.this, R.layout.notice_item, noticeData);
                    listNotice.setAdapter(noticeAdapter);
                    refreshLayout.finishRefresh();//结束刷新
                    listNotice.setSelectionFromTop(scrollPos, scrollTop);
                    refreshLayout.finishLoadMore();//结束加载
                    DialogUtils.closeDialog(mDialog);
                    break;
                case GETNEXT:
                    if("0".equals(firsturl)) {
                        refreshLayout.setNoMoreData(true);//显示全部加载完成，并不再触发加载更多事件
                    }else {
                        getNotices();
                    }
                    break;
                default:
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
