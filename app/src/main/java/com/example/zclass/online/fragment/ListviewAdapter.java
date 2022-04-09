package com.example.zclass.online.fragment;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.zclass.R;
import com.example.zclass.online.service.HttpClientUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ListviewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    public ArrayList<HashMap<String, Object>> listItem;
    //public int[] color={R.color.dark_blue,R.color.dark_green,R.color.dark_purple,R.color.light_blue,R.color.light_gray,R.color.light_green,R.color.light_purple,R.color.light_yellow};

    public ListviewAdapter(Context context, ArrayList<HashMap<String, Object>> listItem) {
        this.mInflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }//声明构造函数

    @Override
    public int getCount() {
        return listItem.size();
    }//这个方法返回了在适配器中所代表的数据集合的条目数

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }//这个方法返回了数据集合中与指定索引position对应的数据项

    @Override
    public long getItemId(int position) {
        return position;
    }//这个方法返回了在列表中与指定索引对应的行id

    //利用convertView+ViewHolder来重写getView()
    static class ViewHolder
    {
        public ImageView img_bottom;
        public TextView title;
        public TextView itemid;
        public TextView text_left;
        public TextView text_right;
        public ImageView img_up;
        public View  item;
    }//声明一个外部静态类
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder ;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item, null);
            holder.itemid=convertView.findViewById(R.id.item_id);
            holder.img_bottom = (ImageView)convertView.findViewById(R.id.item_icon);
            holder.title = (TextView)convertView.findViewById(R.id.item_title);
            holder.text_left = (TextView)convertView.findViewById(R.id.item_bottom_left);
            holder.text_right = (TextView)convertView.findViewById(R.id.item_bottom_right);
            holder.img_up = (ImageView)convertView.findViewById(R.id.item_up_right);
            holder.item=convertView.findViewById(R.id.item);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();

        }
        //holder.img.setImageResource((Integer) listItem.get(position).get("ItemImage"));
        holder.title.setText((String) listItem.get(position).get("cou_on_name"));
        holder.text_left.setText((String) listItem.get(position).get("tea_name"));
        holder.text_right.setText((String) listItem.get(position).get("cou_grade")+"-"+listItem.get(position).get("cou_class"));
        holder.itemid.setText(listItem.get(position).get("cou_on_id").toString());
        //holder.item.setBackgroundColor(color[position]);

        return convertView;
    }//这个方法返回了指定索引对应的数据项的视图
}