package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.R;

import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;


public class Search2Activity extends Activity {
    // 初始化搜索框变量
    private SearchView searchView;
    private String account,headurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);

        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        headurl = intent.getStringExtra("headurl");

        // 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);
        // 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                Intent intent = new Intent(Search2Activity.this, SearchResultActivity.class);
                intent.putExtra("guanjianzi",string); // 向下一个界面传递信息
                intent.putExtra("account",account);
                intent.putExtra("headurl", headurl);
                startActivity(intent);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });
    }
}
