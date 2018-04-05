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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.Adapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

public class AlterGoodsActivity extends Activity {
    private ImageView back;
    private EditText title,content,price,location,mobile;
    private ImageButton map;
    private List<String> s = new ArrayList<String>();//创建了s来保存本地图片的地址
    private List <String> ss = new ArrayList<String>();//创建了ss来保存服务器图片的地址
    private static String requestURL = SERVER_ADDRESS+"/UploadShipServlet";
    private List<Bitmap> data = new ArrayList<Bitmap>();
    private GridView mGridView;
    private String photoPath;
    private Adapter adapter;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alter_goods);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        Intent intent = getIntent();
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
                dialog(position);
                return true;
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
            //title.setText(photoPath);
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
