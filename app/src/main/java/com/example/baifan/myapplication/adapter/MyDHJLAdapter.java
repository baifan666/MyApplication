package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.DHJLInfo;

import java.util.List;

/**
 * Created by baifan on 2018/4/2.
 */

public class MyDHJLAdapter extends ArrayAdapter<DHJLInfo> {
    private Context context;
    private int resourceId; // 资源号
    public MyDHJLAdapter(Context context, int resource, List<DHJLInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        DHJLInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        // 奖品名称
        TextView prizename = ( TextView )view.findViewById(R.id.prizename);
        //兑换时间
        TextView dhtime = (TextView)view.findViewById(R.id.dhtime);
        //地址
        TextView address = (TextView)view.findViewById(R.id.address);
        //姓名
        TextView name = (TextView)view.findViewById(R.id.name);
        //手机号
        TextView mobile =(TextView)view.findViewById(R.id.mobile);

        prizename.setText(info.getPrizename());
        dhtime.setText(info.getDhtime());
        address.setText(info.getAddress());
        name.setText(info.getName());
        mobile.setText(info.getMobile());

        return view;
    }

}
