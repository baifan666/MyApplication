package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.NoticeInfo;

import java.util.List;

/**
 * Created by baifan on 2018/4/11.
 */

public class NoticeAdapter extends ArrayAdapter<NoticeInfo> {
    private int resourceId; // 资源号
    public NoticeAdapter(Context context, int resource, List<NoticeInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        NoticeInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        //时间
        TextView time = ( TextView )view.findViewById(R.id.time);
        //标题
        TextView title = ( TextView )view.findViewById(R.id.title);

        time.setText(info.getNoticeTime());
        title.setText(info.getNoticeTitle());

        return view;
    }
}
