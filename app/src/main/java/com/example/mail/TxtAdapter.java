package com.example.mail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class TxtAdapter extends BaseAdapter {
    private LinkedList<TxtBox> mData;
    private Context mContext;

    public TxtAdapter(LinkedList<TxtBox> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inbox_item, parent, false);
            holder.t_ima = (TextView) convertView.findViewById(R.id.t_ima);
            holder.t_sender = (TextView)convertView.findViewById(R.id.t_sender);
            holder.t_context = (TextView)convertView.findViewById(R.id.t_content);
            // convertView设置Tag为ViewHolder
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.t_ima.setText(mData.get(position).getIcon());
        holder.t_sender.setText(mData.get(position).getSender());
        holder.t_context.setText(mData.get(position).getContent());
        return convertView;
    }

    private static class ViewHolder{
        TextView t_ima;
        TextView t_sender;
        TextView t_context;
    }
}
