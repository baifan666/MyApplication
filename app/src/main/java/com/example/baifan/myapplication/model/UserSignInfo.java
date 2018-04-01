package com.example.baifan.myapplication.model;

/**
 * Created by baifan on 2018/3/31.
 */

public class UserSignInfo {
    private String username;
    private int signcount;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getSigncount() {
        return signcount;
    }
    public void setSigncount(int signcount) {
        this.signcount = signcount;
    }
    public UserSignInfo(String username,int signcount){
        this.username = username;
        this.signcount = signcount;
    }
}
