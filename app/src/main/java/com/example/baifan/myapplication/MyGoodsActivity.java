package com.example.baifan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.baifan.myapplication.activity.SearchActivity;
import com.example.baifan.myapplication.utils.ExitApplication;

public class MyGoodsActivity extends Activity {

    private ImageView back;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_goods);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);

        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyGoodsActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Toast.makeText(MyGoodsActivity.this, username, Toast.LENGTH_SHORT).show();
    }
}
