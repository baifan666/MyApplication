package com.example.baifan.myapplication.model;

import java.io.Serializable;

/**
 * Created by baifan on 2018/3/24.
 */

public class UserOpinionInfo implements Serializable {
    private String username; //用户名
    private String content; //反馈内容
    private String feedbacktime; //反馈时间

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeedbacktime() {
        return feedbacktime;
    }

    public void setFeedbacktime(String feedbacktime) {
        this.feedbacktime = feedbacktime;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    private String contacts; //联系方式

    public UserOpinionInfo(String username, String content, String feedbacktime, String contacts) {
        this.username = username;
        this.content = content;
        this.feedbacktime = feedbacktime;
        this.contacts =contacts;
    }
}
