package com.example.baifan.myapplication.common;

/**
 * Created by baifan on 2018/4/7.
 */

public class Config {
    public static final String APP_ID = "wx9140df8c937eba09";
    public static final String APP_SERECET = "e0a03153947b663e33015973df28b860";

    private static String u = "未登陆";

    public static String getU() {
        return u;
    }

    public static void setU(String u) {
        Config.u = u;
    }
}
