package com.example.baifan.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.*;

import com.example.baifan.myapplication.utils.ExitApplication;
import com.example.baifan.myapplication.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private boolean isback;
    private EditText username, userpassword;
    private Button btnlogin;
    private  CheckBox iv;
    private TextView reg;
    String act, pasd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);

        username = (EditText) findViewById(R.id.username);
        userpassword = (EditText) findViewById(R.id.userpassword);
        iv=(CheckBox)findViewById(R.id.iv_hide) ;   //显示、隐藏密码
        iv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    //如果选中，显示密码
                    userpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    userpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });
        reg=(TextView)findViewById(R.id.goto_reg_texta); //注册
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this, RegActivity.class);
                startActivity(intent);
            }
        });
        btnlogin = (Button)findViewById(R.id.signup_Btn);  //登陆
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act = username.getText().toString();
                pasd = userpassword.getText().toString();
                if (TextUtils.isEmpty(act) || TextUtils.isEmpty(pasd)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("您的账号/密码不能为空哦");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                } else
                    whetherRegister(act, pasd);
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
                        if (parserXml(response)) {
                            Toast.makeText(getApplicationContext(), "尊敬的用户，欢迎您常来!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            act = username.getText().toString();
                            intent.putExtra("account", act);
                            startActivity(intent);
                            finish();
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("This is a warnining!");
                            dialog.setMessage("对不起，您的账户或密码有错!");
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


    private void whetherRegister(String account2, String password2) {
        final String account = account2; // 进程中不能传入变量 一定要为常量final
        final String password = password2;

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {

                HttpURLConnection connection = null;
                try {
                    // 打开链接
                //    ip iip = new ip();

                    String account2 = URLEncoder.encode(account, "UTF-8"); // 中文转译！
                    URL url = new URL("http://111.231.101.251:8080/kehuduan/denglu.jsp?account=" + account2+ "&password=" + password);

                    connection = (HttpURLConnection) url.openConnection();

                    // 设置属性
                    connection.setRequestMethod("GET");
                    // Post 1)容量没有限制 2） 安全
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    // 读取数据
                    // 1)获取位流
                    InputStream in = connection.getInputStream();
                    // 二进制-->BufferedReader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    // 2) 读取
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // 发送消息
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = response.toString();
                    handler.sendMessage(msg);
                    // Handler
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect(); // 断开链接
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if(isback){
            isback=false;
            // 结束所有Activity
            ExitApplication.getInstance().exit();
        }else{
            Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            isback=true;
        }
    }
}
