package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.CommentInfo;
import com.example.baifan.myapplication.model.GoodsInfo;
import com.example.baifan.myapplication.utils.TimeUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by baifan on 2018/4/17.
 */

public class CommentAdapter extends ArrayAdapter<CommentInfo> {
    private int resourceId; // 资源号
    private Context context;
    private String user;
    public CommentAdapter(Context context, int resource, List<CommentInfo> objects, String user) {
        super(context, resource, objects);
        resourceId = resource;
        this.context = context;
        this.user = user;
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        final CommentInfo info = getItem(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        // 头像
        CircleImageView head = ( CircleImageView )view.findViewById(R.id.head);
        // 评论人
        TextView username =( TextView )view.findViewById(R.id.username);
        // 回复人，没有为空
        TextView tousername = ( TextView )view.findViewById(R.id.tousername);
        //回复文字，有回复人就显示，没有就不显示
        TextView tv_huifu = (TextView)view.findViewById(R.id.tv_huifu);
        //位置
        TextView local = (TextView)view.findViewById(R.id.local);
        //评论内容
        TextView content = (TextView)view.findViewById(R.id.content);
        //评论时间
        TextView commenttime = (TextView)view.findViewById(R.id.commenttime);
        //删除
        ImageView delete = (ImageView)view.findViewById(R.id.delete);
        if ((info.getUsername().toString()).equals(user)) {
            delete.setImageResource(R.drawable.delete);
        }
        Glide.with(context).load(info.getHeadurl()).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
                .error(R.drawable.error)//图片加载失败后，显示的图片
                .into(head);
        username.setText(info.getUsername().toString());
        if (TextUtils.isEmpty(info.getReplyed().toString())) {
            tousername.setText("");
            tv_huifu.setText("");
        }else {
            tousername.setText(info.getReplyed().toString());
            tv_huifu.setText("回复");
        }
        local.setText(info.getLocal().toString());
        content.setText(info.getContent().toString());
        commenttime.setText(info.getCommenttime());
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemDeleteListener.onDeleteClick(info.getCommentid().toString());
            }
        });

        return view;
    }

    /**
     * 删除按钮的监听接口
     */
    public interface onItemDeleteListener {
        void onDeleteClick(String commentid);
    }

    private onItemDeleteListener mOnItemDeleteListener;

    public void setOnItemDeleteClickListener(onItemDeleteListener mOnItemDeleteListener) {
        this.mOnItemDeleteListener = mOnItemDeleteListener;
    }
}
