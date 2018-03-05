package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.OrderSpecificInfo;

public class MyOrdersSpecificActivity extends Activity {
    private OrderSpecificInfo orderSpecificInfo;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_orders_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        orderSpecificInfo = (OrderSpecificInfo) intent.getSerializableExtra("orderSpecificInfo");
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
