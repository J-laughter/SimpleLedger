package com.example.laughter.simpleledger.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laughter.simpleledger.R;
import com.example.laughter.simpleledger.util.AlipayUtility;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setToolbar();
        initDefaultDate();
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_diy);
        toolbar.setTitle("");
        TextView title = (TextView)findViewById(R.id.title_toolbar);
        title.setText(R.string.about);

        Button back = (Button)findViewById(R.id.back_but_toolbar);
        back.setVisibility(View.VISIBLE);
        back.setBackgroundResource(R.drawable.back_selector);
        back.setOnClickListener(this);

        setSupportActionBar(toolbar);
    }

    private void initDefaultDate(){
        TextView text_version = (TextView)findViewById(R.id.text_version);
        String versionName = null;
        try {
            versionName = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        text_version.setText(String.format(this.getString(R.string.version),versionName));
        TextView text_call = (TextView)findViewById(R.id.text_call);
        TextView text_csdn = (TextView)findViewById(R.id.text_csdn);
        TextView text_support = (TextView)findViewById(R.id.text_support);
        text_call.setOnClickListener(this);
        text_csdn.setOnClickListener(this);
        text_support.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
            case R.id.text_call:
                if (checkApkExist(this, "com.tencent.mobileqq")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+1131054117+"&version=1")));
                }else{
                    Toast.makeText(this,"本机未安装QQ应用", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.text_csdn:
                Intent intent = new Intent(AboutActivity.this, WebActivity.class);
                intent.putExtra("url","https://blog.csdn.net/laughter_jiang");
                intent.putExtra("title","我的博客");
                startActivity(intent);
                break;
            case R.id.text_support:
                if(AlipayUtility.hasInstalledAlipayClient(this)){
                    //第二个参数代表要给被支付的二维码code  可以在用草料二维码在线生成
                    AlipayUtility.startAlipayClient(this, "FKX01042CVV8AHV0VPHXC7");
                }else{
                    Toast.makeText(this,"没有检测到支付宝客户端",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back_but_toolbar:
                finish();
                break;
        }

    }

    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
