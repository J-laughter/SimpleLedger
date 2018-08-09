package com.example.laughter.simpleledger.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.util.PermissionCheckUtility;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends PermissionCheckUtility {

    private BmobUser user;
    private EditText edit_username;
    private EditText edit_password;
    private CheckBox cb_password;
    private SharedPreferences spUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //设置状态栏透明
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
    }

    private void initView(){
        edit_username = (EditText)findViewById(R.id.edit_username_login);
        edit_password = (EditText)findViewById(R.id.edit_password_login);
        cb_password = (CheckBox)findViewById(R.id.checkbox_login);
        Button but_login = (Button)findViewById(R.id.login_but_login);

        spUserInfo = this.getSharedPreferences("userInfo",MODE_PRIVATE);
        edit_username.setText(spUserInfo.getString("username",null));
        if (spUserInfo.getBoolean("isChecked",false)){
            edit_password.setText(spUserInfo.getString("password",null));
            cb_password.setChecked(true);
        }

        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new BmobUser();
                user.setUsername(edit_username.getText().toString());
                user.setPassword(edit_password.getText().toString());
                login();
            }
        });

        TextView register = (TextView)findViewById(R.id.register_but_login);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(){

        user.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e==null){
                    SharedPreferences.Editor editor = spUserInfo.edit();
                    editor.putString("username",edit_username.getText().toString());
                    editor.putString("password",edit_password.getText().toString());
                    editor.putBoolean("isChecked",cb_password.isChecked());
                    editor.putBoolean("isLogin",true);
                    editor.apply();
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
