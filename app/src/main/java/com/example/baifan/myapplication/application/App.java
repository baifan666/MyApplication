package com.example.baifan.myapplication.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.mapapi.SDKInitializer;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.activity.MainActivity;
import com.example.baifan.myapplication.common.Config;
import com.example.baifan.myapplication.utils.AppFrontBackHelper;
import com.example.baifan.myapplication.utils.MyConversationBehaviorListener;
import com.example.baifan.myapplication.utils.NetBroadcastReceiver;
import com.example.baifan.myapplication.utils.NetUtil;
import com.tencent.smtt.sdk.QbSdk;

import java.util.LinkedList;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * Created by baifan on 2018/2/13.
 */

public class App extends Application implements NetBroadcastReceiver.NetChangeListener{
    private static Context mContext;
    public static NetBroadcastReceiver.NetChangeListener listener;
    private AlertDialog dialog = null;
    private NetBroadcastReceiver netBroadcastReceiver;
    private List<Activity> activityList = new LinkedList<Activity>();
    private static App instance;

    /**
     * 网络类型
     */
    private static int netType;

    public static Context getContext() {
        return mContext;
    }

    public static int getNetType() {
        return netType;
    }

    public App() {

    }

    // 单例模式中获取唯一的ExitApplication实例
    public static App getInstance() {
        if (null == instance) {
            instance = new App();
        }
        return instance;
    }

    // 将Activity添加到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 当要退出Activity时，遍历所有Activity 并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        SDKInitializer.initialize(getApplicationContext());
        RongIM.init(this);//初始化
        /**
         * 设置会话列表界面操作的监听器。
         */
        RongIM.setConversationClickListener(new MyConversationBehaviorListener());
        RongIM.getInstance().setMessageAttachedUserInfo(true);
        initTBS();

        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(App.this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
            }

            @Override
            public void onBack() {
                //应用切到后台处理
                showNotification();
            }
        });

        String curProcessName = getProcessName(mContext, android.os.Process.myPid());
        if(curProcessName != null && curProcessName.equalsIgnoreCase(mContext.getPackageName())){
            //初始化主线程资源
            listener = this;
            //Android 7.0以上需要动态注册
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //实例化IntentFilter对象
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                netBroadcastReceiver = new NetBroadcastReceiver();
                //注册广播接收
                registerReceiver(netBroadcastReceiver, filter);
            }
            checkNet();
        }else{
            //初始化其它进程的资源
        }
    }

    /**
     * 初始化时判断有没有网络
     */
    public boolean checkNet() {
        this.netType = NetUtil.getNetWorkState(mContext);
        if (!isNetConnect()) {
            //网络异常，请检查网络
            showNetDialog();

        }
        return isNetConnect();
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onChangeListener(int netMobile) {
        this.netType = netMobile;
        if (!isNetConnect()) {
            showNetDialog();
        } else {
            hideNetDialog();
        }
    }

    /*
    * 弹出设置网络框
    * 弹出对话框的步骤：
    *  1.创建alertDialog的builder.
    *  2.要给builder设置属性, 对话框的内容,样式,按钮
    *  3.通过builder 创建一个对话框
    *  4.对话框show()出来
    */
    private void showNetDialog() {
        if (dialog == null) {
            final AlertDialog.Builder builer = new AlertDialog.Builder(this);
            builer.setTitle("提醒");
            builer.setMessage("网络异常，请检查网络");
            //当点确定按钮时
            builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    hideNetDialog();
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                }
            });
            //当点取消按钮时
            builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog = builer.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        } else {

        }

    }

    /**
     * 隐藏设置网络框
     */
    private void hideNetDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netType == 1) {
            return true;
        } else if (netType == 0) {
            return true;
        } else if (netType == -1) {
            return false;
        }
        return false;
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 初始化TBS浏览服务X5内核
     */
    private void initTBS() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("app", " onViewInitFinished is " + arg0);
            }
            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void showNotification() {
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle(Config.getU());
        builder.setContentText("校园二手交易平台正在后台运行");
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setWhen(System.currentTimeMillis());// 通知产生的时间，会在通知信息里显示

        Intent resultIntent = new Intent(Intent.ACTION_MAIN); // 启动栈顶的activity
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setClass(mContext, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        // 设改通知将要启动程序的Intent
        builder.setContentIntent(resultPendingIntent);

        Notification notification = builder.build();
        // 定义Notification的各种属性
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS = 5000;

        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }

}
