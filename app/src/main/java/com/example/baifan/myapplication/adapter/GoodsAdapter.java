package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.utils.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by baifan on 2018/1/12.
 */

public class GoodsAdapter extends ArrayAdapter<GoodsInfo> {
    private int resourceId; // 资源号
    public GoodsAdapter(Context context, int resource, List<GoodsInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        GoodsInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        // 发布用户
        TextView username = ( TextView )view.findViewById(R.id.username);
        // 发布时间
        TextView publish_time =( TextView )view.findViewById(R.id.publish_time);
        // 标题
        TextView title = ( TextView )view.findViewById(R.id.title);

        username.setText(info.getUsername());
        publish_time.setText(TimeUtil.getTimeFormatText(info.getPublish_time()));
        title.setText(info.getTitle());

        return view;
    }

}
