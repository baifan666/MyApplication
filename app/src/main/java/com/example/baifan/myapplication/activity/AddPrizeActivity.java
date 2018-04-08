package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.Adapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.BitmapUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.example.baifan.myapplication.utils.UploadUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.example.baifan.myapplication.common.ServerAddress.SERVER_ADDRESS;

public class AddPrizeActivity extends Activity {
    private ImageView back;
    private Button cancal,add;
    private EditText prizename,prizecoins,number;
    private String name,coins,num,path;
    private List<String> s = new ArrayList<String>();//创建了s来保存本地图片的地址
    private List <String> ss = new ArrayList<String>();//创建了ss来保存服务器图片的地址
    private static String requestURL = SERVER_ADDRESS+"/MangerUploadShipServlet";
    private List<Bitmap> data = new ArrayList<Bitmap>();
    private GridView mGridView;
    private String photoPath;
    private Adapter adapter;
    private Dialog mDialog;
    private final int ADD_PRIZE = 1;
    private String result = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_prize);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        prizename = (EditText)findViewById(R.id.prizename);
        prizecoins = (EditText)findViewById(R.id.prizecoins);
        number = (EditText)findViewById(R.id.number);
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cancal = (Button)findViewById(R.id.cancal);
        cancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 设置默认图片为加号
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
        data.add(bp);
        // 找到控件ID
        mGridView = (GridView)findViewById(R.id.gridView1);
        // 绑定Adapter
        adapter = new Adapter(getApplicationContext(), data, mGridView);
        mGridView.setAdapter((ListAdapter) adapter);
        // 设置点击监听事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data.size() == 2) {
                    Toast.makeText(AddPrizeActivity.this, "图片数1张已满", Toast.LENGTH_SHORT).show();
                } else {
                    if (position == data.size() - 1) {
                        //Toast.makeText(SearchActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                        // 选择图片
                        //Intent intent = new Intent(Intent.ACTION_PICK, null);
                        // intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        //startActivityForResult(intent, 0x1);

                        //使用startActivityForResult启动SelectPicPopupWindow当返回到此Activity的时候就会调用onActivityResult函数
                        startActivityForResult(new Intent(AddPrizeActivity.this,
                                SelectPicPopupWindow.class), 0x1);

                    } else {
                        Toast.makeText(AddPrizeActivity.this, "点击第" + (position + 1) + " 号图片", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // 设置长按事件
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < 1 ) {
                    dialog(position);
                }
                return true;
            }
        });

        add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = DialogUtils.createLoadingDialog(AddPrizeActivity.this, "新增中...");
                name = prizename.getText().toString();
                coins = prizecoins.getText().toString();
                num = number.getText().toString();
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(coins) || TextUtils.isEmpty(num) || s.size() ==0 )  {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddPrizeActivity.this);
                    dialog.setTitle("This is a warnining!");
                    dialog.setMessage("请确保每一个信息已输入！");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                    DialogUtils.closeDialog(mDialog);
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (String tmp : s) {
                                File file = new File(tmp);
                                if (file != null) {
                                    result = UploadUtil.uploadFile(file, requestURL);
                                    ss.add(result);
                                } else {
                                    result = "1111111";
                                }
                            }
                            HashSet h = new HashSet(ss);
                            ss.clear();
                            ss.addAll(h);
                            if (ss.size() == 1) {
                                path = ss.get(0);
                            } else {
                                path = "";
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addPrize(name,coins,num,path);
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    // 响应startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {

                ContentResolver resolver = getContentResolver();
                try {
                    Uri uri = data.getData();
                    if(uri !=null) {
                        // 这里开始的第二部分，获取图片的路径：
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                        // 按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        // 最后根据索引值获取图片路径
                        photoPath = cursor.getString(column_index);
                    }
                    else {
                        Bundle extras = data.getExtras(); //拍照没有uri
                        if (extras != null) {
                            //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                            Bitmap bitmap = (Bitmap) extras.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                            if (bitmap != null) {
                                Uri uri2=Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                                if(uri2 !=null) {
                                    // 这里开始的第二部分，获取图片的路径：
                                    String[] proj = {MediaStore.Images.Media.DATA};
                                    Cursor cursor = getContentResolver().query(uri2, proj, null, null, null);
                                    // 按我个人理解 这个是获得用户选择的图片的索引值
                                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                    cursor.moveToFirst();
                                    // 最后根据索引值获取图片路径
                                    photoPath = cursor.getString(column_index);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
         * Dialog对话框提示用户删除操作 position为删除图片位置
         */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddPrizeActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                data.remove(position);
                s.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void addPrize(String prizename1,String prizecoins1, String number1, String prizeurl1) {
        final String prizename2 = prizename1;
        final String prizecoins2= prizecoins1;
        final String number2 = number1;
        final String path2 = prizeurl1.replace("\\","/");

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String prizename3 = URLEncoder.encode(prizename2, "UTF-8");
                    String prizecoins3 = URLEncoder.encode(prizecoins2, "UTF-8");
                    String number3 = URLEncoder.encode(number2, "UTF-8");
                    String path3 = URLEncoder.encode(path2, "UTF-8");
                    String url = SERVER_ADDRESS+"/addPrize.jsp?prizename="+prizename3
                            + "&prizecoins=" + prizecoins3 + "&number=" + number3 + "&prizeurl=" + path3;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = ADD_PRIZE;
                    msg.obj = HttpUtils.connection(url).toString();
                    handler.sendMessage(msg);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_PRIZE:
                    String response = (String) msg.obj;
                    if (parserXml(response)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AddPrizeActivity.this);
                        dialog.setTitle("success");
                        dialog.setMessage("新增成功!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                // 物品新增成功后 跳转至界面
//                                Intent intent = new Intent(AlterGoodsActivity.this, SearchActivity.class);
//                                intent.putExtra("account", account);
//                                startActivity(intent);
                                finish();
                            }
                        });
                        DialogUtils.closeDialog(mDialog);
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AddPrizeActivity.this);
                        dialog.setTitle("This is a warnining!");
                        dialog.setMessage("对不起，系统出错了！");
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
                        }
                        // 简单的判断物品是否修改成功
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


    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(photoPath)) {
            s.add(photoPath);
            Bitmap newBp = BitmapUtils.decodeSampledBitmapFromFd(photoPath, 300, 300);
            data.remove(data.size() - 1);
            Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addpic);
            data.add(newBp);
            data.add(bp);
            //将路径设置为空，防止在手机休眠后返回Activity调用此方法时添加照片
            photoPath = null;
            adapter.notifyDataSetChanged();
        }
    }


}
