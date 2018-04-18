package com.example.baifan.myapplication.application;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.activity.MainActivity;
import com.example.baifan.myapplication.activity.SearchActivity;
import com.example.baifan.myapplication.common.Config;
import com.example.baifan.myapplication.utils.AppFrontBackHelper;
import com.example.baifan.myapplication.utils.MyConversationBehaviorListener;
import com.tencent.smtt.sdk.QbSdk;

import io.rong.imkit.RongIM;

/**
 * Created by baifan on 2018/2/13.
 */

public class App extends Application {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
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
