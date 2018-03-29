package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.model.PrizeInfo;
import com.example.baifan.myapplication.utils.DialogUtils;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class PrizeSpecificActivity extends Activity {
    private ImageView back,img;
    private PrizeInfo prizeInfo;
    private String path,url;
    private TextView prizename,prizecoins;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prize_specific);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        mDialog = DialogUtils.createLoadingDialog(PrizeSpecificActivity.this, "加载中...");
        back = (ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        prizeInfo = (PrizeInfo) intent.getSerializableExtra("prizeInfo");
        prizename = (TextView)findViewById(R.id.prizename);
        prizename.setText(prizeInfo.getPrizename());
        prizecoins = (TextView)findViewById(R.id.prizecoins);
        prizecoins.setText(String.valueOf(prizeInfo.getPrizecoins()));
        path = prizeInfo.getPictureurl().substring(prizeInfo.getPictureurl().lastIndexOf("/")+1);
        url = SERVER_ADDRESS+"/prize/"+path;
        img = (ImageView)findViewById(R.id.img);
        Glide.with(PrizeSpecificActivity.this).load(url).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                .listener( requestListener )
                .error(R.drawable.error)//图片加载失败后，显示的图片
                .into(img);


    }

    //设置错误监听
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(PrizeSpecificActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
            }
            // important to return false so the error placeholder can be placed
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog);
            return false;
        }
    };
}
