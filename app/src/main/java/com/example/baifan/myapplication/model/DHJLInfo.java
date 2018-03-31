package com.example.baifan.myapplication.model;

import java.io.Serializable;

/**
 * Created by baifan on 2018/3/31.
 */

public class DHJLInfo implements Serializable {
    private String username;
    private String prizename;
    private String dhtime;
    private String mobile;
    private String address;
    private String name;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPrizename() {
        return prizename;
    }
    public void setPrizename(String prizename) {
        this.prizename = prizename;
    }
    public String getDhtime() {
        return dhtime;
    }
    public void setDhtime(String dhtime) {
        this.dhtime = dhtime;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
