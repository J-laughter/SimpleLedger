package com.example.laughter.simpleledger.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.model.DbRecord;

import java.util.List;

public class RecordsAdapter extends BaseAdapter {

    private Context mContext;
    private List<DbRecord> recordList;

    public RecordsAdapter(Context context, List<DbRecord> objects) {
        this.mContext = context;
        this.recordList = objects;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        DbRecord dbRecord = recordList.get(position);
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (dbRecord.getType() == 1){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group,null);
            viewHolder.date = (TextView) convertView.findViewById(R.id.text_date);
            viewHolder.total = (TextView)convertView.findViewById(R.id.text_total);
            viewHolder.date.setText(dbRecord.getDate());
            viewHolder.total.setText(String.format(mContext.getString(R.string.expense),
                    dbRecord.getMoney()));
        }
        else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent,false);
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.img_kind);
            viewHolder.tab = (TextView)convertView.findViewById(R.id.text_tab);
            viewHolder.money = (TextView)convertView.findViewById(R.id.text_money);
            viewHolder.tab.setText(dbRecord.getRemark());
            if(dbRecord.getMoney() >= 0) {
                viewHolder.icon.setImageResource(R.drawable.ic_money_blue);
            }
            else {
                viewHolder.icon.setImageResource(R.drawable.ic_money_red);
            }
            viewHolder.money.setText(String.valueOf(dbRecord.getMoney()));
        }
        convertView.setTag(viewHolder);

        return convertView;
    }

    class ViewHolder{
        ImageView icon;
        TextView tab;
        TextView money;
        TextView date;
        TextView total;
    }
}