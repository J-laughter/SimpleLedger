package com.example.laughter.simpleledger.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.bean.BmobRecord;
import com.example.laughter.simpleledger.bean.DbRecord;

import org.litepal.LitePal;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView text_kind;
    private TextView text_date;
    private EditText edit_remark;
    private EditText edit_money;
    private String objectId;
    private BmobRecord bmobRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        setToolbar();
        initDate();
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_diy);
        toolbar.setTitle("");
        TextView title = (TextView)findViewById(R.id.title_toolbar);
        title.setText(R.string.modify);

        Button back = (Button)findViewById(R.id.back_but_toolbar);
        back.setVisibility(View.VISIBLE);
        back.setBackgroundResource(R.drawable.back_selector);
        back.setOnClickListener(this);

        Button confirm = (Button)findViewById(R.id.menu_but_toolbar);
        confirm.setBackgroundResource(R.drawable.confirm_selector);
        confirm.setVisibility(View.VISIBLE);
        confirm.setOnClickListener(this);

        setSupportActionBar(toolbar);
    }

    private void initDate(){
        text_kind = (TextView)findViewById(R.id.text_kind_mod);
        text_date = (TextView)findViewById(R.id.text_date_mod);
        edit_remark = (EditText)findViewById(R.id.edit_remark_mod);
        edit_money = (EditText)findViewById(R.id.edit_money_mod);
        Button delete = (Button)findViewById(R.id.but_delete_mod);
        delete.setOnClickListener(this);

        Intent intent = getIntent();
        DbRecord record = (DbRecord) intent.getSerializableExtra("record");
        text_kind.setText(record.getMoney() > 0 ? "收入" : "支出");
        text_date.setText(record.getDate());
        edit_remark.setText(record.getRemark());
        edit_money.setText(String.valueOf(Math.abs(record.getMoney())));
        objectId = record.getObjectId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
            case R.id.menu_but_toolbar :
                bmobRecord = new BmobRecord();
                bmobRecord.setRemark(edit_remark.getText().toString());
                if (text_kind.getText().toString().equals("收入")){
                    bmobRecord.setMoney(Float.parseFloat(edit_money.getText().toString()));
                }else {
                    bmobRecord.setMoney(-Float.parseFloat(edit_money.getText().toString()));
                }
                bmobRecord.update(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            ContentValues values = new ContentValues();
                            values.put("remark", bmobRecord.getRemark());
                            values.put("money", bmobRecord.getMoney());
                            LitePal.updateAll(DbRecord.class, values, "objectId = ?",objectId);
                            Toast.makeText(ModifyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                break;
            case R.id.back_but_toolbar :
                Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.but_delete_mod :
                BmobRecord record = new BmobRecord();
                record.setObjectId(objectId);
                record.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            LitePal.deleteAll(DbRecord.class, "objectId = ?",objectId);
                            Toast.makeText(ModifyActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                break;
        }
    }
}
