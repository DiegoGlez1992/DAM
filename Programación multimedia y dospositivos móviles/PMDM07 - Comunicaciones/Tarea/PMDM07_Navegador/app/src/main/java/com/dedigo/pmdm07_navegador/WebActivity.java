package com.dedigo.pmdm07_navegador;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WebActivity extends AppCompatActivity {

    WebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = findViewById(R.id.visorWeb);
        String URL = getIntent().getStringExtra("nombreSitio");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(URL);
    }
}
