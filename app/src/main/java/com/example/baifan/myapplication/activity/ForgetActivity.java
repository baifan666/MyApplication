package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.AES256Encryption;
import com.example.baifan.myapplication.utils.AddMessageUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import sun.misc.BASE64Encoder;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class ForgetActivity extends Activity {
    private ImageView back;
    private EditText et_username,et_phone;
    private Button btn_ok;
    private String username,mobile,result;
    private Dialog mDialog;
    private byte[] data,key;
    private BASE64Encoder base64Encoder;
    private final int SELECT_MOBILE = 1;
    private final int SET_PASSWORD = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        back = (ImageView) findViewById(R.id.IV_back); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_username = (EditText)findViewById(R.id.et_username);
        et_phone = (EditText)findViewById(R.id.et_phone);
        btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = DialogUtils.createLoadingDialog(ForgetActivity.this, "加载中...");
                if(TextUtils.isEmpty(et_phone.getText().toString()) || TextUtils.isEmpty(et_username.getText().toString())) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("您的账号/手机号码不能为空哦");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                    DialogUtils.closeDialog(mDialog);
                }else {
                    selectMoblie(et_username.getText().toString());
                }
            }
        });
    }

    private void selectMoblie(String username1) {
        final String username2 = username1;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/getUser.jsp?account=" + username2;
                Message msg = new Message();
                msg.what = SELECT_MOBILE;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void changePassword(String account1,String password1) {
        final String account2 = account1;
        final String password2 = password1;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/changePassword.jsp?account=" + account2+ "&newPassword=" + password2;
                Message msg = new Message();
                msg.what = 2;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private Handler handler;
    {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SELECT_MOBILE:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if(mobile.equals(et_phone.getText().toString())) {
                            try {
                                key = AES256Encryption.getKeyByPass();
                                // 加密
                                data = AES256Encryption.encrypt("123456".getBytes(), key);
                                base64Encoder = new BASE64Encoder();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            changePassword(et_username.getText().toString(),base64Encoder.encode(data));
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("对不起，手机号输入有误!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            DialogUtils.closeDialog(mDialog);
                            dialog.show();
                        }
                        break;
                    case SET_PASSWORD:
                        String response1 = (String) msg.obj;
                        parserXml(response1);
                        if(result.equals("succeessful")) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetActivity.this);
                            dialog.setTitle("成功");
                            dialog.setMessage("恭喜您，密码已重置为123456，请登录后及时修改密码");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent();
                                    intent.setClass(ForgetActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                            dialog.show();
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetActivity.this);
                            dialog.setTitle("错误");
                            dialog.setMessage("系统当前有故障，请稍后再试");
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
                    default:
                        break;
                }
            }
        };
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
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                        }
                        if ("mobile".equals(nodeName)) {
                            mobile = parse.nextText();
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
}
