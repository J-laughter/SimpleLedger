package com.example.laughter.simpleledger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.adapter.RecordsAdapter;
import com.example.laughter.simpleledger.bean.BmobRecord;
import com.example.laughter.simpleledger.bean.DbRecord;
import com.example.laughter.simpleledger.util.PermissionCheckUtility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends PermissionCheckUtility {

    private ProgressDialog progressDialog;
    private List<DbRecord> recordList = new ArrayList<>();
    private RecordsAdapter adapter;
    private ListView listView;
    private TextView textBlank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Bmob默认初始化
        Bmob.initialize(this, "b41e662d256ba141c87f5c04beb23f35");

        if (isLogin()){
            setToolbar();
            init();
            adapter = new RecordsAdapter(this, R.layout.item_record, recordList);
            listView.setAdapter(adapter);
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
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
    }

    private void init(){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView)findViewById(R.id.list_view_main);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, ModifyActivity.class);
                intent.putExtra("record", recordList.get(position));
                startActivity(intent);
            }
        });
        textBlank = (TextView)findViewById(R.id.text_blank);
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
                    SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                    editor.putBoolean("idLogin",false);
                    editor.apply();
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
        SharedPreferences spUserInfo = this.getSharedPreferences("userInfo",MODE_PRIVATE);
        return spUserInfo.getBoolean("isLogin",false);
    }

    private void queryDateFromDatebase(){
        List<DbRecord> list = LitePal.order("date desc").find(DbRecord.class);
        if (list.size() > 0){
            listView.setVisibility(View.VISIBLE);
            textBlank.setVisibility(View.GONE);
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
                                textBlank.setVisibility(View.VISIBLE);
                                listView.setVisibility(View.GONE);
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
