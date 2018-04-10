package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.MessageInfo;

import java.util.List;

/**
 * Created by baifan on 2018/4/10.
 */

public class MessageAdapter extends ArrayAdapter<MessageInfo> {
    private int resourceId; // 资源号
    public MessageAdapter(Context context, int resource, List<MessageInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        MessageInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        //消息时间
        TextView messagetime = ( TextView )view.findViewById(R.id.messagetime);
        //消息内容
        TextView content = ( TextView )view.findViewById(R.id.content);
        //消息是否已读
        TextView isread = (TextView)view.findViewById(R.id.isread);

        messagetime.setText(info.getMessagetime());
        content.setText(info.getContent());

        return view;
    }
}
