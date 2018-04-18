package com.example.baifan.myapplication.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.CommentAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.CommentInfo;
import com.example.baifan.myapplication.model.GoodsInfo;
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
    private final int ADD_COMMENT = 3;
    private Dialog mDialog;
    private ImageView back;
    private String username,goodsid,headurl,local,replyed = "";

    // 物品显示列表
    private ArrayList<CommentInfo> comentsdata =new ArrayList<CommentInfo>();
    private ListView _listcomments;
    private RefreshLayout refreshLayout;
    private CommentAdapter commentAdapter;
    private int num = 0,num1 = 0;//用来记录comentsdata中的数据条数
    private int startrow = 0;  //起初页面为第一页
    private int scrollPos; //滑动以后的可见的第一条数据
    private int scrollTop;//滑动以后的第一条item的可见部分距离top的像素值
    private EditText content;
    private Button addcomment;
    public LocationClient mLocationClient;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        username = intent.getStringExtra("account");
        goodsid = intent.getStringExtra("goodsid");
        headurl = intent.getStringExtra("headurl");

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
                CommentInfo commentInfo = comentsdata.get(position);
                if(username.equals(commentInfo.getUsername().toString())) {
                }else {
                    content.setHint("回复: "+commentInfo.getUsername().toString());
                    replyed = commentInfo.getUsername().toString();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
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

        content = (EditText) findViewById(R.id.content);
        addcomment = (Button)findViewById(R.id.add);
        addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(content.getHint())) {
                    replyed = "";
                }
                mDialog = DialogUtils.createLoadingDialog(CommentActivity.this, "评论中...");
                if(TextUtils.isEmpty(content.getText().toString())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("评论不能为空！");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                    DialogUtils.closeDialog(mDialog);
                }else {
                    addComment(username, goodsid, headurl, local, content.getText().toString(), replyed);
                }
            }
        });

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        /**
         * 设置定位模式 Battery_Saving 低功耗模式 Device_Sensors 仅设备(Gps)模式 Hight_Accuracy
         * 高精度模式
         /   */
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);

    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                // GPS定位结果
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                // 网络定位结果
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                // 离线定位结果
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(CommentActivity.this, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(CommentActivity.this, "网络错误，请检查", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(CommentActivity.this, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
            }
            local = location.getAddrStr();    //获取详细地址信息
        }
    }


    private void addComment(String username1, String goodsid1,
                            String headurl1,String local1,String content1, String replyed1) {
        final String username2 = username1;
        final String goodsid2 = goodsid1;
        final String headurl2 = headurl1;
        final String local2 = local1;
        final String content2 = content1;
        final String replyed2 = replyed1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String username3 = URLEncoder.encode(username2, "UTF-8");
                    String goodsid3 = URLEncoder.encode(goodsid2, "UTF-8");
                    String headurl3 = URLEncoder.encode(headurl2, "UTF-8");
                    String local3 = URLEncoder.encode(local2, "UTF-8");
                    String content3 = URLEncoder.encode(content2, "UTF-8");
                    String replyed3 = URLEncoder.encode(replyed2, "UTF-8");
                    // 打开链接
                    String url = SERVER_ADDRESS+"/addComment.jsp?username="+username3+"&goodsid="+goodsid3
                            +"&headurl="+headurl3+"&local="+local3+"&content="+content3+"&replyed="+replyed3;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = ADD_COMMENT;
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
                    String url = SERVER_ADDRESS+"/deleteComment.jsp?commentid="+commentid2;
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
    private boolean parserXml(String xmlData) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String result = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                        }
                        // 简单的判断物品是否修改成功
                        if (result.equals("succeessful"))
                            return true;
                        else if (result.equals("failed"))
                            return false;
                        break;
                }
                eventType = parse.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
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
                    String response1 = (String) msg.obj;
                    if (parserXml(response1)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                        dialog.setTitle("success");
                        dialog.setMessage("删除成功!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                // 物品新增成功后 跳转至界面
//                                Intent intent = new Intent(AlterGoodsActivity.this, SearchActivity.class);
//                                intent.putExtra("account", account);
//                                startActivity(intent);
                                finish();
                            }
                        });
                        DialogUtils.closeDialog(mDialog);
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，系统出错了！");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                        DialogUtils.closeDialog(mDialog);
                    }
                    break;
                case ADD_COMMENT:
                    String response2 = (String) msg.obj;
                    if (parserXml(response2)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                        dialog.setTitle("success");
                        dialog.setMessage("新增成功!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                // 物品新增成功后 跳转至界面
//                                Intent intent = new Intent(AlterGoodsActivity.this, SearchActivity.class);
//                                intent.putExtra("account", account);
//                                startActivity(intent);
                                finish();
                            }
                        });
                        DialogUtils.closeDialog(mDialog);
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CommentActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，系统出错了！");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                        DialogUtils.closeDialog(mDialog);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
