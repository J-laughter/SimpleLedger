package com.example.laughter.simpleledger.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.model.BmobRecord;
import com.example.laughter.simpleledger.model.DbRecord;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar_diy) Toolbar mToolbar;
    @BindView(R.id.back_but_toolbar) Button butBack;
    @BindView(R.id.menu_but_toolbar) Button butConfirm;
    @BindView(R.id.title_toolbar) TextView tvTitle;
    @BindView(R.id.edit_money_add) EditText etMoney;
    @BindView(R.id.edit_remark_add) EditText etRemark;
    @BindView(R.id.text_date_add) TextView tvDate;
    @BindView(R.id.text_kind_add) TextView tvKind;

    private String[] kindArray = new String[]{"请选择" ,"收入" ,"支出"};
    private int year,month,day,kind;
    private BmobRecord bmobRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);

        setToolbar();
        initDefault();
    }

    private void setToolbar(){
        mToolbar.setTitle("");
        tvTitle.setText(R.string.selector);

        butBack.setVisibility(View.VISIBLE);
        butBack.setBackgroundResource(R.drawable.back_selector);
        butBack.setOnClickListener(this);

        butConfirm.setBackgroundResource(R.drawable.confirm_selector);
        butConfirm.setVisibility(View.VISIBLE);
        butConfirm.setOnClickListener(this);

        setSupportActionBar(mToolbar);
    }

    private void initDefault(){
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
        tvDate.setText(date);
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
            tvDate.setText(date);
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
                if (TextUtils.isEmpty(etMoney.getText().toString())){
                    Toast.makeText(AddActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etRemark.getText().toString())){
                    Toast.makeText(AddActivity.this, "请输入备注", Toast.LENGTH_SHORT).show();
                    return;
                }
                float money = 0;
                if (kind == 1){
                    money = Float.parseFloat(etMoney.getText().toString());
                }else {
                    money = -Float.parseFloat(etMoney.getText().toString());
                }
                String remark = etRemark.getText().toString();
                String date = tvDate.getText().toString();
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
        }
    }

    @OnClick({R.id.text_kind_add, R.id.text_date_add, R.id.edit_remark_add,
            R.id.edit_money_add})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.text_date_add:
                new DatePickerDialog(AddActivity.this,onDateSetListener,year,month-1,day).show();
                break;
            case R.id.text_kind_add:
                showKindChooseDialog();
                break;
            case R.id.edit_money_add:
                etMoney.setFocusable(true);
                etMoney.setFocusableInTouchMode(true);
                etMoney.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
            case R.id.edit_remark_add:
                etRemark.setFocusable(true);
                etRemark.setFocusableInTouchMode(true);
                etRemark.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
            default:
                break;
        }
    }

    private void showKindChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        // 2默认的选中
        builder.setSingleChoiceItems(kindArray, 0, (dialog, which) -> {// which是被选中的位置
            // showToast(which+"");
            tvKind.setText(kindArray[which]);
            kind = which;
            dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
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
