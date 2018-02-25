package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.GoodsInfo;

import io.rong.imkit.RongIM;

import static com.tencent.smtt.sdk.TbsReaderView.TAG;

public class SpecificActivity extends Activity {
    private ImageView back,imageView1,imageView2;
    TextView username,publishtime,title,content,price,location,mobile;
    private String path1,path2;
    private Button conversation,buy;
    private GoodsInfo goodsInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        goodsInfo = (GoodsInfo) intent.getSerializableExtra("goodsInfo");
        username = (TextView)findViewById(R.id.username);
        username.setText(goodsInfo.getUsername());
        publishtime = (TextView)findViewById(R.id.publish_time);
        publishtime.setText(String.valueOf(goodsInfo.getPublish_time()));
        title = (TextView)findViewById(R.id.title);
        title.setText(String.valueOf(goodsInfo.getTitle()));
        content = (TextView)findViewById(R.id.content);
        content.setText(String.valueOf(goodsInfo.getContent()));
        price = (TextView)findViewById(R.id.price);
        price.setText(String.valueOf(goodsInfo.getPrice()));
        location = (TextView)findViewById(R.id.location);
        location.setText(String.valueOf(goodsInfo.getLocation()));
        mobile = (TextView)findViewById(R.id.mobile);
        mobile.setText(String.valueOf(goodsInfo.getMobile()));
        path1 = goodsInfo.getPath1().substring(goodsInfo.getPath1().lastIndexOf("/")+1);
        path2 = goodsInfo.getPath2().substring(goodsInfo.getPath2().lastIndexOf("/")+1);
        imageView1 = (ImageView)findViewById(R.id.image_view1);
        imageView2 = (ImageView)findViewById(R.id.image_view2);
        String url1 = "http://111.231.101.251:8080/fuwuduan/upload/"+path1;
        String url2 = "http://111.231.101.251:8080/fuwuduan/upload/"+path2;
        Glide.with(this).load(url1).into(imageView1);
        Glide.with(this).load(url2).into(imageView2);

        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        conversation = (Button)findViewById(R.id.conversation);
        conversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RongIM.getInstance().startPrivateChat(SpecificActivity.this, goodsInfo.getUsername(), "聊天中");
            }
        });
        buy = (Button)findViewById(R.id.buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    /*
  *
  * 弹出对话框
  *
  * 弹出对话框的步骤：
  *  1.创建alertDialog的builder.
  *  2.要给builder设置属性, 对话框的内容,样式,按钮
  *  3.通过builder 创建一个对话框
  *  4.对话框show()出来
  */
    protected void showDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("二次确认");
        builer.setMessage("确定要购买吗？请务必和发布人确认清楚哦！");
        //当点确定按钮时
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"");
            }
        });

        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }
}
