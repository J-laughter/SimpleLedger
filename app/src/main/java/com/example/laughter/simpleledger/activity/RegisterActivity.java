package com.example.laughter.simpleledger.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import com.example.laughter.simpleledger.R;

public class RegisterActivity extends AppCompatActivity {

    private BmobUser user;
    private EditText edit_userName;
    private EditText edit_email;
    private EditText edit_password;
    private EditText edit_verifyPw;
    private SharedPreferences spUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setToolBar();
        initView();
    }

    private void setToolBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_diy);
        toolbar.setTitle("");
        Button back = (Button)findViewById(R.id.back_but_toolbar);
        back.setBackgroundResource(R.drawable.back_selector);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView title = (TextView)findViewById(R.id.title_toolbar);
        title.setText(R.string.register);
    }

    private void initView(){
        edit_userName = (EditText)findViewById(R.id.name_edit_register);
        edit_email = (EditText)findViewById(R.id.email_edit_register);
        edit_password = (EditText)findViewById(R.id.password_edit_register);
        edit_verifyPw = (EditText)findViewById(R.id.verify_pw_edit_register);
        Button register = (Button)findViewById(R.id.register_but_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                judge();
            }
        });
    }

    private void judge(){
        String username = edit_userName.getText().toString();
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        String verifyPw = edit_verifyPw.getText().toString();

        if (username.equals("")){
            Toast.makeText(RegisterActivity.this,"请输入昵称",Toast.LENGTH_SHORT).show();
        }else if (email.equals("")){
            Toast.makeText(RegisterActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
        }else if (password.equals("")){
            Toast.makeText(RegisterActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
        }else if (verifyPw.equals("")){
            Toast.makeText(RegisterActivity.this,"请确认密码",Toast.LENGTH_SHORT).show();
        }else {
            if(!password.equals(verifyPw)){
                Toast.makeText(RegisterActivity.this,"两次输入密码不同，请重新输入",Toast.LENGTH_SHORT).show();
            }else {
                user = new BmobUser();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                register();
            }
        }
    }

    private void register(){
        spUserInfo = this.getSharedPreferences("userInfo",MODE_PRIVATE);
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if(e==null){
                    Editor editor = spUserInfo.edit();
                    editor.putString("username",edit_userName.getText().toString());
                    editor.putString("password",edit_password.getText().toString());
                    editor.apply();
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    switch (e.getErrorCode()){
                        default:
                            Toast.makeText(RegisterActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                            break;
                        case 202:
                            Toast.makeText(RegisterActivity.this,"用户名已存在，请重新输入",Toast.LENGTH_SHORT).show();
                            break;
                        case 203:
                            Toast.makeText(RegisterActivity.this,"该邮箱已被注册",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
