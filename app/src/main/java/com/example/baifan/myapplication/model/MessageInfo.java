package com.example.baifan.myapplication.model;

import java.io.Serializable;

/**
 * Created by baifan on 2018/3/31.
 */

public class MessageInfo implements Serializable {
    private String username;
    private String messagetime;
    private String content;
    private int isdeleted;
    private String messageid;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getMessagetime() {
        return messagetime;
    }
    public void setMessagetime(String messagetime) {
        this.messagetime = messagetime;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getIsdeleted() {
        return isdeleted;
    }
    public void setIsdeleted(int isdeleted) {
        this.isdeleted = isdeleted;
    }
    public String getMessageid() {
        return messageid;
    }
    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }
}
