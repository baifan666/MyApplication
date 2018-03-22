package com.example.baifan.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.utils.HttpUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends AppCompatActivity {

    private boolean isback;
    private EditText username, userpassword;
    private Button btnlogin;
    private  CheckBox iv;
    private TextView reg;
    String act, pasd;
    private final int GETTOKEN = 2;
    private String usertoken,result;
    private CheckBox rememberPass;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        rememberPass = (CheckBox)findViewById(R.id.cb_passworda);
        pref= PreferenceManager.getDefaultSharedPreferences(this);

        username = (EditText) findViewById(R.id.username);
        userpassword = (EditText) findViewById(R.id.userpassword);
        boolean isRemenber=pref.getBoolean("remember_password",false);
        if(isRemenber){
            //将账号和密码都设置到文本中
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            username.setText(account);
            userpassword.setText(password);
            rememberPass.setChecked(true);
        }
        iv = (CheckBox)findViewById(R.id.iv_hide) ;   //显示、隐藏密码
        iv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                        parserXml(response);
                        if(result.equals("succeessful")) {
                           // Toast.makeText(MainActivity.this, usertoken, Toast.LENGTH_SHORT).show();
                            RongIM.connect(usertoken, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    Log.e("MainActivity", "--onTokenIncorrect");
                                }

                                @Override
                                public void onSuccess(String userid) {
                                    editor=pref.edit();
                                    if(rememberPass.isChecked()){
                                        editor.putBoolean("remember_password",true);
                                        editor.putString("account",username.getText().toString());
                                        editor.putString("password",userpassword.getText().toString());
                                    }else {
                                        editor.clear();
                                    }
                                    editor.apply();
                                    Log.d("MainActivity", "--onSuccess--" + userid);
                                    Toast.makeText(MainActivity.this, "登录成功,用户：" + userid, Toast.LENGTH_SHORT).show();
                                    //服务器连接成功，跳转
                                    RongIM.getInstance().setCurrentUserInfo(new UserInfo(userid,userid, Uri.parse("http://111.231.101.251:8080/fuwuduan/HeadPortrait/boy.png")));
                                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                                    act = username.getText().toString();
                                    intent.putExtra("account", act);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Toast.makeText(MainActivity.this, errorCode.toString(), Toast.LENGTH_SHORT).show();
                                    Log.e("MainActivity", "--onError");
                                }
                            });
                        }
                        else {
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

    private void whetherRegister(String account2, String password2) {
        final String account = account2; // 进程中不能传入变量 一定要为常量final
        final String password = password2;

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = "http://111.231.101.251:8080/fuwuduan/dengLu.jsp?account=" + account+ "&password=" + password;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
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
                        if ("token".equals(nodeName)) {
                            String tokenStr = parse.nextText();
                            usertoken = tokenStr;
                            Log.d("token", usertoken);
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
