package com.example.baifan.myapplication.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.baifan.myapplication.activity.SearchActivity;
import com.example.baifan.myapplication.activity.SpecificActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

/**
 * 发送系统消息，工具类
 * Created by baifan on 2018/4/12.
 */

public class AddMessageUtils {
    private final int ADD_MESSAGE = 1;
    public void addMessage(String username,String content) {
        final String username1 = username;
        final String content1 = content;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/addMessage.jsp?username=" + username1 +
                        "&content="+ content1;
                Message msg = new Message();
                msg.what = ADD_MESSAGE;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_MESSAGE:
                    String response = (String) msg.obj;
                    if (parserXml(response)) {

                    } else {

                    }
                    break;
                default:
                    break;
            }
        }
    };

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
                        // 简单的判断是否成功
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
}
