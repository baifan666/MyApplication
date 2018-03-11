package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.example.baifan.myapplication.R;

import java.util.List;

/**
 * Created by baifan on 2018/3/11.
 */

public class PoiListAdapter extends ArrayAdapter<PoiInfo> {
    private int resourceId; // 资源号
    public PoiListAdapter(Context context, int resource, List<PoiInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        PoiInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        TextView tv_location = ( TextView )view.findViewById(R.id.tv_location);
        tv_location.setText(info.name);
        TextView tv_detail_location = ( TextView )view.findViewById(R.id.tv_detail_location);
        tv_detail_location.setText(info.address);
        return view;
    }


}
