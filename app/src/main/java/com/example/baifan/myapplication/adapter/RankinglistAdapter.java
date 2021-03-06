package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.UserSignInfo;

import java.util.List;

/**
 * Created by baifan on 2018/3/31.
 */

public class RankinglistAdapter extends ArrayAdapter<UserSignInfo> {
    private int resourceId; // 资源号
    public RankinglistAdapter(Context context, int resource, List<UserSignInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        UserSignInfo info = getItem(position);
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
        // 连续签到天数
        TextView signcount =( TextView )view.findViewById(R.id.signcount);

        username.setText(info.getUsername());
        signcount.setText(String.valueOf(info.getSigncount()));

        return view;
    }
}
