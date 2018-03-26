package com.example.baifan.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.model.PrizeInfo;

import java.util.List;

import static com.example.baifan.myapplication.utils.ServerAddress.SERVER_ADDRESS;

/**
 * Created by baifan on 2018/3/26.
 */

public class PrizeAdapter extends ArrayAdapter<PrizeInfo>{
    private int resourceId; // 资源号
    private Context context;
    private String path,url;
    private List<PrizeInfo> objects;
    public PrizeAdapter(Context context, int resource, List<PrizeInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.context = context;
        this.objects = objects;
    }
    //获取当前items项的大小，也可以看成是数据源的大小
    @Override
    public int getCount() {
        return objects.size();
    }

    // getView设置列表的值
    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        // position: 表示列表的第几个
        PrizeInfo info = objects.get(position);
        // 获取视图
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        }
        else{
            view = convertView;
        }
        path = info.getPictureurl().substring(info.getPictureurl().lastIndexOf("/")+1);
        url = SERVER_ADDRESS+"/prize/"+path;
        //奖品图片
        ImageView img = (ImageView)view.findViewById(R.id.img);
        // 奖品名称
        TextView prizename = ( TextView )view.findViewById(R.id.prizename);
        // 金币数
        TextView prizecoins =( TextView )view.findViewById(R.id.prizecoins);
        // 数量
        TextView number = ( TextView )view.findViewById(R.id.number);

       Glide.with(context).load(url).placeholder(R.drawable.jiazaizhong)//图片加载出来前，显示的图片
               .listener( requestListener )
               .error(R.drawable.error)//图片加载失败后，显示的图片
               .into(img);
        prizename.setText(info.getPrizename());
        prizecoins.setText(String.valueOf(info.getPrizecoins()));
        number.setText("剩余可兑换数量"+String.valueOf(info.getNumber())+"个");
        Log.d("resultStr", info.getPictureurl()+info.getPrizename());
        return view;
    }
    //设置错误监听
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            if(e.toString().contains("java.net.SocketTimeoutException")) {
                Toast.makeText(context,"当前网络异常，请稍后点击图片重新加载",Toast.LENGTH_LONG).show();
            }
            // important to return false so the error placeholder can be placed
            return false;
        }
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            return false;
        }
    };
}
