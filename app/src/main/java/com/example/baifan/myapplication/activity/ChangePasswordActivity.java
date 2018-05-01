package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.utils.AES256Encryption;
import com.example.baifan.myapplication.utils.AddMessageUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import sun.misc.BASE64Encoder;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;


public class ChangePasswordActivity extends Activity {

    private String username,old,new1,new2,result;
    private EditText oldpassword,newpassword1,newpassword2;
    private CheckBox iv1,iv2,iv3;
    private ImageView back;
    private Button btn_cancel,btn_confirm;
    private byte[] data,key;
    private BASE64Encoder base64Encoder;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);

        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        back = (ImageView)findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        oldpassword = (EditText)findViewById(R.id.oldpassword);
        newpassword1 = (EditText)findViewById(R.id.newpassword1);
        newpassword2 = (EditText)findViewById(R.id.newpassword2);
        iv1=(CheckBox)findViewById(R.id.iv_hide1) ;   //显示、隐藏密码
        iv1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //如果选中，显示密码
                    oldpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    oldpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });
        iv2=(CheckBox)findViewById(R.id.iv_hide2) ;   //显示、隐藏密码
        iv2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //如果选中，显示密码
                    newpassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    newpassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        iv3=(CheckBox)findViewById(R.id.iv_hide3) ;   //显示、隐藏密码
        iv3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //如果选中，显示密码
                    newpassword2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    newpassword2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                old = oldpassword.getText().toString();
                new1 = newpassword1.getText().toString();
                new2 = newpassword2.getText().toString();
                try {
                    key = AES256Encryption.getKeyByPass();
                    // 加密
                    data = AES256Encryption.encrypt(old.getBytes(), key);
                    base64Encoder = new BASE64Encoder();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDialog = DialogUtils.createLoadingDialog(ChangePasswordActivity.this, "修改中...");
                whetherRegister(username, base64Encoder.encode(data));
            }
        });
    }

    private Handler handler;

    {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if(result.equals("succeessful")) {
                            if(!new1.equals(new2)) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                                dialog.setTitle("This is a warnining!");
                                dialog.setMessage("新密码和确认密码不一致!");
                                dialog.setCancelable(false);
                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                dialog.show();
                            }else {
                                try {
                                    key = AES256Encryption.getKeyByPass();
                                    // 加密
                                    data = AES256Encryption.encrypt(new1.getBytes(), key);
                                    base64Encoder = new BASE64Encoder();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                changePassword(username,base64Encoder.encode(data));
                            }
                            DialogUtils.closeDialog(mDialog);
                        }
                        else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("对不起，旧密码输入有误!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            dialog.show();
                        }
                        DialogUtils.closeDialog(mDialog);
                        break;
                    case 2:
                        String response1 = (String) msg.obj;
                        parserXml(response1);
                        if(result.equals("succeessful")) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                            dialog.setTitle("成功");
                            dialog.setMessage("恭喜您，密码修改成功!请重新登陆");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                    //获取当前时间
                                    Date date = new Date(System.currentTimeMillis());
                                    String str = simpleDateFormat.format(date)+"修改了密码，请确认是本人操作。";
                                    //发送系统消息
                                    AddMessageUtils.addMessage(username,str);
                                    Intent intent=new Intent();
                                    intent.setClass(ChangePasswordActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                            dialog.show();
                        }
                        DialogUtils.closeDialog(mDialog);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void changePassword(String account1,String password1) {
        final String account = account1;
        final String password = password1;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/changePassword.jsp?account=" + account+ "&newPassword=" + password;
                Message msg = new Message();
                msg.what = 2;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void whetherRegister(String account2, String password2) {
        final String account = account2; // 进程中不能传入变量 一定要为常量final
        final String password = password2;

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String account1 = URLEncoder.encode(account, "UTF-8");
                    String password1 = URLEncoder.encode(password, "UTF-8");
                    String url = SERVER_ADDRESS+"/dengLu.jsp?account=" + account1+ "&password=" + password1;
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                    // Handler
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parserXml(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();
            String str = String.format(" type = %d, str = %s\n", eventType, parse.getName());
            Log.d("xmlStr", str);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parse.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        // 从数据库读取2个参数
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                            Log.d("result", result);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d("end_tag", "节点结束");
                        break;
                    default:
                        break;
                }
                eventType = parse.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
