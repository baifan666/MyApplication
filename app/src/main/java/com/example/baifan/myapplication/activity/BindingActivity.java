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
import android.widget.TextView;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.AES256Encryption;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sun.misc.BASE64Encoder;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class BindingActivity extends Activity {
    private ImageView back;
    private EditText username, userpassword;
    private String act, psd,openid,useropenid,result,headurl;
    private Button binding;
    private TextView reg;
    private Dialog mDialog;
    private final int GET_USEROPENID = 1;
    private final int ISRIGHT = 2;
    private final int SET_OPENID = 3;
    private byte[] data,key;
    private BASE64Encoder base64Encoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_binding);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
        openid = intent.getStringExtra("openid");
        headurl = intent.getStringExtra("headurl");
        username = (EditText)findViewById(R.id.username);
        userpassword = (EditText)findViewById(R.id.userpassword);
        back=(ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding = (Button)findViewById(R.id.binding);
        binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act = username.getText().toString();
                psd = userpassword.getText().toString();
                // 账户不能为空
                if (TextUtils.isEmpty(act) || TextUtils.isEmpty(psd)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(BindingActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("用户名或者密码不能为空!");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                }else {
                    mDialog = DialogUtils.createLoadingDialog(BindingActivity.this, "绑定中...");
                    getUserOpenid(act);
                }
            }
        });
        reg = (TextView)findViewById(R.id.goto_reg_texta);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BindingActivity.this, BindingRegActivity.class);
                intent.putExtra("openid", openid);
                intent.putExtra("headurl", headurl);
                startActivity(intent);
            }
        });
    }

    private void getUserOpenid(String username1) {
        final String id = username1;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/getUser.jsp?account=" + id;
                Message msg = new Message();
                msg.what = GET_USEROPENID;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void setOpenid(String username1,String openid1,String headurl1) {
        final String username2 = username1;
        final String openid2 = openid1;
        final String headurl2 = headurl1;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/setOpenid.jsp?username=" + username2 +
                        "&openid="+ openid2 +"&headurl="+ headurl2;
                Message msg = new Message();
                msg.what = SET_OPENID;
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
                msg.what = ISRIGHT;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler;
    {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_USEROPENID:
                        String response = (String) msg.obj;
                        parserXml(response);
                        try {
                            key = AES256Encryption.getKeyByPass();
                            // 加密
                            data = AES256Encryption.encrypt(psd.getBytes(), key);
                            base64Encoder = new BASE64Encoder();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ("0".equals(useropenid)) {
                            whetherRegister(act,base64Encoder.encode(data));
                        }else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(BindingActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("该用户已被其他qq号绑定！");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            dialog.show();
                            DialogUtils.closeDialog(mDialog);
                        }
                        break;
                    case ISRIGHT:
                        String response1 = (String) msg.obj;
                        parserXml(response1);
                        if("succeessful".equals(result)) {
                            setOpenid(act,openid,headurl);
                        }else {
                            DialogUtils.closeDialog(mDialog);
                            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(BindingActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("对不起，您输入的用户名或密码有错!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            dialog.show();
                        }
                        break;
                    case SET_OPENID:
                        String response2 = (String) msg.obj;
                        parserXml(response2);
                        if("succeessful".equals(result)) {
                            Toast.makeText(BindingActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                            DialogUtils.closeDialog(mDialog);
                            finish();
                        }else {
                            DialogUtils.closeDialog(mDialog);
                            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(BindingActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("当前网络不稳定，请稍后再试");
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
                        if ("openid".equals(nodeName)) {
                            String useropenidStr = parse.nextText();
                            useropenid = useropenidStr;
                            Log.d("openid", useropenid);
                        }
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

}
