package com.example.baifan.myapplication.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.baifan.myapplication.R;

public class ConversationActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //会话界面 对方id
        //String targetId = getIntent().getData().getQueryParameter("targetId");
        //对方 昵称
        String title = getIntent().getData().getQueryParameter("title");
        if (!TextUtils.isEmpty(title)){
            setTitle("与" + title + "聊天中");
        }
    }
}
