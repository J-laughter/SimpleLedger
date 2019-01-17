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

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends AppCompatActivity {

    @BindView(R.id.progressbar) ProgressBar progressBar;
    @BindView(R.id.webview) WebView webView;
    @BindView(R.id.toolbar_diy) Toolbar toolbar;
    @BindView(R.id.title_toolbar) TextView title;
    @BindView(R.id.back_but_toolbar) Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);

        setToolbar();
        initDate();
    }

    private void setToolbar(){
        toolbar.setTitle("");
        Intent intent = getIntent();
        title.setText(intent.getStringExtra("tvTitle"));

        back.setVisibility(View.VISIBLE);
        back.setBackgroundResource(R.drawable.back_selector);
        back.setOnClickListener(view -> finish());

        setSupportActionBar(toolbar);
    }

    private void initDate(){
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
