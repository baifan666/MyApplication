package com.example.baifan.myapplication.utils;


import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by baifan on 2018/2/13.
 */

public class HttpUtils {
    public static StringBuilder connection(String url) {
        // Http链接
        HttpURLConnection connection = null;
        try {
            URL url1 = new URL(url);
            connection = (HttpURLConnection) url1.openConnection();
            // 设置属性
            connection.setRequestMethod("GET");
            // Post 1)容量没有限制 2） 安全
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            // 读取数据
            // 1)获取位流
            InputStream in = connection.getInputStream();
            // 二进制-->BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            // 2) 读取
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect(); // 断开链接
            }
        }
    }
}
