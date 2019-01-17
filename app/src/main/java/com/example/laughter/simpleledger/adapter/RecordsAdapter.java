package com.example.laughter.simpleledger.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.model.DbRecord;

import java.util.List;

public class RecordsAdapter extends ArrayAdapter<DbRecord> {

    private int resourceId;

    public RecordsAdapter(Context context, int resourceId, List<DbRecord> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        DbRecord dbRecord = getItem(position);
        View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        if (dbRecord.getType() == 1){
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_group,null);
            viewHolder.date = (TextView) view.findViewById(R.id.text_date);
            viewHolder.total = (TextView)view.findViewById(R.id.text_total);
            viewHolder.date.setText(dbRecord.getDate());
            viewHolder.total.setText(String.format(getContext().getString(R.string.expense),
                    dbRecord.getMoney()));
        }
        else {
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder.icon = (ImageView)view.findViewById(R.id.img_kind);
            viewHolder.tab = (TextView)view.findViewById(R.id.text_tab);
            viewHolder.money = (TextView)view.findViewById(R.id.text_money);
            viewHolder.tab.setText(dbRecord.getRemark());
            if(dbRecord.getMoney() >= 0)
                viewHolder.icon.setImageResource(R.drawable.ic_money_blue);
            else
                viewHolder.icon.setImageResource(R.drawable.ic_money_red);
            viewHolder.money.setText(String.valueOf(dbRecord.getMoney()));
        }
        view.setTag(viewHolder);

        return view;
    }

    class ViewHolder{
        ImageView icon;
        TextView tab;
        TextView money;
        TextView date;
        TextView total;
    }
}
