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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.PrizeAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.PrizeInfo;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class MallActivity extends Activity {
    private final int GETALL = 1;
    private ImageView back,change;
    private String coins;
    private String username;
    // 物品显示列表
    private ArrayList<PrizeInfo> prizedata =new ArrayList<PrizeInfo>();
    private GridView gridprize;
    private ListView listprize;
    private PrizeAdapter prizeAdapter;
    private Dialog mDialog;
    private boolean isShowView = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mall);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        coins = intent.getStringExtra("coins");
        back = (ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        change = (ImageView)findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayout();
            }
        });
        gridprize = (GridView)findViewById(R.id.gridprize);
        gridprize.setEmptyView(findViewById(R.id.myText));
        listprize = (ListView)findViewById(R.id.listprize);
        listprize.setEmptyView(findViewById(R.id.myText));
        searchPrize();  //从服务度读取全部奖品信息
        mDialog = DialogUtils.createLoadingDialog(MallActivity.this, "加载中...");
        gridprize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PrizeInfo prizeInfo = prizedata.get(position);
                Intent intent = new Intent(MallActivity.this, PrizeSpecificActivity.class);
                intent.putExtra("prizeInfo",prizeInfo); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        listprize.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PrizeInfo prizeInfo = prizedata.get(i);
                Intent intent = new Intent(MallActivity.this, PrizeSpecificActivity.class);
                intent.putExtra("prizeInfo",prizeInfo); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isShowView = !isShowView;
                searchPrize();  //从服务度读取全部奖品信息
                //   goodsadapter.refresh(goodsdata);
                prizeAdapter.notifyDataSetChanged();
                refreshlayout.finishRefresh(2000);
            }
        });
    }

    /**
     *实现切换视图
     */
    private void setLayout() {
        if (isShowView) {
            if (gridprize == null) {
                gridprize = (GridView) findViewById(R.id.gridprize);
            }
            prizeAdapter = new PrizeAdapter(MallActivity.this, R.layout.prize_item, prizedata);
            gridprize.setVisibility(View.VISIBLE);
            gridprize.setAdapter(prizeAdapter);
            listprize.setVisibility(View.GONE);
            gridprize.setSelection(0);
            isShowView = !isShowView;
        } else {
            if (listprize == null) {
                listprize = (ListView) findViewById(R.id.listprize);
            }
            prizeAdapter = new PrizeAdapter(MallActivity.this, R.layout.prize_item2, prizedata);
            listprize.setVisibility(View.VISIBLE);
            listprize.setAdapter(prizeAdapter);
            gridprize.setVisibility(View.GONE);
            listprize.setSelection(0);//可将第一个item对我们可见显示，用于错乱，也可以不要
            isShowView = !isShowView;
        }
    }

    private void searchPrize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 打开链接
                String url = SERVER_ADDRESS+"/searchPrize.jsp";
                // 发送消息
                Message msg = new Message();
                msg.what = GETALL;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
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
            String str = String.format(" type = %d, str = %s\n", eventType, parse.getName());
            Log.d("xmlStr", str);

            String prizeid = "";
            String prizename = "";
            int prizecoins = 0;
            int number = 0;
            String pictureurl = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                result += nodeName;
                result += ", ";
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取3个参数
                        if ("prizeid".equals(nodeName)) {
                            String prizeidStr = parse.nextText();
                            result += "奖品id为：" + prizeidStr + ", ";
                            prizeid = prizeidStr;
                        } else if ("prizename".equals(nodeName)) {
                            String prizenameStr = parse.nextText();
                            result += "奖品名称为" + prizenameStr + ", ";
                            prizename = prizenameStr;
                        } else if ("prizecoins".equals(nodeName)) {
                            String prizecoinsStr = parse.nextText();
                            result += "所需金币数为" + prizecoinsStr + ", ";
                            prizecoins = Integer.parseInt(prizecoinsStr);
                        } else if ("number".equals(nodeName)) {
                            String numberStr = parse.nextText();
                            result += "剩余数量为" + numberStr + ", ";
                            number = Integer.parseInt(numberStr);
                        }else if ("pictureurl".equals(nodeName)) {
                            String pictureurlStr = parse.nextText();
                            result += "图片地址为" + pictureurlStr + ", ";
                            pictureurl = pictureurlStr;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        result += " \n ";
                        Log.d("end_tag", "节点结束");
                        // 添加数据
                        PrizeInfo info = new PrizeInfo(prizeid,prizename,prizecoins,number,pictureurl);
                        prizedata.add(info);
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
                case GETALL:
                    String response = (String) msg.obj;
                    prizedata.clear();
                    parserXml(response);
                    setLayout();
                    DialogUtils.closeDialog(mDialog);
                    break;
            }
        }
    };
}
