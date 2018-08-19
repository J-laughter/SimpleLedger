package com.example.laughter.simpleledger.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laughter.simpleledger.R;

public class WebActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        setToolbar();
        initDate();
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_diy);
        toolbar.setTitle("");
        TextView title = (TextView)findViewById(R.id.title_toolbar);
        Intent intent = getIntent();
        title.setText(intent.getStringExtra("title"));

        Button back = (Button)findViewById(R.id.back_but_toolbar);
        back.setVisibility(View.VISIBLE);
        back.setBackgroundResource(R.drawable.back_selector);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setSupportActionBar(toolbar);
    }

    private void initDate(){
        progressBar= (ProgressBar)findViewById(R.id.progressbar);//进度条
        webView = (WebView) findViewById(R.id.webview);
        Intent intent = getIntent();
        webView.loadUrl(intent.getStringExtra("url"));//加载url
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        webView.destroy();
        webView=null;
    }
}
