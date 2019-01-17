package com.example.laughter.simpleledger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.adapter.RecordsAdapter;
import com.example.laughter.simpleledger.model.BmobRecord;
import com.example.laughter.simpleledger.model.DbRecord;
import com.example.laughter.simpleledger.util.PermissionUtil;
import com.example.laughter.simpleledger.util.SpUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends PermissionUtil {

    @BindView(R.id.list_view_main) ListView mListView;
    @BindView(R.id.text_blank) TextView tvBlank;
    @BindView(R.id.fab_add) FloatingActionButton fabAdd;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private ProgressDialog progressDialog;
    private List<DbRecord> recordList = new ArrayList<>();
    private RecordsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Bmob默认初始化
        Bmob.initialize(this, "b41e662d256ba141c87f5c04beb23f35");

        if (isLogin()){
            setToolbar();
            init();
            adapter = new RecordsAdapter(this, R.layout.item_record, recordList);
            mListView.setAdapter(adapter);
        }else {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryDateFromDatebase();
    }

    private void setToolbar(){
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(onMenuItemClick);
    }

    private void init(){
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AddActivity.class);
            startActivity(intent);
        });

        mListView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
            intent.putExtra("record", recordList.get(position));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                default:
                    break;
                case R.id.about_developer:
                    Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.log_off:
                    SpUtil.putBoolean(getApplicationContext(), "idLogin",false);

                    LitePal.deleteAll(DbRecord.class);
                    Intent intent3 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent3);
                    finish();
                    break;
            }
            return true;
        }
    };

    private boolean isLogin(){
        return SpUtil.getBoolean(getApplicationContext(), "isLogin",false);
    }

    private void queryDateFromDatebase(){
        List<DbRecord> list = LitePal.order("date desc").find(DbRecord.class);
        if (list.size() > 0){
            mListView.setVisibility(View.VISIBLE);
            tvBlank.setVisibility(View.GONE);
            groupByDbDate(list);
            adapter.notifyDataSetChanged();
        }else {
            queryDateFromBmob();
        }
    }

    private void groupByDbDate(List<DbRecord> list){
        recordList.clear();
        String date = null;
        int index = 0;
        float sum = 0f;
        for (int i = 0; i<list.size(); i++){
            if (! list.get(i).getDate().equals(date)){
                date = list.get(i).getDate();
                DbRecord group = new DbRecord();
                group.setDate(date);
                group.setType(1);
                recordList.add(group);
                index = recordList.indexOf(group);
                sum = 0f;
            }
            recordList.add(list.get(i));
            sum += list.get(i).getMoney();
            recordList.get(index).setMoney(sum);
        }
    }

    private void queryDateFromBmob(){
        showProgressDialog();
        BmobQuery<BmobRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(BmobUser.getCurrentUser()));
        query.order("-date");
        query.findObjects(new FindListener<BmobRecord>() {
            @Override
            public void done(final List<BmobRecord> records, BmobException e) {
                if (e == null) {
                    groupByBmobDate(records);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (records.size()==0){
                                tvBlank.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                            closeProgressDialog();
                        }
                    });
                }else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void groupByBmobDate(List<BmobRecord> list){
        recordList.clear();
        String date = null;
        int index = 0;
        float sum = 0f;
        for (BmobRecord ele : list){
            if (! ele.getDate().equals(date)){
                date = ele.getDate();
                DbRecord group = new DbRecord();
                group.setDate(date);
                group.setType(1);
                recordList.add(group);
                index = recordList.indexOf(group);
                sum = 0f;
            }
            DbRecord record = new DbRecord(ele, ele.getObjectId());
            record.save();
            recordList.add(record);
            sum += record.getMoney();
            recordList.get(index).setMoney(sum);
        }
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
