package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class AccountManagementActivity extends Activity {
    private TextView exitapp,updatepassword,binding,isbinding;
    private String account,uname;
    private ImageView back;
    private Dialog mDialog;
    private String openid,openidString,result;
    private Tencent mTencent;
    private final int GET_OPENID = 1;
    private final int SELECT_OPENID = 2;
    private final int SET_OPENID = 3;
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
        isbinding = (TextView)findViewById(R.id.isbinding);
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
        mDialog = DialogUtils.createLoadingDialog(AccountManagementActivity.this, "加载中...");
        getOpenid(account);
        binding = (TextView)findViewById(R.id.binding);
        //QQ第三方登录
        mTencent = Tencent.createInstance("101466661",getApplicationContext());//将101466661为自己的AppID
        binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get_simple_userinfo
                mTencent.login(AccountManagementActivity.this,"all",new BaseUiListener());
            }
        });
    }

    private class BaseUiListener implements IUiListener {
        //这个类需要实现三个方法 onComplete（）：登录成功需要做的操作写在这里
        // onError onCancel
        public void onComplete(Object response) {
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.v("----TAG--", "-------------" + response.toString());
                openidString = ((JSONObject) response).getString("openid");
                //Toast.makeText(MainActivity.this, openidString, Toast.LENGTH_SHORT).show();
                mTencent.setOpenId(openidString);
                mTencent.setAccessToken(((JSONObject) response).getString("access_token"), ((JSONObject) response).getString("expires_in"));
                Log.v("TAG", "-------------" + openidString);
                //access_token= ((JSONObject) response).getString("access_token");
                //expires_in = ((JSONObject) response).getString("expires_in");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /**到此已经获得OpneID以及其他想获得的内容了
             QQ登录成功了
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，可以通过这个类拿到这些信息
             如何得到这个UserInfo类呢？  获取详细信息的UserInfo ，返回的信息参看下面地址：
             http://wiki.open.qq.com/wiki/%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF#1._Tencent.E7.B1.BB.E7.9A.84request.E6.88.96requestAsync.E6.8E.A5.E5.8F.A3.E7.AE.80.E4.BB.8B
             */
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);

            //    info.getUserInfo(new BaseUIListener(this,"get_simple_userinfo"));
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    //用户信息获取到了
                    try {
                        Log.v("用户名", ((JSONObject) o).getString("nickname"));
                        Log.v("用户姓名", ((JSONObject) o).getString("gender"));
                        Log.v("UserInfo",o.toString());
                        String path = ((JSONObject) o).getString("figureurl_qq_2");
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
            Toast.makeText(AccountManagementActivity.this, "onError", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(AccountManagementActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
        }


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
                intent.putExtra("exit","0"); // 向下一个界面传递信息
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

    private void getOpenid(String username) {
        final String id = username;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/getUser.jsp?account=" + id;
                Message msg = new Message();
                msg.what = GET_OPENID;
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
                msg.what = SELECT_OPENID;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }

    private void setOpenid(String username1,String openid1) {
        final String username2 = username1;
        final String openid2 = openid1;
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                String url = SERVER_ADDRESS+"/setOpenid.jsp?username=" + username2 +
                        "&openid="+ openid2;
                Message msg = new Message();
                msg.what = SET_OPENID;
                msg.obj = HttpUtils.connection(url).toString();
                handler.sendMessage(msg);
                // Handler
            }
        }).start();
    }
    private Handler handler;{
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_OPENID:
                        String response = (String) msg.obj;
                        parserXml(response);
                        if("0".equals(openid)) {
                            isbinding.setText("未绑定");
                            binding.setEnabled(true);
                        }else {
                            isbinding.setText("已绑定");
                            binding.setEnabled(false);
                        }
                        DialogUtils.closeDialog(mDialog);
                        break;
                    case SELECT_OPENID:
                        String response1 = (String) msg.obj;
                        parserXml(response1);
                        if(!"null".equals(uname)) {
                            Toast.makeText(AccountManagementActivity.this,"该qq号已被其他账户绑定！请切换qq号", Toast.LENGTH_SHORT).show();
                        }else {
                            mDialog = DialogUtils.createLoadingDialog(AccountManagementActivity.this, "绑定中...");
                            setOpenid(account,openidString);
                        }
                        break;
                    case SET_OPENID:
                        String response2 = (String) msg.obj;
                        parserXml(response2);
                        if("succeessful".equals(result)) {
                            Toast.makeText(AccountManagementActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                            DialogUtils.closeDialog(mDialog);
                            finish();
                        }else {
                            DialogUtils.closeDialog(mDialog);
                            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(AccountManagementActivity.this);
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
                            String openidStr = parse.nextText();
                            openid = openidStr;
                            Log.d("openid", openid);
                        }else if ("username".equals(nodeName)) {
                            String unameStr = parse.nextText();
                            uname = unameStr;
                            Log.d("uname", uname);
                        }else if ("result".equals(nodeName)) {
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
