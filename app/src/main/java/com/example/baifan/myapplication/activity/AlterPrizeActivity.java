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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.Adapter;
import com.example.baifan.myapplication.application.App;
import com.example.baifan.myapplication.model.PrizeInfo;
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

public class AlterPrizeActivity extends Activity {
    private ImageView back,oldImage;
    private Button cancal,alter;
    private EditText prizename,prizecoins,number;
    private String name,coins,num,path,prizeid,prizeurl,prizeurl1,prizeurl2;
    private List<String> s = new ArrayList<String>();//创建了s来保存本地图片的地址
    private List <String> ss = new ArrayList<String>();//创建了ss来保存服务器图片的地址
    private static String requestURL = SERVER_ADDRESS+"/MangerUploadShipServlet";
    private List<Bitmap> data = new ArrayList<Bitmap>();
    private GridView mGridView;
    private String photoPath;
    private Adapter adapter;
    private Dialog mDialog,mDialog1;
    private final int UPDATE_SUCCEESS = 1;
    private String result = null;
    private PrizeInfo prizeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alter_prize);
        //将该Activity添加到App实例中，
        App.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        prizeInfo = (PrizeInfo) intent.getSerializableExtra("prizeInfo");
        prizeurl = prizeInfo.getPictureurl();
        prizeurl1 = prizeurl.substring(prizeurl.lastIndexOf("/")+1);
        prizeurl2 = SERVER_ADDRESS+"/prize/"+prizeurl1;
        prizeid = prizeInfo.getPrizeid();
        prizename = (EditText)findViewById(R.id.prizename);
        prizename.setText(prizeInfo.getPrizename());
        prizecoins = (EditText)findViewById(R.id.prizecoins);
        prizecoins.setText(String.valueOf(prizeInfo.getPrizecoins()));
        number = (EditText)findViewById(R.id.number);
        number.setText(String.valueOf(prizeInfo.getNumber()));
        oldImage = (ImageView)findViewById(R.id.oldImage);
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mDialog1 = DialogUtils.createLoadingDialog(AlterPrizeActivity.this, "加载中...");
        Glide.with(getApplicationContext()).load(prizeurl2).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                .listener( requestListener )
                .error(R.drawable.error)//图片加载失败后，显示的图片
                .into(oldImage);
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
                    Toast.makeText(AlterPrizeActivity.this, "图片数1张已满", Toast.LENGTH_SHORT).show();
                } else {
                    if (position == data.size() - 1) {
                        //Toast.makeText(SearchActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                        // 选择图片
                        //Intent intent = new Intent(Intent.ACTION_PICK, null);
                        // intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        //startActivityForResult(intent, 0x1);

                        //使用startActivityForResult启动SelectPicPopupWindow当返回到此Activity的时候就会调用onActivityResult函数
                        startActivityForResult(new Intent(AlterPrizeActivity.this,
                                SelectPicPopupWindow.class), 0x1);

                    } else {
                        Toast.makeText(AlterPrizeActivity.this, "点击第" + (position + 1) + " 号图片", Toast.LENGTH_SHORT).show();
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

        alter = (Button)findViewById(R.id.alter);
        alter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = DialogUtils.createLoadingDialog(AlterPrizeActivity.this, "修改中...");
                name = prizename.getText().toString();
                coins = prizecoins.getText().toString();
                num = number.getText().toString();
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(coins) || TextUtils.isEmpty(num) || s.size() ==0 )  {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AlterPrizeActivity.this);
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
                                    updatePrize(prizeid,name,Integer.parseInt(coins),Integer.parseInt(num),path);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AlterPrizeActivity.this);
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

    private void updatePrize(String prizeid1,String prizename1, int prizecoins1, int number1, String prizeurl) {
        final String prizeid2 = prizeid1;
        final String prizename2= prizename1;
        final int prizecoins2 = prizecoins1;
        final int number2 = number1;
        final String prizeur2 = prizeurl.replace("\\","/");
        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String prizeid3 = URLEncoder.encode(prizeid2, "UTF-8");
                    String prizename3 = URLEncoder.encode(prizename2, "UTF-8");
                    String prizeur3 = URLEncoder.encode(prizeur2, "UTF-8");
                    String url = SERVER_ADDRESS+"/updatePrize.jsp?prizeid="+prizeid3
                            + "&prizename=" + prizename3 + "&prizecoins=" + prizecoins2 + "&number=" + number2 + "&prizeurl=" + prizeur3;
                    // 发送消息
                    Message msg = new Message();
                    msg.what = UPDATE_SUCCEESS;
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
                case UPDATE_SUCCEESS:
                    String response = (String) msg.obj;
                    if (parserXml(response)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterPrizeActivity.this);
                        dialog.setTitle("success");
                        dialog.setMessage("修改成功!");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        DialogUtils.closeDialog(mDialog);
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterPrizeActivity.this);
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

    //设置错误监听
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog1);
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog1);
            return false;
        }
    };

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

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
