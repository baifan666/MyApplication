package com.example.baifan.myapplication.model;

import java.io.Serializable;

/**
 * Created by baifan on 2018/3/26.
 */

public class PrizeInfo implements Serializable {
    private String prizeid;
    private String prizename;
    private int prizecoins;
    private int number;
    private String pictureurl;

    public String getPrizeid() {
        return prizeid;
    }

    public void setPrizeid(String prizeid) {
        this.prizeid = prizeid;
    }

    public String getPrizename() {
        return prizename;
    }

    public void setPrizename(String prizename) {
        this.prizename = prizename;
    }

    public int getPrizecoins() {
        return prizecoins;
    }

    public void setPrizecoins(int prizecoins) {
        this.prizecoins = prizecoins;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }
    public PrizeInfo( String prizeid,String prizename,int prizecoins,int number,String pictureurl) {
        this.prizeid = prizeid;
        this.prizename = prizename;
        this.prizecoins = prizecoins;
        this.number = number;
        this.pictureurl = pictureurl;
    }
}
