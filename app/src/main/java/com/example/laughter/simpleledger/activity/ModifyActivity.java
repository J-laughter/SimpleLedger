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
import com.example.laughter.simpleledger.model.BmobRecord;
import com.example.laughter.simpleledger.model.DbRecord;

import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar_diy) Toolbar mToolbar;
    @BindView(R.id.title_toolbar) TextView tvTitle;
    @BindView(R.id.back_but_toolbar) Button butBack;
    @BindView(R.id.menu_but_toolbar) Button butConfirm;

    @BindView(R.id.text_kind_mod) TextView tvKind;
    @BindView(R.id.text_date_mod) TextView tvDate;
    @BindView(R.id.edit_remark_mod) EditText etRemark;
    @BindView(R.id.edit_money_mod) EditText etMoney;
    @BindView(R.id.but_delete_mod) Button butDelete;

    private String objectId;
    private BmobRecord bmobRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ButterKnife.bind(this);

        setToolbar();
        initDate();
    }

    private void setToolbar(){
        mToolbar.setTitle("");
        tvTitle.setText(R.string.modify);

        butBack.setVisibility(View.VISIBLE);
        butBack.setBackgroundResource(R.drawable.back_selector);
        butBack.setOnClickListener(this);

        butConfirm.setBackgroundResource(R.drawable.confirm_selector);
        butConfirm.setVisibility(View.VISIBLE);
        butConfirm.setOnClickListener(this);

        setSupportActionBar(mToolbar);
    }

    private void initDate(){
        butDelete.setOnClickListener(this);

        Intent intent = getIntent();
        DbRecord record = (DbRecord) intent.getSerializableExtra("record");
        tvKind.setText(record.getMoney() > 0 ? "收入" : "支出");
        tvDate.setText(record.getDate());
        etRemark.setText(record.getRemark());
        etMoney.setText(String.valueOf(Math.abs(record.getMoney())));
        objectId = record.getObjectId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
            case R.id.menu_but_toolbar :
                bmobRecord = new BmobRecord();
                bmobRecord.setRemark(etRemark.getText().toString());
                if (tvKind.getText().toString().equals("收入")){
                    bmobRecord.setMoney(Float.parseFloat(etMoney.getText().toString()));
                }else {
                    bmobRecord.setMoney(-Float.parseFloat(etMoney.getText().toString()));
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
