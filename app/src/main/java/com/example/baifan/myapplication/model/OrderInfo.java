package com.example.baifan.myapplication.model;

import java.io.Serializable;

/**
 * Created by baifan on 2018/3/4.
 */

public class OrderInfo implements Serializable {
    private String orderid;
    private String ordertime;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public String getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid;
    }

    public String getBuyerid() {
        return buyerid;
    }

    public void setBuyerid(String buyerid) {
        this.buyerid = buyerid;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public String getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(String isfinish) {
        this.isfinish = isfinish;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    private String goodsid;
    private String buyerid;
    private String sellerid;
    private String isfinish;
    private String finishtime;

    // 对应列表中显示的物品
    public OrderInfo(String orderid, String ordertime,String goodsid,String buyerid,String sellerid,
                     String isfinish, String finishtime) {
        this.orderid = orderid;
        this.ordertime = ordertime;
        this.goodsid = goodsid;
        this.buyerid = buyerid;
        this.sellerid = sellerid;
        this.isfinish = isfinish;
        this.finishtime = finishtime;
    }
}
