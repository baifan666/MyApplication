package com.example.baifan.myapplication.model;

/**
 * Created by baifan on 2018/2/18.
 */

public class UserInfo {
    private String username; //用户名
    private String userpassowrd; //密码
    private String name;  //昵称
    private String mobile;  //手机号
    private String coins;  //金币
    private String head_url;  //头像

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassowrd() {
        return userpassowrd;
    }

    public void setUserpassowrd(String userpassowrd) {
        this.userpassowrd = userpassowrd;
    }

    public String getName() {
        return name;
    }

    public void setName(String content) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String location) {
        this.coins = coins;
    }


    // 对应列表中显示的物品
    public UserInfo(String username,String name) {
        this.username= username;
        this.name = name;
        this.head_url = "http://111.231.101.251:8080/fuwuduan/HeadPortrait/boy.png";
    }
}
