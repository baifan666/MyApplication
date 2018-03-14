package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;

public class AccountManagementActivity extends Activity {
    private TextView exitapp,updatepassword;
    private String account;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_account_management);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        account = intent.getStringExtra("username");
        back = (ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        exitapp = (TextView)findViewById(R.id.exitapp);
        exitapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        updatepassword = (TextView)findViewById(R.id.updatepassword);
        updatepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(AccountManagementActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username",account); // 向下一个界面传递信息
                startActivity(intent);
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
        builer.setTitle("退出登陆");
        builer.setMessage("确定要退出登陆吗？点击确定将返回登陆页面");
        //当点确定按钮时
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AccountManagementActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //当点取消按钮时
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }
}
