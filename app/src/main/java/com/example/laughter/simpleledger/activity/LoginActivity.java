package com.example.laughter.simpleledger.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.util.PermissionUtil;
import com.example.laughter.simpleledger.util.SpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends PermissionUtil {

    @BindView(R.id.edit_username_login) EditText etUsername;
    @BindView(R.id.edit_password_login) EditText etPassword;
    @BindView(R.id.checkbox_login) CheckBox cbPassword;
    @BindView(R.id.login_but_login) Button butLogin;
    @BindView(R.id.register_but_login) TextView butRegister;

    private BmobUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //设置状态栏透明
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
    }

    private void initView(){

        etUsername.setText(SpUtil.getString(getApplicationContext(), "username", null));
        if (SpUtil.getBoolean(getApplicationContext(), "isChecked", false)){
            etPassword.setText(SpUtil.getString(getApplicationContext(), "password",null));
            cbPassword.setChecked(true);
        }

        butLogin.setOnClickListener(view -> {
            user = new BmobUser();
            user.setUsername(etUsername.getText().toString());
            user.setPassword(etPassword.getText().toString());
            login();
        });

        butRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login(){

        user.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e==null){
                    SpUtil.putString(getApplicationContext(), "username",etUsername.getText().toString());
                    SpUtil.putString(getApplicationContext(), "password",etPassword.getText().toString());
                    SpUtil.putBoolean(getApplicationContext(), "isChecked",cbPassword.isChecked());
                    SpUtil.putBoolean(getApplicationContext(), "isLogin",true);

                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    switch (e.getErrorCode()){
                        default:
                            Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                            break;
                        case 101:
                            Toast.makeText(LoginActivity.this,"用户名或密码不正确",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
