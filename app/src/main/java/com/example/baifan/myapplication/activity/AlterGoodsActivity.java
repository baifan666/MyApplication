package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.Adapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.BitmapUtils;
import com.example.baifan.myapplication.utils.DialogUtils;
import com.example.baifan.myapplication.utils.HttpUtils;
import com.example.baifan.myapplication.utils.UploadUtil;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

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
import static com.youth.banner.BannerConfig.CENTER;
import static com.youth.banner.BannerConfig.CIRCLE_INDICATOR;

public class AlterGoodsActivity extends Activity {
    private ImageView back;
    private EditText title,content,price,location,mobile;
    private String path1,path2,tit,con,pri,loc,mob,url1,url2,goodsid;
    private String result = null;
    private ImageButton map;
    private List<String> s = new ArrayList<String>();//创建了s来保存本地图片的地址
    private List <String> ss = new ArrayList<String>();//创建了ss来保存服务器图片的地址
    private static String requestURL = SERVER_ADDRESS+"/UploadShipServlet";
    private List<Bitmap> data = new ArrayList<Bitmap>();
    private GridView mGridView;
    private String photoPath;
    private Adapter adapter;
    private Dialog mDialog,mDialog1,mDialog2;
    private Button send_btn;
    private final int UPDATE_SUCCEESS = 1;
    private Banner banner;
    private List<String> images= new ArrayList<String>();       //设置图片集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alter_goods);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        goodsid = intent.getStringExtra("goodsid");
        title = (EditText)findViewById(R.id.title);
        title.setText(intent.getStringExtra("title"));
        content = (EditText)findViewById(R.id.content);
        content.setText(intent.getStringExtra("content"));
        price = (EditText)findViewById(R.id.price);
        price.setText(intent.getStringExtra("price"));
        mobile = (EditText)findViewById(R.id.mobile);
        mobile.setText(intent.getStringExtra("mobile"));
        location = (EditText)findViewById(R.id.location);
        location.setText(intent.getStringExtra("location"));
        url1 = intent.getStringExtra("url1");
        url2 = intent.getStringExtra("url2");
        path1 = intent.getStringExtra("path1");
        path2 = intent.getStringExtra("path2");
        if(!"".equals(url1)) {
            mDialog1 = DialogUtils.createLoadingDialog(AlterGoodsActivity.this, "加载中...");
            images.add(url1);
        }
        if (!TextUtils.isEmpty(url2)) {
            mDialog2 = DialogUtils.createLoadingDialog(AlterGoodsActivity.this, "加载中...");
            images.add(url2);
        }
        back = (ImageView)findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        map = (ImageButton)findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AlterGoodsActivity.this,
                        MapActivity.class), 0x2);
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
                if (data.size() == 3) {
                    Toast.makeText(AlterGoodsActivity.this, "图片数2张已满", Toast.LENGTH_SHORT).show();
                } else {
                    if (position == data.size() - 1) {
                        //Toast.makeText(SearchActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                        // 选择图片
                        //Intent intent = new Intent(Intent.ACTION_PICK, null);
                        // intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        //startActivityForResult(intent, 0x1);

                        //使用startActivityForResult启动SelectPicPopupWindow当返回到此Activity的时候就会调用onActivityResult函数
                        startActivityForResult(new Intent(AlterGoodsActivity.this,
                                SelectPicPopupWindow.class), 0x1);

                    } else {
                        Toast.makeText(AlterGoodsActivity.this, "点击第" + (position + 1) + " 号图片", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // 设置长按事件
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < 2 ) {
                    dialog(position);
                }
                return true;
            }
        });

        banner = (Banner) findViewById(R.id.banner);
        //BannerConfig.NOT_INDICATOR	不显示指示器和标题	setBannerStyle
        //BannerConfig.CIRCLE_INDICATOR	显示圆形指示器	setBannerStyle
        //BannerConfig.NUM_INDICATOR	显示数字指示器	setBannerStyle
        //BannerConfig.NUM_INDICATOR_TITLE	显示数字指示器和标题	setBannerStyle
        //BannerConfig.CIRCLE_INDICATOR_TITLE	显示圆形指示器和标题（垂直显示）	setBannerStyle
        //BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE	显示圆形指示器和标题（水平显示）	setBannerStyle
        //BannerConfig.LEFT	指示器居左	setIndicatorGravity
        //BannerConfig.CENTER	指示器居中	setIndicatorGravity
        //BannerConfig.RIGHT	指示器居右	setIndicatorGravity
        banner.setBannerStyle(CIRCLE_INDICATOR);
        banner.setIndicatorGravity(CENTER);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //自定义图片加载框架
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                String p = String.valueOf(path);
                Glide.with(getApplicationContext()).load(p).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                        .listener( requestListener )
                        .error(R.drawable.error)//图片加载失败后，显示的图片
                        .into(imageView);
            }
        });
        //设置图片资源:url或本地资源
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(AlterGoodsActivity.this, PictureActivity.class);
                intent.putExtra("url",images.get(position)); // 向下一个界面传递信息
                startActivity(intent);
            }
        });



        send_btn = (Button)findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog = DialogUtils.createLoadingDialog(AlterGoodsActivity.this, "重新发布中...");
                tit = title.getText().toString();
                con = content.getText().toString();
                pri = price.getText().toString();
                mob = mobile.getText().toString();
                loc = location.getText().toString();
                if("".equals(tit)||"".equals(con)||"".equals(pri)||"".equals(mob)||"".equals(loc))  {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AlterGoodsActivity.this);
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
                                path1 = ss.get(0);
                                path2 = "";
                            } else if (ss.size() == 2) {
                                path1 = ss.get(0);
                                path2 = ss.get(1);
                            } else {
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateGoods(goodsid,tit, con, pri, mob, loc, path1, path2);
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
        }else if (requestCode == 0x2 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    //处理代码在此地
                    location.setText(bundle.getString("address"));// 得到子窗口ChildActivity的回传数据
//                    Toast.makeText(SearchActivity.this, bundle.getString("address"),
//                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /*
         * Dialog对话框提示用户删除操作 position为删除图片位置
         */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AlterGoodsActivity.this);
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

    private void updateGoods(String goodsid1,String title1, String content1, String price1, String mobile1, String location1, String path11, String path21) {
        final String goodsid2 = goodsid1;
        final String title2= title1;
        final String content2 = content1;
        final String price2 = price1;
        final String mobile2 = mobile1;
        final String location2 = location1;
        final String path12 = path11.replace("\\","/");
        final String path22 = path21.replace("\\","/");

        new Thread(new Runnable() { // 开启子线程
            @Override
            public void run() {
                try {
                    String goodsid3 = URLEncoder.encode(goodsid2, "UTF-8");
                    String title3 = URLEncoder.encode(title2, "UTF-8");
                    String content3 = URLEncoder.encode(content2, "UTF-8");
                    String price3 = URLEncoder.encode(price2, "UTF-8");
                    String mobile3 = URLEncoder.encode(mobile2, "UTF-8");
                    String location3 = URLEncoder.encode(location2, "UTF-8");
                    String path13 = URLEncoder.encode(path12, "UTF-8");
                    String path23 = URLEncoder.encode(path22, "UTF-8");
                    String url = SERVER_ADDRESS+"/updateGoods.jsp?goodsid="+goodsid3
                            + "&title=" + title3 + "&content=" + content3 + "&price=" + price3 + "&mobile=" + mobile3
                            + "&location=" + location3 + "&path1=" + path13 + "&path2=" + path23;
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
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterGoodsActivity.this);
                        dialog.setTitle("success");
                        dialog.setMessage("修改成功!");
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
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AlterGoodsActivity.this);
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
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(AlterGoodsActivity.this,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
            }
            DialogUtils.closeDialog(mDialog1);
            DialogUtils.closeDialog(mDialog2);
            // important to return false so the error placeholder can be placed
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            DialogUtils.closeDialog(mDialog1);
            DialogUtils.closeDialog(mDialog2);
            return false;
        }
    };

}
