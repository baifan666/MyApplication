package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.WxShareAndLoginUtils;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class ShareSelectActivity extends Activity implements View.OnClickListener {
    private Button btn_cancel;
    private ImageView pengyouquan,pengyou;
    private LinearLayout layout;
    private Intent intent;
    private String title,content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_select);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
        getWindow().setAttributes(p);

        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        pengyouquan = (ImageView) this.findViewById(R.id.pengyouquan); //拍照
        pengyou = (ImageView) this.findViewById(R.id.pengyou);  //从相册选择
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);   //取消
        layout = (LinearLayout) findViewById(R.id.pop_layout);
        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // 添加按钮监听
        btn_cancel.setOnClickListener(this);
        pengyouquan.setOnClickListener(this);
        pengyou.setOnClickListener(this);
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {  //点击外面退出这activity
        finish();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pengyouquan:     //分享到朋友圈
//                WxShareAndLoginUtils.WxTextShare(title, WxShareAndLoginUtils.WECHAT_MOMENT);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                WxShareAndLoginUtils.WxUrlShare(SERVER_ADDRESS+"/校园二手交易平台.png", title, content, bitmap, WxShareAndLoginUtils.WECHAT_MOMENT);
                break;
            case R.id.pengyou:    // 分享到朋友
//                WxShareAndLoginUtils.WxTextShare(title, WxShareAndLoginUtils.WECHAT_FRIEND);
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                WxShareAndLoginUtils.WxBitmapShare(bitmap,WxShareAndLoginUtils.WECHAT_MOMENT);
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                WxShareAndLoginUtils.WxUrlShare(SERVER_ADDRESS+"/校园二手交易平台.png", title, content, bitmap1, WxShareAndLoginUtils.WECHAT_FRIEND);
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }

    }
}
