package com.example.baifan.myapplication.model;

/**
 * Created by baifan on 2018/4/11.
 */

public class NoticeInfo {
    private String NoticeTitle;  //通知标题
    private String NoticeTime;   //通知时间
    private String NoticeUrl;    //通知链接地址

    public String getNoticeUrl() {
        return NoticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        NoticeUrl = noticeUrl;
    }

    public String getNoticeTitle() {
        return NoticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        NoticeTitle = noticeTitle;
    }

    public String getNoticeTime() {
        return NoticeTime;
    }

    public void setNoticeTime(String noticeTime) {
        NoticeTime = noticeTime;
    }

    public NoticeInfo(String noticeTitle, String noticeTime, String noticeUrl) {
        this.NoticeTitle = noticeTitle;
        this.NoticeTime = noticeTime;
        this.NoticeUrl = noticeUrl;
    }
}
