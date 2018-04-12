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
    private String headurl;  //头像
    private String openid;   //第三方登陆qq唯一标识
    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }
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
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public UserInfo(String username,String name) {
        this.username= username;
        this.name = name;
        this.headurl = "http://111.231.101.251:8080/fuwuduan/HeadPortrait/boy.png";
    }
}
