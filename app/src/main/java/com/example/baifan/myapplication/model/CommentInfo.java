package com.example.baifan.myapplication.model;

/**
 * Created by baifan on 2018/4/16.
 */

public class CommentInfo {
    private String commentid;
    private String username;
    private String goodsid;
    private String headurl;
    private String local;
    private String content;
    private String commenttime;
    private String replyed;

    public String getCommentid() {
        return commentid;
    }
    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getGoodsid() {
        return goodsid;
    }
    public void setGoodsid(String goodsid) {
        this.goodsid = goodsid;
    }
    public String getHeadurl() {
        return headurl;
    }
    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }
    public String getLocal() {
        return local;
    }
    public void setLocal(String local) {
        this.local = local;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCommenttime() {
        return commenttime;
    }
    public void setCommenttime(String commenttime) {
        this.commenttime = commenttime;
    }
    public String getReplyed() {
        return replyed;
    }
    public void setReplyed(String replyed) {
        this.replyed = replyed;
    }

    public CommentInfo( String commentid,String username,String goodsid,
                        String headurl,String local,String content,
                        String commenttime,String replyed) {
        this.commentid = commentid;
        this.username = username;
        this.goodsid = goodsid;
        this.headurl = headurl;
        this.local = local;
        this.content = content;
        this.commenttime = commenttime;
        this.replyed = replyed;
    }
}
