package com.example.laughter.simpleledger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.util.SpUtil;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.name_edit_register) EditText etUserName;
    @BindView(R.id.email_edit_register) EditText etEmail;
    @BindView(R.id.password_edit_register) EditText etPassword;
    @BindView(R.id.verify_pw_edit_register) EditText etVerifyPw;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.back_but_toolbar) Button butBack;
    @BindView(R.id.title_toolbar) TextView tvTitle;
    @BindView(R.id.register_but_register) Button butRegister;

    private BmobUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        setToolBar();
        initView();
    }

    private void setToolBar(){
        mToolbar.setTitle("");
        butBack.setBackgroundResource(R.drawable.back_selector);
        butBack.setVisibility(View.VISIBLE);
        butBack.setOnClickListener(view -> finish());
        tvTitle.setText(R.string.register);
        setSupportActionBar(mToolbar);
    }

    private void initView(){
        butRegister.setOnClickListener(view -> judge());
    }

    private void judge(){
        String username = etUserName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String verifyPw = etVerifyPw.getText().toString();

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
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if(e==null){
                    SpUtil.putString(getApplicationContext(), "username",etUserName.getText().toString());
                    SpUtil.putString(getApplicationContext(), "password",etPassword.getText().toString());

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
