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

import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.R;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class MainActivity extends AppCompatActivity {

    private boolean isback;
    private EditText username, userpassword;
    private Button btnlogin;
    private ImageView qq_login;
    private  CheckBox iv;
    private TextView reg;
    private String act, pasd;
    private String usertoken,result,tuichu,openidString,uname,upassword,headurl,urlpath;   //urlpath存储QQ登陆获取到的头像连接，headurl存储服务器读出的头像连接
    private int flag;
    private CheckBox rememberPass,autologin;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Dialog mDialog;
    private Tencent mTencent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
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

        username = (EditText) findViewById(R.id.username);
        userpassword = (EditText) findViewById(R.id.userpassword);
        boolean isRemenber = pref.getBoolean("remember_password",false);
        boolean isCheck = pref.getBoolean("autologin",false);
        if("0".equals(tuichu)) {
            isCheck = false;
        }
        if(isRemenber){
            //将账号和密码都设置到文本中
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            flag = pref.getInt("flag",0);
            headurl = pref.getString("headurl","");
            username.setText(account);
            userpassword.setText(password);
            rememberPass.setChecked(true);
            if(isCheck) {
                autologin.setChecked(true);
                mDialog = DialogUtils.createLoadingDialog(MainActivity.this, "登陆中...");
                whetherRegister(account, password);
            }
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
                    whetherRegister(act, pasd);
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
    }

    private class BaseUiListener implements IUiListener {
        //这个类需要实现三个方法 onComplete（）：登录成功需要做的操作写在这里
        // onError onCancel
        public void onComplete(Object response) {
            //Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.v("----TAG--", "-------------" + response.toString());
                openidString = ((JSONObject) response).getString("openid");
                //Toast.makeText(MainActivity.this, openidString, Toast.LENGTH_SHORT).show();
                mTencent.setOpenId(openidString);
                //saveUser("44", "text", "text", 1);
                mTencent.setAccessToken(((JSONObject) response).getString("access_token"), ((JSONObject) response).getString("expires_in"));
                Log.v("TAG", "-------------" + openidString);
                //access_token= ((JSONObject) response).getString("access_token");
                //expires_in = ((JSONObject) response).getString("expires_in");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /**到此已经获得OpneID以及其他你想获得的内容了
             QQ登录成功了
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，可以通过这个类拿到这些信息
             如何得到这个UserInfo类呢？  获取详细信息的UserInfo ，返回的信息参看下面地址：
             http://wiki.open.qq.com/wiki/%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF#1._Tencent.E7.B1.BB.E7.9A.84request.E6.88.96requestAsync.E6.8E.A5.E5.8F.A3.E7.AE.80.E4.BB.8B
             */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    //用户信息获取到了
                    try {
                        Log.v("用户名", ((JSONObject) o).getString("nickname"));
                        Log.v("用户性别", ((JSONObject) o).getString("gender"));
                        Log.v("UserInfo",o.toString());
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
                                    if(rememberPass.isChecked()){
                                        editor.putBoolean("remember_password",true);
                                        editor.putString("account",username.getText().toString());
                                        editor.putString("password",userpassword.getText().toString());
                                        editor.putString("usertoken",usertoken);
                                        editor.putString("headurl",headurl);
                                        editor.putInt("flag",flag);
                                        if(autologin.isChecked()) {
                                            editor.putBoolean("autologin",true);
                                        }
                                    }else {
                                        editor.clear();
                                    }
                                    editor.apply();
                                    Log.d("MainActivity", "--onSuccess--" + userid);
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
                                    } else {
                                        Toast.makeText(MainActivity.this, "管理员，您好！" , Toast.LENGTH_SHORT).show();
                                        //服务器连接成功，跳转
                                        RongIM.getInstance().setCurrentUserInfo(new io.rong.imlib.model.UserInfo(userid, userid, Uri.parse(headurl)));
                                        Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
                                        act = username.getText().toString();
                                        intent.putExtra("account", act);
                                        intent.putExtra("headurl",headurl);
                                        startActivity(intent);
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
                String url = SERVER_ADDRESS+"/dengLu.jsp?account=" + account+ "&password=" + password;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
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
}
