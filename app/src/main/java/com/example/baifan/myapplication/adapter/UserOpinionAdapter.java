package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.model.UserOpinionInfo;

import java.util.List;

/**
 * Created by baifan on 2018/3/24.
 */

public class UserOpinionAdapter extends ArrayAdapter<UserOpinionInfo> {
    private int resourceId; // 资源号
    public UserOpinionAdapter(Context context, int resource, List<UserOpinionInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        UserOpinionInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        // 反馈用户
        TextView username = ( TextView )view.findViewById(R.id.username);
        // 反馈时间
        TextView feedbacktime =( TextView )view.findViewById(R.id.feedbacktime);

        username.setText(info.getUsername());
        feedbacktime.setText(info.getFeedbacktime());

        return view;
    }

}
