package com.example.baifan.myapplication.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;

public class ConversationActivity extends FragmentActivity {
    private TextView title;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        back = (ImageView) findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (TextView)findViewById(R.id.title);
        //会话界面 对方id
        String targetId = getIntent().getData().getQueryParameter("targetId");
        title.setText("与" + targetId + "聊天中");
        //对方 昵称
//        String title = getIntent().getData().getQueryParameter("title");
//        if (!TextUtils.isEmpty(title)){
//            setTitle("与" + title + "聊天中");
//        }
    }
}
