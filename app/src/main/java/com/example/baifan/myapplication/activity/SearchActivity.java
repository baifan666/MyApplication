package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.example.baifan.myapplication.utils.BitmapUtils;
import com.example.baifan.myapplication.utils.CacheUtil;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.DownLoadManager;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.UpdataInfo;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.example.baifan.myapplication.utils.UpdataInfoParser;
import com.example.baifan.myapplication.utils.UploadUtil;
import com.example.baifan.myapplication.adapter.Adapter;
import com.example.baifan.myapplication.adapter.GoodsAdapter;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import q.rorbin.badgeview.QBadgeView;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;


public class SearchActivity extends Activity implements
        android.view.View.OnClickListener{
    private int startrow = 0;
    private boolean isback;

    private ViewPager mViewPager;// 用来放置界面切换
    private PagerAdapter mPagerAdapter;// 初始化View适配器
    private List<View> mViews = new ArrayList<View>();// 用来存放Tab01-04
    // 四个Tab，每个Tab包含一个按钮
    private LinearLayout mTabWeiXin;
    private LinearLayout mTabAddress;
    private LinearLayout mTabFrd;
    private LinearLayout mTabSetting;
    // 四个按钮
    private ImageButton mWeiXinImg;
    private ImageButton mAddressImg;
    private ImageButton mFrdImg;
    private ImageButton mSettingImg;
    //四个view
    View tab01;
    View tab02;
    View tab03;
    View tab04;

    private final String TAG = this.getClass().getName();
    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    private final int ADD_SUCCEESS = 5;
    private final int DISMISS = 6;
    private final int READALL = 7;

    private UpdataInfo info;
    private String localVersion;

    private TextView cache,gengxin,deletecache,about,zhanghaoguanli,jinbishangcheng,fankui,myorders,wodefabu,shouchuwupin;
    private ImageView game1,game2,game3,search,huihua;
    private Button send_btn;

    private List<Bitmap> data = new ArrayList<Bitmap>();
    private GridView mGridView;
    private String photoPath;
    private Adapter adapter;
    private GoodsAdapter goodsadapter;

    private String result = null;
    private EditText title,content,price,location,mobile;
    private String account,path1,path2,tit,con,pri,loc,mob;
    private List <String> s = new ArrayList<String>();//创建了s来保存本地图片的地址
    private List <String> ss = new ArrayList<String>();//创建了ss来保存服务器图片的地址
    private static String requestURL = SERVER_ADDRESS+"/UploadShipServlet";

    private Dialog mDialog;

    // 物品显示列表
    private ArrayList<GoodsInfo> goodsdata =new ArrayList<GoodsInfo>();
    private ListView _listGoods;
    private RefreshLayout refreshLayout;
    private int num = 0,num1 = 0;//用来记录goodsdata中的数据条数
    private ImageButton map;

    private int scrollPos; //滑动以后的可见的第一条数据
    private int scrollTop;//滑动以后的第一条item的可见部分距离top的像素值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                "azrbHW8CGeAEMt4MyLSplNCAodv7xZwG");

        initUnreadCountListener();
        mDialog = DialogUtils.createLoadingDialog(SearchActivity.this, "加载中...");
        Intent intent = getIntent();
        account = intent.getStringExtra("account");


        initView();
        initViewPage();
        initEvent();

        refreshLayout = (RefreshLayout)tab01.findViewById(R.id.refreshLayout);
        refreshLayout.setDisableContentWhenRefresh(true);//是否在刷新的时候禁止列表的操作
        refreshLayout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
        refreshLayout.setFooterHeight(80);//Footer标准高度（显示上拉高度>=标准高度 触发加载）
        refreshLayout.setEnableAutoLoadMore(false);//是否启用列表惯性滑动到底部时自动加载更多
        refreshLayout.setEnableScrollContentWhenLoaded(false);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                startrow = 0;
                readAll(startrow); //从服务端读取所有物品
                goodsadapter.notifyDataSetChanged();
                //refreshlayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                num1 = goodsdata.size();
                startrow = startrow+10;
                readAll(startrow); //从服务端读取接下来的10个数据
                //refreshlayout.finishLoadmore(2000);
            }
        });

        _listGoods = (ListView)tab01.findViewById(R.id.listgoods);
        readAll(startrow); //从服务端读取所有物品
        _listGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsInfo goodsInfo = goodsdata.get(position);
                Intent intent = new Intent(SearchActivity.this, SpecificActivity.class);
                intent.putExtra("goodsInfo",goodsInfo); // 向下一个界面传递信息
                intent.putExtra("account",account);
                startActivity(intent);
            }
        });
        //给ListView设置监听器
        _listGoods.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // scrollPos记录当前可见的List顶端的一行的位置
                    scrollPos = _listGoods.getFirstVisiblePosition();
                }
                if (_listGoods != null) {
                    View v=_listGoods .getChildAt(0);
                    scrollTop=(v==null)?0:v.getTop();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        title = (EditText)tab02.findViewById(R.id.title);
        content = (EditText)tab02.findViewById(R.id.content);
        price = (EditText)tab02.findViewById(R.id.price);
        mobile = (EditText)tab02.findViewById(R.id.mobile);
        location = (EditText)tab02.findViewById(R.id.location);

        map = (ImageButton)tab02.findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SearchActivity.this,
                        MapActivity.class), 0x2);
            }
        });

        send_btn = (Button)tab02.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = DialogUtils.createLoadingDialog(SearchActivity.this, "发布中...");
                tit = title.getText().toString();
                con = content.getText().toString();
                pri = price.getText().toString();
                mob = mobile.getText().toString();
                loc = location.getText().toString();
                if("".equals(tit)||"".equals(con)||"".equals(pri)||"".equals(mob)||"".equals(loc))  {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("请确保每一个信息已输入！");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                    DialogUtils.closeDialog(mDialog);
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (String tmp : s) {
                                File file = new File(tmp);
                                if (file != null) {
                                    result = UploadUtil.uploadFile(file, requestURL);
                                    ss.add(result);
                                } else {
                                    result = "1111111";
                                }
                            }
                            HashSet h = new HashSet(ss);
                            ss.clear();
                            ss.addAll(h);
                            if (ss.size() == 1) {
                                path1 = ss.get(0);
                                path2 = "";
                            } else if (ss.size() == 2) {
                                path1 = ss.get(0);
                                path2 = ss.get(1);
                            } else {
                                path1 = "";
                                path2 = "";
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addGoods(account, tit, con, pri, mob, loc, path1, path2);
                                }
                            });
                        }
                    }).start();
                }
            }
        });


        search=(ImageView)findViewById(R.id.top_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(SearchActivity.this, Search2Activity.class);
                startActivity(i);
            }
        });

        huihua = (ImageView) findViewById(R.id.top_huihua);
        huihua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(SearchActivity.this, ConversationListActivity.class);
                startActivity(i);
            }
        });

        wodefabu = (TextView) tab04.findViewById(R.id.wodefabu);
        wodefabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(SearchActivity.this, MyGoodsActivity.class);
                i.putExtra("username",account); // 向下一个界面传递信息
                startActivity(i);
            }
        });

        // 设置默认图片为加号
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
        data.add(bp);
        // 找到控件ID
        mGridView = (GridView)tab02.findViewById(R.id.gridView1);
        // 绑定Adapter
        adapter = new Adapter(getApplicationContext(), data, mGridView);
        mGridView.setAdapter((ListAdapter) adapter);
        // 设置点击监听事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data.size() == 3) {
                    Toast.makeText(SearchActivity.this, "图片数2张已满", Toast.LENGTH_SHORT).show();
                } else {
                    if (position == data.size() - 1) {
                        //Toast.makeText(SearchActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                        // 选择图片
                       //Intent intent = new Intent(Intent.ACTION_PICK, null);
                       // intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        //startActivityForResult(intent, 0x1);

                        //使用startActivityForResult启动SelectPicPopupWindow当返回到此Activity的时候就会调用onActivityResult函数
                        startActivityForResult(new Intent(SearchActivity.this,
                                SelectPicPopupWindow.class), 0x1);

                    } else {
                        Toast.makeText(SearchActivity.this, "点击第" + (position + 1) + " 号图片", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // 设置长按事件
        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < 2 ) {
                    dialog(position);
                }
                return true;
            }
        });


        game1=(ImageView)tab03.findViewById(R.id.game1);
        game2=(ImageView)tab03.findViewById(R.id.game2);
        game3=(ImageView)tab03.findViewById(R.id.game3);
        game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, Game1Activity.class);
                startActivity(intent);
            }
        });
        game2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, Game2Activity.class);
                startActivity(intent);
            }
        });
        game3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, Game3Activity.class);
                startActivity(intent);
            }
        });

        cache = (TextView) tab04.findViewById(R.id.cache);
        try {
            cache.setText(CacheUtil.getTotalCacheSize(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        gengxin = (TextView)tab04.findViewById(R.id.gengxin);
        gengxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    localVersion = getVersionName();
                    CheckVersionTask cv = new CheckVersionTask();
                    new Thread(cv).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        deletecache=(TextView)tab04.findViewById(R.id.deletecache);
        deletecache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheUtil.clearAllCache(getApplicationContext());
                Toast.makeText(getApplicationContext(),"缓存已清理",Toast.LENGTH_SHORT).show();
                try {
                    cache.setText(CacheUtil.getTotalCacheSize(getApplicationContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        about = (TextView)tab04.findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
        zhanghaoguanli = (TextView)tab04.findViewById(R.id.zhanghaoguanli);
        zhanghaoguanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, AccountManagementActivity.class);
                intent.putExtra("username",account); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        jinbishangcheng = (TextView)tab04.findViewById(R.id.jinbishangcheng);
        jinbishangcheng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, CoinMallActivity.class);
                intent.putExtra("username",account); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        fankui = (TextView)tab04.findViewById(R.id.fankui);
        fankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, OpinionActivity.class);
                intent.putExtra("username",account); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        myorders = (TextView)tab04.findViewById(R.id.myorders);
        myorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, MyOrdersActivity.class);
                intent.putExtra("username",account); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        shouchuwupin = (TextView)tab04.findViewById(R.id.shouchuwupin);
        shouchuwupin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(SearchActivity.this, MySellsActivity.class);
                intent.putExtra("username",account); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
    }


    /*
     * 获取当前程序的版本号
     */
    private String getVersionName() throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionName;
    }
    /*
     * 从服务器获取xml解析并进行比对版本号
     */
    public class CheckVersionTask implements Runnable {
        InputStream is;
        public void run() {
            try {
                String path = getResources().getString(R.string.url_server);
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // 从服务器获得一个输入流
                    is = conn.getInputStream();
                }
                info = UpdataInfoParser.getUpdataInfo(is);
                if (info.getVersion().equals(localVersion)) {
                    Log.i(TAG, "版本号相同");
                    Message msg = new Message();
                    msg.what = UPDATA_NONEED;
                    handler.sendMessage(msg);
                    // LoginMain();
                } else {
                    Log.i(TAG, "版本号不相同 ");
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_NONEED:
                    Toast.makeText(getApplicationContext(), "已是最新版本，不需要更新",Toast.LENGTH_SHORT).show();
                    break;
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    break;
                case ADD_SUCCEESS:
                    String response = (String) msg.obj;
                    if (parserXml(response)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
                        dialog.setTitle("success");
                        dialog.setMessage("物品发布成功!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 物品新增成功后 跳转至界面
                                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                                intent.putExtra("account", account);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，系统出错了！");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    }
                    break;
                case DISMISS:
                    DialogUtils.closeDialog(mDialog);
                    break;
                case READALL:
                    String response2 = (String) msg.obj;
                    if(startrow == 0) {
                        goodsdata.clear();
                        refreshLayout.setNoMoreData(false);
                    }
                    parserXml2(response2);
                    num = goodsdata.size();
                    if(num == num1) {
                        refreshLayout.setNoMoreData(true);
                       // refreshLayout.finishLoadMoreWithNoMoreData();//显示全部加载完成，并不再触发加载更事件
                    }
                    goodsadapter = new GoodsAdapter(SearchActivity.this, R.layout.goods_item, goodsdata);
                    _listGoods.setAdapter(goodsadapter);
                    DialogUtils.closeDialog(mDialog);
                    refreshLayout.finishRefresh();//结束刷新
                    _listGoods .setSelectionFromTop(scrollPos, scrollTop);
                    refreshLayout.finishLoadMore();//结束加载
                    break;
            }
        }
    };
    /*
     *
     * 弹出对话框通知用户更新程序
     *
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdataDialog() {
        AlertDialog.Builder builer = new Builder(this) ;
        builer.setTitle("版本升级");
        builer.setMessage(info.getDescription());
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"下载apk,更新");
                downLoadApk();
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = DownLoadManager.getFileFromServer(info.getUrl(), pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = DOWN_ERROR;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }}.start();
    }

    //安装apk
    protected void installApk(File file) {
/*        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://"+file),"application/vnd.android.package-archive");
        startActivity(intent);*/

        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installApkIntent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), "com.example.baifan.myapplication.fileprovider", file), "application/vnd.android.package-archive");
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }

        if (getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
            startActivity(installApkIntent);
        }

    }





    private void initEvent() {
        mTabWeiXin.setOnClickListener(this);
        mTabAddress.setOnClickListener(this);
        mTabFrd.setOnClickListener(this);
        mTabSetting.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             *ViewPage左右滑动时
             */
            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        resetImg();
                        mWeiXinImg.setImageResource(R.drawable.zhuye_pressed);
                        break;
                    case 1:
                        resetImg();
                        mAddressImg.setImageResource(R.drawable.fabu_pressed);
                        break;
                    case 2:
                        resetImg();
                        mFrdImg.setImageResource(R.drawable.faxian_pressed);
                        break;
                    case 3:
                        resetImg();
                        mSettingImg.setImageResource(R.drawable.wode_pressed);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    /**
     * 初始化设置
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpage);
        // 初始化四个LinearLayout
        mTabWeiXin = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
        mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabSetting = (LinearLayout) findViewById(R.id.id_tab_settings);
        // 初始化四个按钮
        mWeiXinImg = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        mAddressImg = (ImageButton) findViewById(R.id.id_tab_address_img);
        mFrdImg = (ImageButton) findViewById(R.id.id_tab_frd_img);
        mSettingImg = (ImageButton) findViewById(R.id.id_tab_settings_img);
    }

    /**
     * 初始化ViewPage
     */
    private void initViewPage() {

        // 初妈化四个布局
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        tab01 = mLayoutInflater.inflate(R.layout.tab01, null);
        tab02 = mLayoutInflater.inflate(R.layout.tab02, null);
        tab03 = mLayoutInflater.inflate(R.layout.tab03, null);
        tab04 = mLayoutInflater.inflate(R.layout.tab04, null);

        mViews.add(tab01);
        mViews.add(tab02);
        mViews.add(tab03);
        mViews.add(tab04);

        // 适配器初始化并设置
        mPagerAdapter = new PagerAdapter() {

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mViews.get(position));

            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }

            @Override
            public int getCount() {

                return mViews.size();
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
    }

    /**
     * 判断哪个要显示，及设置按钮图片
     */
    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
            case R.id.id_tab_weixin:
                mViewPager.setCurrentItem(0,false);
                resetImg();
                mWeiXinImg.setImageResource(R.drawable.zhuye_pressed);
                break;
            case R.id.id_tab_address:
                mViewPager.setCurrentItem(1,false);
                resetImg();
                mAddressImg.setImageResource(R.drawable.fabu_pressed);
                break;
            case R.id.id_tab_frd:
                mViewPager.setCurrentItem(2,false);
                resetImg();
                mFrdImg.setImageResource(R.drawable.faxian_pressed);
                break;
            case R.id.id_tab_settings:
                mViewPager.setCurrentItem(3,false);
                resetImg();
                mSettingImg.setImageResource(R.drawable.wode_pressed);
                break;
            default:
                break;
        }
    }

    /**
     * 把所有图片变暗
     */
    private void resetImg() {
        mWeiXinImg.setImageResource(R.drawable.zhuye);
        mAddressImg.setImageResource(R.drawable.fabu);
        mFrdImg.setImageResource(R.drawable.faxian);
        mSettingImg.setImageResource(R.drawable.wode);
    }



    @Override
    public void onBackPressed() {
        if(isback){
            isback=false;
            // 结束所有Activity
            RongIM.getInstance().removeUnReadMessageCountChangedObserver(mCountListener);
            ExitApplication.getInstance().exit();
        }else{
            Toast.makeText(SearchActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            isback=true;
        }
    }


    /*
         * Dialog对话框提示用户删除操作 position为删除图片位置
         */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new Builder(SearchActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                data.remove(position);
                s.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // 响应startActivityForResult，获取图片路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {

                ContentResolver resolver = getContentResolver();
                try {
                    Uri uri = data.getData();
                    if(uri !=null) {
                        // 这里开始的第二部分，获取图片的路径：
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                        // 按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        // 最后根据索引值获取图片路径
                        photoPath = cursor.getString(column_index);
                    }
                    else {
                        Bundle extras = data.getExtras(); //拍照没有uri
                        if (extras != null) {
                            //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                            Bitmap bitmap = (Bitmap) extras.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                            if (bitmap != null) {
                                Uri uri2=Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                                if(uri2 !=null) {
                                    // 这里开始的第二部分，获取图片的路径：
                                    String[] proj = {MediaStore.Images.Media.DATA};
                                    Cursor cursor = getContentResolver().query(uri2, proj, null, null, null);
                                    // 按我个人理解 这个是获得用户选择的图片的索引值
                                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                    cursor.moveToFirst();
                                    // 最后根据索引值获取图片路径
                                    photoPath = cursor.getString(column_index);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (requestCode == 0x2 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    //处理代码在此地
                    location.setText(bundle.getString("address"));// 得到子窗口ChildActivity的回传数据
//                    Toast.makeText(SearchActivity.this, bundle.getString("address"),
//                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(photoPath)) {
            s.add(photoPath);
            //title.setText(photoPath);
            Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(photoPath, 300, 300);
            data.remove(data.size() - 1);
            Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
            data.add(newBp);
            data.add(bp);
            //将路径设置为空，防止在手机休眠后返回Activity调用此方法时添加照片
            photoPath = null;
            adapter.notifyDataSetChanged();
        }
        initUnreadCountListener();
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
                        // 简单的判断物品是否发布成功
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

    private void addGoods(String a, String b, String c, String d, String e, String f, String g, String h) {
        final String acc = a;
        final String tit= b;
        final String con = c;
        final String pri = d;
        final String mob = e;
        final String loc = f;
        final String p1 = g.replace("\\","/");
        final String p2 = h.replace("\\","/");

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String acc1 = URLEncoder.encode(acc, "UTF-8");
                    String tit1 = URLEncoder.encode(tit, "UTF-8");
                    String con1 = URLEncoder.encode(con, "UTF-8");
                    String pri1 = URLEncoder.encode(pri, "UTF-8");
                    String mob1 = URLEncoder.encode(mob, "UTF-8");
                    String loc1 = URLEncoder.encode(loc, "UTF-8");
                    String p11 = URLEncoder.encode(p1, "UTF-8");
                    String p21 = URLEncoder.encode(p2, "UTF-8");
                    String url = SERVER_ADDRESS+"/addGoods.jsp?account=" + acc1
                            + "&title=" + tit1 + "&content=" + con1 + "&price=" + pri1 + "&mobile=" + mob1
                            + "&location=" + loc1 + "&path1=" + p11 + "&path2=" + p21;
                    // 发送消息
                    handler.sendEmptyMessage(DISMISS);
                    Message msg = new Message();
                    msg.what = ADD_SUCCEESS;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void readAll(int startrow) {
        final String row = String.valueOf(startrow);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 打开链接
                String url = SERVER_ADDRESS+"/goods.jsp?startrow="+row;
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

    private void parserXml2(String xmlData) {
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

    private void initUnreadCountListener() {
        final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE};

        Handler _handler = new Handler();
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RongIM.getInstance().addUnReadMessageCountChangedObserver(mCountListener, conversationTypes);
            }
        }, 500);

        Handler __handler = new Handler();
        __handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RongIMClient.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
                    @Override
                    public void onChanged(ConnectionStatus connectionStatus) {
                        Log.e("SearchActivity", "融云连接状态监听--> " + connectionStatus.toString());
                        switch (connectionStatus) {
                            case CONNECTED://连接成功。
                                break;
                            case DISCONNECTED://断开连接。
                                break;
                            case CONNECTING://连接中。
                                break;
                            case NETWORK_UNAVAILABLE://网络不可用
                                break;
                            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                                        builder.setTitle("提示");
                                        builder.setMessage("您的帐号在异地登录，请重新登录");
                                        builder.setInverseBackgroundForced(true);
                                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(final DialogInterface dialog, final int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });
                                        builder.show();
                                    }
                                });

                                break;
                        }
                    }
                });
                     RongIM.getInstance().getRongIMClient().setConnectionStatusListener(mConnectionStatusListener);
                    //RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, conversationTypes);
            }
        }, 500);
    }


    public io.rong.imkit.manager.IUnReadMessageObserver mCountListener = new io.rong.imkit.manager.IUnReadMessageObserver() {
        @Override
        public void onCountChanged(int count) {
            Log.e("SearchActivity", "count:" + count);
            if (count == 0) {
                new QBadgeView(SearchActivity.this).bindTarget(huihua).setBadgeNumber(count);
 //               new QBadgeView(SearchActivity.this).bindTarget(huihua).hide(true);
//                mUnreadCount.setVisibility(View.GONE);
            } else if (count > 0 && count < 100) {
                new QBadgeView(SearchActivity.this).bindTarget(huihua).setBadgeNumber(count);
//               mUnreadCount.setVisibility(View.VISIBLE);
//                mUnreadCount.setText(count + "");
            } else {
                new QBadgeView(SearchActivity.this).bindTarget(huihua).setBadgeText("99+");
//                mUnreadCount.setVisibility(View.VISIBLE);
//                mUnreadCount.setText("···");
            }
        }
//setText
//        @Override
//        public void onMessageIncreased(int count) {
//            Log.e("SearchActivity", "count:" + count);
//            if (count == 0) {
//                new QBadgeView(SearchActivity.this).bindTarget(huihua).setBadgeNumber(count);
////                mUnreadCount.setVisibility(View.GONE);
//            } else if (count > 0 && count < 100) {
//                new QBadgeView(SearchActivity.this).bindTarget(huihua).setBadgeNumber(count);
////               mUnreadCount.setVisibility(View.VISIBLE);
////                mUnreadCount.setText(count + "");
//           } else {
//                new QBadgeView(SearchActivity.this).bindTarget(huihua).setBadgeText("99+");
////                mUnreadCount.setVisibility(View.VISIBLE);
////                mUnreadCount.setText("···");
//          }
//        }
    };

    public RongIMClient.ConnectionStatusListener mConnectionStatusListener = new RongIMClient.ConnectionStatusListener() {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            switch (connectionStatus) {
                case CONNECTED://连接成功。
                    break;
                case DISCONNECTED://断开连接。
                    break;
                case CONNECTING://连接中。
                    break;
                case NETWORK_UNAVAILABLE://网络不可用
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("您的帐号在异地登录，请重新登录");
                    builder.setInverseBackgroundForced(true);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    break;
            }
        }
    };

}
