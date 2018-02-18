package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.utils.ExitApplication;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class SpecificActivity extends Activity {
    private ImageView back;
    private TextView conversation;
    private String token,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");//获取上个界面传递过来的token
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecificActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        conversation = (TextView)findViewById(R.id.conversation);
        conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RongIM.getInstance().startPrivateChat(SpecificActivity.this, username, "聊天中");
            }
        });
    }
}
