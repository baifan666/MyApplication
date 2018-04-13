package com.example.baifan.myapplication.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

/**
 * 发送系统消息，工具类
 * Created by baifan on 2018/4/12.
 */

public class AddMessageUtils {
    public static void addMessage(String username,String content) {
        final String username1 = username;
        final String content1 = content;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String username2 = URLEncoder.encode(username1, "UTF-8");
                    String content2 = URLEncoder.encode(content1, "UTF-8");
                    String url = SERVER_ADDRESS + "/sendSystemMessage.jsp?content=" + content2
                            + "&toUserId=" + username2;
                    HttpUtils.connection(url).toString();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
