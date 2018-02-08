package com.example.baifan.myapplication.activity;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.baifan.myapplication.utils.ExitApplication;
import com.example.baifan.myapplication.R;

/**
 * Created by baifan on 2018/1/8.
 */

public class SelectPicPopupWindow extends Activity implements View.OnClickListener {
    private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic_popup_window);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的1.0
        getWindow().setAttributes(p);

        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        intent = getIntent();
        btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo); //拍照
        btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);  //从相册选择
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);   //取消

        layout = (LinearLayout) findViewById(R.id.pop_layout);

        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // 添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {  //点击外面退出这activity
        finish();
        return true;
    }

    @Override  //startActivityResult()后调用的这下面方法
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {   // 选择完, 拍照,或者选择图片后调用的方法
        if (resultCode != RESULT_OK) {
            return;
        }
        //选择完或者拍完照后会在这里处理，然后我们继续使用setResult返回Intent以便可以传递数据和调用
        if (data.getExtras() != null)
            intent.putExtras(data.getExtras());   //拍照得到的图片
        if (data.getData()!= null)
            intent.setData(data.getData());   //选择图片得到的数据, 里面有uri
        setResult(RESULT_OK, intent);     // 返回到下面的, MainActivity
        finish();

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:     //拍照
                try {
                    //拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_pick_photo:    // 选择图片
                try {
                    //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
                    //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
                    //Intent intent = new Intent();
                    //intent.setType("image/*");
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    //startActivityForResult(intent, 2);
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, 2);
                }
                catch (ActivityNotFoundException e) {
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }

    }


}