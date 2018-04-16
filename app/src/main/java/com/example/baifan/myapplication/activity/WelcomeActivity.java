package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.content.Intent;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.UpdataInfo;
import com.example.baifan.myapplication.utils.AddMessageUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.DownLoadManager;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.example.baifan.myapplication.utils.UpdataInfoParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

/**
 * Created by baifan on 2017/11/13.
 */

public class WelcomeActivity extends Activity {
    private String localVersion;
    private String usertoken,result,headurl,account,password;   //headurl存储服务器读出的头像连接
    private int flag;
    private UpdataInfo info;
    private final String TAG = this.getClass().getName();
    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    private final int LOGIN = 5;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public io.rong.imlib.model.UserInfo getUserInfo(String arg0) {
                io.rong.imlib.model.UserInfo customer = new io.rong.imlib.model.UserInfo("SYSTEM", "系统消息", Uri.parse(SERVER_ADDRESS+"/HeadPortrait/system.png"));
                return customer;
            }
        }, true);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isCheck = pref.getBoolean("autologin",false);
        if(isCheck) {
            account=pref.getString("account","");
            password=pref.getString("password","");
            whetherRegister(account, password);
        }else {
            checkUpdate();
        }
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            finish();
            return;
        }
    }

    private void checkUpdate() {
        try {
            localVersion = getVersionName();
            WelcomeActivity.CheckVersionTask cv = new WelcomeActivity.CheckVersionTask();
            new Thread(cv).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(WelcomeActivity.this,
                                    MainActivity.class));
                            finish();
                        }
                    }, 3000);
                    break;
                case UPDATA_CLIENT:
                    //对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    //服务器超时
                    Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(WelcomeActivity.this,
                                    MainActivity.class));
                            finish();
                        }
                    }, 3000);
                    break;
                case DOWN_ERROR:
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(WelcomeActivity.this,
                                    MainActivity.class));
                            finish();
                        }
                    }, 3000);
                    break;
                case LOGIN:
                    String response = (String) msg.obj;
                    parserXml(response);
                    if(result.equals("succeessful")) {
                        RongIM.connect(usertoken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                Log.e("MainActivity", "--onTokenIncorrect");
                            }

                            @Override
                            public void onSuccess(String userid) {
                                Log.d("MainActivity", "--onSuccess--" + userid);
                                if(0 == flag) {
                                    Toast.makeText(WelcomeActivity.this, "登录成功,用户：" + userid, Toast.LENGTH_SHORT).show();
                                    RongIM.getInstance().setCurrentUserInfo(new io.rong.imlib.model.UserInfo(userid, userid, Uri.parse(headurl)));
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                    //获取当前时间
                                    Date date = new Date(System.currentTimeMillis());
                                    String str = simpleDateFormat.format(date)+"在"+ Build.MODEL +"登陆成功";
                                    AddMessageUtils.addMessage(userid,str);
                                    Intent intent = new Intent(WelcomeActivity.this, SearchActivity.class);
                                    intent.putExtra("account", account);
                                    intent.putExtra("headurl",headurl);
                                    intent.putExtra("autologin","true");   //自动登陆传递一个参数给主页面
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(WelcomeActivity.this, "管理员，您好！" , Toast.LENGTH_SHORT).show();
                                    //服务器连接成功，跳转
                                    RongIM.getInstance().setCurrentUserInfo(new io.rong.imlib.model.UserInfo(userid, userid, Uri.parse(headurl)));
                                    Intent intent = new Intent(WelcomeActivity.this, ManagerActivity.class);
                                    intent.putExtra("account", account);
                                    intent.putExtra("headurl",headurl);
                                    intent.putExtra("autologin","true");   //自动登陆传递一个参数给主页面
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Toast.makeText(WelcomeActivity.this, errorCode.toString(), Toast.LENGTH_SHORT).show();
                                Log.e("MainActivity", "--onError");
                            }
                        });
                    }
                    else {
                        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(WelcomeActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，您的账户或密码有错!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(WelcomeActivity.this,
                                        MainActivity.class));
                                finish();
                            }
                        });
                        dialog.show();
                    }
                    result = "";
                    break;
                default:
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
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("检查到应用有新版本");
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
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
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
                    sleep(2000);
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

    private void whetherRegister(String account2, String password2) {
        final String account = account2; // 进程中不能传入变量 一定要为常量final
        final String password = password2;

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String account1 = URLEncoder.encode(account, "UTF-8");
                    String password1 = URLEncoder.encode(password, "UTF-8");
                    String url = SERVER_ADDRESS+"/dengLu.jsp?account=" + account1+ "&password=" + password1;
                    Message msg = new Message();
                    msg.what = LOGIN;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parserXml(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String str = String.format(" type = %d, str = %s\n", eventType, parse.getName());
            Log.d("xmlStr", str);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("flag".equals(nodeName)) {
                            String flagStr = parse.nextText();
                            flag = Integer.parseInt(flagStr);
                        }
                        if ("token".equals(nodeName)) {
                            String tokenStr = parse.nextText();
                            usertoken = tokenStr;
                        }
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                        }
                        if ("headurl".equals(nodeName)) {
                            String headurlStr = parse.nextText();
                            headurl = headurlStr;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d("end_tag", "节点结束");
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

}
