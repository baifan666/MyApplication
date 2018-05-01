package com.example.baifan.myapplication.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
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

import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.utils.AES256Encryption;
import com.example.baifan.myapplication.utils.AddMessageUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class MainActivity extends AppCompatActivity {

    private boolean isback;
    private EditText username, userpassword;
    private Button btnlogin;
    private ImageView qq_login;
    private CheckBox iv;
    private TextView reg,forget;
    private String act, pasd;
    private String usertoken,result,tuichu,openidString,uname,upassword,headurl,urlpath;   //urlpath存储QQ登陆获取到的头像连接，headurl存储服务器读出的头像连接
    private int flag;
    private CheckBox rememberPass,autologin;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Dialog mDialog;
    private Tencent mTencent;
    private byte[] data,key;
    private BASE64Encoder base64Encoder;
    private int isQQLogin = 0;  //是否qq登陆，标记，qq登陆置为1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public io.rong.imlib.model.UserInfo getUserInfo(String arg0) {
                io.rong.imlib.model.UserInfo customer = new io.rong.imlib.model.UserInfo("SYSTEM", "系统消息", Uri.parse(SERVER_ADDRESS+"/HeadPortrait/system.png"));
                return customer;
            }
        }, true);
        Intent intent = getIntent();
        tuichu = intent.getStringExtra("exit");

        rememberPass = (CheckBox)findViewById(R.id.cb_passworda);
        autologin = (CheckBox)findViewById(R.id.cb_logina);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    rememberPass.setChecked(true);
                }
            }
        });
        username = (EditText) findViewById(R.id.username);
        userpassword = (EditText) findViewById(R.id.userpassword);
        boolean isRemenber = pref.getBoolean("remember_password",false);
        editor=pref.edit();
        if("0".equals(tuichu)) {
            editor.putBoolean("autologin",false);
            autologin.setChecked(false);
            editor.apply();
        }
        if(isRemenber){
            //将账号和密码都设置到文本中
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            flag = pref.getInt("flag",0);
            headurl = pref.getString("headurl","");
            username.setText(account);
            try {
                key = AES256Encryption.getKeyByPass();
                BASE64Decoder base64decoder = new BASE64Decoder();
                data = base64decoder.decodeBuffer(password);
                data = AES256Encryption.decrypt(data, key);// 调用解密方法
                userpassword.setText(new String(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                } else {
                    mDialog = DialogUtils.createLoadingDialog(MainActivity.this, "登陆中...");
                    try {
                        key = AES256Encryption.getKeyByPass();
                        // 加密
                        data = AES256Encryption.encrypt(pasd.getBytes(), key);
                        base64Encoder = new BASE64Encoder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    whetherRegister(act, base64Encoder.encode(data));
                }
            }
        });

        //QQ第三方登录
        mTencent = Tencent.createInstance("101466661",getApplicationContext());//将101466661为自己的AppID
        qq_login = (ImageView)findViewById(R.id.qq_login);
        qq_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get_simple_userinfo
                mTencent.login(MainActivity.this,"all",new BaseUiListener());
            }
        });

        forget = (TextView)findViewById(R.id.forget);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });
    }

    private class BaseUiListener implements IUiListener {
        public void onComplete(Object response) {
            try {
                //获得的数据是JSON格式
                openidString = ((JSONObject) response).getString("openid");
                mTencent.setOpenId(openidString);
                mTencent.setAccessToken(((JSONObject) response).getString("access_token"), ((JSONObject) response).getString("expires_in"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /**到此已经获得OpneID以及其他你想获得的内容了
             QQ登录成功了
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，可以通过这个类拿到这些信息
             */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    //用户信息获取到了
                    try {
                        urlpath = ((JSONObject) o).getString("figureurl_qq_2");
                        selectOpenid(openidString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(UiError uiError) {
                    Log.v("UserInfo","onError");
                }
                @Override
                public void onCancel() {
                    Log.v("UserInfo","onCancel");
                }
            });
        }
        @Override
        public void onError(UiError uiError) {
            Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, "取消登陆", Toast.LENGTH_SHORT).show();
        }
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
                            RongIM.connect(usertoken, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    Log.e("MainActivity", "--onTokenIncorrect");
                                }

                                @Override
                                public void onSuccess(String userid) {
                                    DialogUtils.closeDialog(mDialog);
                                    editor=pref.edit();
                                    if(isQQLogin == 0 ) {
                                        if (rememberPass.isChecked()) {
                                            editor.putBoolean("remember_password", true);
                                            editor.putString("account", username.getText().toString());
                                            if (TextUtils.isEmpty(uname) || "null".equals(uname)) {   //uname为空说明是直接用账号名密码登陆
                                                editor.putString("password", base64Encoder.encode(data));
                                            } else {
                                                editor.putString("password", upassword);
                                            }
                                            editor.putString("usertoken", usertoken);
                                            editor.putString("headurl", headurl);
                                            editor.putInt("flag", flag);
                                            if (autologin.isChecked()) {
                                                editor.putBoolean("autologin", true);
                                            } else {
                                                editor.putBoolean("autologin", false);
                                            }
                                        } else {
                                            editor.clear();
                                        }
                                        editor.apply();
                                    }else if (isQQLogin == 1) {
                                        if (rememberPass.isChecked()) {
                                            editor.putBoolean("remember_password", true);
                                            editor.putString("account", uname);
                                            editor.putString("password", upassword);
                                            editor.putString("usertoken", usertoken);
                                            editor.putString("headurl", headurl);
                                            editor.putInt("flag", flag);
                                            if (autologin.isChecked()) {
                                                editor.putBoolean("autologin", true);
                                            } else {
                                                editor.putBoolean("autologin", false);
                                            }
                                            editor.apply();
                                        } else {
                                            editor.clear();
                                        }
                                    }
                                    if(0 == flag) {
                                        Toast.makeText(MainActivity.this, "登录成功,用户：" + userid, Toast.LENGTH_SHORT).show();
                                        RongIM.getInstance().setCurrentUserInfo(new io.rong.imlib.model.UserInfo(userid, userid, Uri.parse(headurl)));
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                                        //获取当前时间
                                        Date date = new Date(System.currentTimeMillis());
                                        String str = simpleDateFormat.format(date)+"在"+ Build.MODEL +"登陆成功";
                                        AddMessageUtils.addMessage(userid,str);
                                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                                        if(TextUtils.isEmpty(uname) || "null".equals(uname)) {   //uname为空说明是直接用账号名密码登陆
                                            act = username.getText().toString();
                                        } else {
                                            act = uname;
                                        }
                                        intent.putExtra("account", act);
                                        intent.putExtra("headurl",headurl);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "管理员，您好！" , Toast.LENGTH_SHORT).show();
                                        //服务器连接成功，跳转
                                        RongIM.getInstance().setCurrentUserInfo(new io.rong.imlib.model.UserInfo(userid, userid, Uri.parse(headurl)));
                                        Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
                                        act = username.getText().toString();
                                        intent.putExtra("account", act);
                                        intent.putExtra("headurl",headurl);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    Toast.makeText(MainActivity.this, errorCode.toString(), Toast.LENGTH_SHORT).show();
                                    Log.e("MainActivity", "--onError");
                                }
                            });
                        }
                        else {
                            DialogUtils.closeDialog(mDialog);
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
                            isQQLogin = 0;
                        }
                        result = "";
                        break;
                    case 2:
                        String response1 = (String) msg.obj;
                        parserXml(response1);
                        if("null".equals(uname)) {
                            Toast.makeText(MainActivity.this,"该qq号还未绑定平台账户，正在跳转绑定页面···", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, BindingActivity.class);
                            intent.putExtra("openid", openidString);
                            intent.putExtra("headurl", urlpath);
                            startActivity(intent);
                        }else {
                            mDialog = DialogUtils.createLoadingDialog(MainActivity.this, "登陆中...");
                            isQQLogin = 1;
                            whetherRegister(uname, upassword);
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
                try {
                    String account1 = URLEncoder.encode(account, "UTF-8");
                    String password1 = URLEncoder.encode(password, "UTF-8");
                    String url = SERVER_ADDRESS + "/dengLu.jsp?account=" + account1 + "&password=" + password1;
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void selectOpenid(String openid) {
        final String id = openid;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/selectOpenid.jsp?openid=" + id;
                Message msg = new Message();
                msg.what = 2;
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
            App.getInstance().exit();
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
                        if ("flag".equals(nodeName)) {
                            String flagStr = parse.nextText();
                            flag = Integer.parseInt(flagStr);
                        }
                        if ("token".equals(nodeName)) {
                            String tokenStr = parse.nextText();
                            usertoken = tokenStr;
                        }
                        if ("result".equals(nodeName)) {
                            result = parse.nextText();
                        }
                        if ("username".equals(nodeName)) {
                            String unameStr = parse.nextText();
                            uname = unameStr;
                        }
                        if ("userpassword".equals(nodeName)) {
                            String upasswordStr = parse.nextText();
                            upassword = upasswordStr;
                        }
                        if ("headurl".equals(nodeName)) {
                        String headurlStr = parse.nextText();
                        headurl = headurlStr;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
        if(requestCode == Constants.REQUEST_API) {
            if(resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
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
