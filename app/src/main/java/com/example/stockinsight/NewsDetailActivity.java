package com.example.stockinsight;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {
    private WebView newsWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        newsWebView = findViewById(R.id.newsWebView);
        WebSettings webSettings = newsWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        newsWebView.setWebViewClient(new WebViewClient());

        String newsUrl = getIntent().getStringExtra("news_url");
        if (newsUrl != null) {
            newsWebView.loadUrl(newsUrl);
        }
    }
}
