package com.example.baifan.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.UpdataInfo;
import com.example.baifan.myapplication.utils.CacheUtil;
import com.example.baifan.myapplication.utils.DownLoadManager;
import com.example.baifan.myapplication.utils.UpdataInfoParser;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ManagerActivity extends AppCompatActivity {
    private boolean isback;
    private TextView cache,gengxin,deletecache,zhanghaoguanli,scgl,ckfk;
    private UpdataInfo info;
    private String localVersion,account;
    private final String TAG = this.getClass().getName();
    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);

        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        cache = (TextView)findViewById(R.id.cache);
        try {
            cache.setText(CacheUtil.getTotalCacheSize(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        gengxin = (TextView)findViewById(R.id.gengxin);
        gengxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    localVersion = getVersionName();
                    ManagerActivity.CheckVersionTask cv = new ManagerActivity.CheckVersionTask();
                    new Thread(cv).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        deletecache=(TextView)findViewById(R.id.deletecache);
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
        zhanghaoguanli = (TextView)findViewById(R.id.zhanghaoguanli);
        zhanghaoguanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(ManagerActivity.this, AccountManagementActivity.class);
                intent.putExtra("username",account); // 向下一个界面传递信息
                startActivity(intent);
            }
        });
        scgl = (TextView)findViewById(R.id.scgl);
        scgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ckfk = (TextView)findViewById(R.id.ckfk);
        ckfk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(ManagerActivity.this, CheckUserOpinionActivity.class);
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

    @Override
    public void onBackPressed() {
        if(isback){
            isback=false;
            // 结束所有Activity
            ExitApplication.getInstance().exit();
        }else{
            Toast.makeText(ManagerActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            isback=true;
        }
    }

}
