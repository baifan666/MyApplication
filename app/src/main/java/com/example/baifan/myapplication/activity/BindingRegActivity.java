package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.utils.AES256Encryption;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import sun.misc.BASE64Encoder;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class BindingRegActivity extends Activity {
    private ImageView back;
    private Dialog mDialog;
    private String openid,headurl;
    private String new_act, new_psd1, new_psd2, phone,name;
    private EditText new_act_edit, new_psd1_edit, new_psd2_edit, phone_edit,name_edit;
    private Button reg;
    private BASE64Encoder base64Encoder;
    private byte[] data,key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_binding_reg);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        Intent intent = getIntent();
        openid = intent.getStringExtra("openid");
        headurl = intent.getStringExtra("headurl");
        new_act_edit = (EditText) findViewById(R.id.new_act); // 用户名
        new_psd1_edit = (EditText) findViewById(R.id.new_psd1); // 密码
        new_psd2_edit = (EditText) findViewById(R.id.new_psd2); // 确认密码
        phone_edit = (EditText) findViewById(R.id.mobile); // 手机号码
        name_edit =(EditText)findViewById(R.id.name); //姓名
        back=(ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reg = (Button)findViewById(R.id.register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_act = new_act_edit.getText().toString(); // 获取各个控件的值
                new_psd2 = new_psd2_edit.getText().toString();
                new_psd1 = new_psd1_edit.getText().toString();
                phone = phone_edit.getText().toString();
                name =name_edit.getText().toString();
                // 账户不能为空
                if (TextUtils.isEmpty(new_act)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BindingRegActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("用户名不能为空哦!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
                // 密码不能为空
                else if (TextUtils.isEmpty(new_psd1) || TextUtils.isEmpty(new_psd2)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BindingRegActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("密码不能为空哦!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
                // 两次输入的密码不一致
                else if (!new_psd1.equals(new_psd2)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BindingRegActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("您两次输入的密码不一致哦!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
                // 手机号码不能为空
                else if (TextUtils.isEmpty(phone)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BindingRegActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("手机号码不能为空哦!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
                // 姓名不能为空
                else if (TextUtils.isEmpty(name)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BindingRegActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("姓名不能为空哦!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }
                else {
                    mDialog = DialogUtils.createLoadingDialog(BindingRegActivity.this, "注册中...");
                    try {
                        key = AES256Encryption.getKeyByPass();
                        // 加密
                        data = AES256Encryption.encrypt(new_act_edit.getText().toString().getBytes(), key);
                        base64Encoder = new BASE64Encoder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addQQCUser(new_act, base64Encoder.encode(data), name, phone,openid,headurl);
                }
            }
        });
    }

    private Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String response = (String) msg.obj;
                    if (parserXml(response)) {
                        DialogUtils.closeDialog(mDialog);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(BindingRegActivity.this);
                        dialog.setTitle("Success!");
                        dialog.setMessage("注册绑定成功，您可以使用qq授权登陆或者使用用户名和密码登陆!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 注册成功后 跳转至登录界面
                                Intent intent = new Intent(BindingRegActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                    } else {
                        DialogUtils.closeDialog(mDialog);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(BindingRegActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，您注册的账户名已存在!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private boolean parserXml(String xmlData) {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parse = factory.newPullParser(); // 生成解析器
            parse.setInput(new StringReader(xmlData)); // 添加xml数据
            int eventType = parse.getEventType();

            String result = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String nodeName = parse.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                            Log.d("whether", result);
                        }
                        if (result.equals("succeessful"))
                            return true;
                        else if (result.equals("failed"))
                            return false;
                        break;
                }
                eventType = parse.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private void addQQCUser(String aa, String pp, String ss, String celll,String openid1,String headurl1) {
        final String a = aa; // 进程中不能传入变量 一定要为常量final
        final String p = pp;
        final String s = ss;
        final String cell = celll;
        final String openid2 = openid1;
        final String headurl2 = headurl1;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String a2 = URLEncoder.encode(a, "UTF-8"); // 中文转译！
                    String s2 = URLEncoder.encode(s, "UTF-8"); // 中文转译！
                    // 打开链接
                    String url = SERVER_ADDRESS+"/addQQUser.jsp?account=" + a2 + "&password="
                            + p + "&name=" + s2 + "&phone=" + cell+ "&openid="+ openid2 + "&headurl="+ headurl2;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = HttpUtils.connection(url).toString();
                    _handler.sendMessage(msg);
                    // Handler
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if(_handler!=null){
            _handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
