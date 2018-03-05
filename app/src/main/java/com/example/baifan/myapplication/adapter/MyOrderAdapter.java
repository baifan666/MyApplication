package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.OrderSpecificInfo;

import java.util.List;

/**
 * Created by baifan on 2018/3/4.
 */

public class MyOrderAdapter extends ArrayAdapter<OrderSpecificInfo> {
    private int resourceId; // 资源号
    public MyOrderAdapter(Context context, int resource, List<OrderSpecificInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        OrderSpecificInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        // 卖家
        TextView seller = ( TextView )view.findViewById(R.id.seller);
        // 购买时间
        TextView ordertime =( TextView )view.findViewById(R.id.ordertime);
        // 标题
        TextView title = ( TextView )view.findViewById(R.id.title);
        // 订单状态
        TextView isfinish = (TextView)view.findViewById(R.id.isfinish);
        //订单金额
        TextView money = (TextView)view.findViewById(R.id.money);
        seller.setText(info.getUsername());
        ordertime.setText(info.getOrdertime());
        title.setText(info.getTitle());
        money.setText(String.valueOf(info.getPrice()));
        if ("0".equals(info.getIsfinish())) {
            isfinish.setText("订单进行中");
        }else {
            isfinish.setText("订单已完成");
        }

        return view;
    }
}
