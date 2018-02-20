package com.example.baifan.myapplication.model;

/**
 * Created by baifan on 2018/1/12.
 */

public class GoodsInfo {
    private String id;
    private String username;
    private String title;
    private String content;
    private double price;
    private String mobile;
    private String location;
    private String publish_time;
    private String deleted;
    private String path1;
    private String path2;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getParh1() {
        return path1;
    }

    public void setPath1(String path1) {
        this.path1 = path1;
    }

    public String getParh2() {
        return path2;
    }

    public void setPath2(String path2) {
        this.path2 = path2;
    }

    // 对应列表中显示的物品
    public GoodsInfo(String id, String username,String title,String publish_time,String content,
                double price, String mobile, String location, String path1,String path2) {
        this.id = id;
        this.username= username;
        this.title = title;
        this.publish_time = publish_time;
        this.content = content;
        this.price = price;
        this.mobile = mobile;
        this.location = location;
        this.path1 = path1;
        this.path2 = path2;
    }
}
