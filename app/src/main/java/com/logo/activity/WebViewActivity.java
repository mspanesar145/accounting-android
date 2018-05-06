package com.logo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.logo.R;

/**
 * Created by mandeep on 6/5/18.
 */

public class WebViewActivity extends LogoActivity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent webviewIntent = getIntent();

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl((String) webviewIntent.getStringExtra("url"));
    }
}