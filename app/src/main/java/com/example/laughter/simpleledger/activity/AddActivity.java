package com.example.laughter.simpleledger.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.bean.BmobRecord;
import com.example.laughter.simpleledger.bean.DbRecord;

import java.util.Calendar;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edit_money;
    private EditText edit_remark;
    private TextView text_date;
    private TextView text_kind;
    private String[] kindArray = new String[]{"请选择" ,"收入" ,"支出"};
    private int year,month,day,kind;
    private BmobRecord bmobRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        setToolbar();
        initDefault();
    }

    public void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_diy);
        toolbar.setTitle("");
        TextView title = (TextView)findViewById(R.id.title_toolbar);
        title.setText(R.string.selector);

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

    private void initDefault(){
        edit_money = (EditText) findViewById(R.id.edit_money_add);
        edit_remark = (EditText) findViewById(R.id.edit_remark_add);
        text_date = (TextView)findViewById(R.id.text_date_add);
        text_kind = (TextView)findViewById(R.id.text_kind_add);
        RelativeLayout rl_kind = (RelativeLayout)findViewById(R.id.rl_kind_add);
        rl_kind.setOnClickListener(this);
        RelativeLayout rl_money = (RelativeLayout)findViewById(R.id.rl_money_add);
        rl_money.setOnClickListener(this);
        RelativeLayout rl_remark = (RelativeLayout)findViewById(R.id.rl_remark_add);
        rl_remark.setOnClickListener(this);
        RelativeLayout rl_date = (RelativeLayout)findViewById(R.id.rl_date_add);
        rl_date.setOnClickListener(this);
        showDefaultData();
    }

    private void showDefaultData(){
        Calendar nowDate = Calendar.getInstance();
        year = nowDate.get(Calendar.YEAR);
        month = nowDate.get(Calendar.MONTH)+1;
        day = nowDate.get(Calendar.DAY_OF_MONTH);
        String strMonth = String.valueOf(month);
        if (month < 10){
            strMonth = "0" + month;
        }
        String strDay = String.valueOf(day);
        if (day < 10){
            strDay = "0" + day;
        }
        String date = year + "-" + strMonth + "-" + strDay;
        text_date.setText(date);
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear+1;
            String strMonth = String.valueOf(monthOfYear);
            if (monthOfYear < 10){
                strMonth = "0" + monthOfYear;
            }
            String strDay = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10){
                strDay = "0" + dayOfMonth;
            }
            String date = year + "-" + strMonth + "-" + strDay;
            text_date.setText(date);
        }
    };


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            default:
                break;
            case R.id.back_but_toolbar:
                intent = new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_but_toolbar:
                if (kind == 0){
                    Toast.makeText(AddActivity.this, "请选择类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edit_money.getText().toString())){
                    Toast.makeText(AddActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edit_remark.getText().toString())){
                    Toast.makeText(AddActivity.this, "请输入备注", Toast.LENGTH_SHORT).show();
                    return;
                }
                float money = 0;
                if (kind == 1){
                    money = Float.parseFloat(edit_money.getText().toString());
                }else {
                    money = -Float.parseFloat(edit_money.getText().toString());
                }
                String remark = edit_remark.getText().toString();
                String date = text_date.getText().toString();
                bmobRecord = new BmobRecord(remark, money, date, 0);
                bmobRecord.setUser(BmobUser.getCurrentUser());
                bmobRecord.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            DbRecord dbRecord = new DbRecord(bmobRecord, s);
                            dbRecord.save();
                            Toast.makeText(AddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.rl_date_add:
                new DatePickerDialog(AddActivity.this,onDateSetListener,year,month-1,day).show();
                break;
            case R.id.rl_kind_add:
                showKindChooseDialog();
                break;
            case R.id.rl_money_add:
                edit_money.setFocusable(true);
                edit_money.setFocusableInTouchMode(true);
                edit_money.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
            case R.id.rl_remark_add:
                edit_remark.setFocusable(true);
                edit_remark.setFocusableInTouchMode(true);
                edit_remark.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
        }
    }

    private void showKindChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        builder.setSingleChoiceItems(kindArray, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                text_kind.setText(kindArray[which]);
                kind = which;
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder.show();// 让弹出框显示
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
