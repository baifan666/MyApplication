package com.example.baifan.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

/**
 * Created by baifan on 2018/4/9.
 */

public class QQShareUtil {
    public static final String QQ_APP_ID = "101466661";//改成你在QQ开放平台审核通过的appID
    private Tencent tencent;
    public QQShareUtil() {
        super();
    }

    /**要分享必须先注册(QQ)*/
    public void regToQQ(Context context) {
        tencent = Tencent.createInstance(QQ_APP_ID, context);
    }

    public Tencent getTencent() {
        return tencent;
    }

    public void setTencent(Tencent tencent) {
        this.tencent = tencent;
    }
    public String getQqAppId() {
        return QQ_APP_ID;
    }

    /**分享到短信*/
    public Intent sendSMS(String description) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        //sendIntent.putExtra("address", "123456"); // 电话号码，这行去掉的话，默认就没有电话
        sendIntent.putExtra("sms_body", description);
        sendIntent.setType("vnd.android-dir/mms-sms");
        return sendIntent;
    }



    /**分享到QQ好友*/
    public void shareToQQ(Activity activity, String url, String shareTitle, String description, IUiListener uiListener){
        Bundle qqParams = new Bundle();
        qqParams.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        qqParams.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
        qqParams.putString(QQShare.SHARE_TO_QQ_SUMMARY,  description);
        qqParams.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  url);
        //qqParams.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        //qqParams.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "APP名称");
        tencent.shareToQQ(activity, qqParams, uiListener);
    }

    /**分享到QQ空间*/
    public void shareToQzone(Activity activity,String url,String imageUrl1,String imageUrl2,String shareTitle,String description,IUiListener uiListener){

        Bundle qzoneParams = new Bundle();
        qzoneParams.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        qzoneParams.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitle);//必填
        qzoneParams.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,  description);
        qzoneParams.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
        ArrayList<String> imageUrlList =new ArrayList<String>();
        if(!TextUtils.isEmpty(imageUrl1.trim())) {
            imageUrlList.add(imageUrl1);
            imageUrlList.add(imageUrl2);
            qzoneParams.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrlList);
        }
        tencent.shareToQzone(activity, qzoneParams, uiListener);
    }

}
