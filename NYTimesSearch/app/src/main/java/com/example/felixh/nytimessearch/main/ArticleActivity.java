package com.example.felixh.nytimessearch.main;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.felixh.nytimessearch.R;
import com.example.felixh.nytimessearch.model.Article;

import org.parceler.Parcels;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Extract article object from intent extras
        Article article = (Article) Parcels.unwrap(getIntent().getParcelableExtra("article"));
        WebView wvArticle = (WebView)  findViewById(R.id.wvArticle);
        final String web_url = article.getWebUrl();
        if (web_url != null && !web_url.isEmpty()) {
            // Configure related browser settings
            wvArticle.getSettings().setLoadsImagesAutomatically(true);
            wvArticle.getSettings().setJavaScriptEnabled(true);
            wvArticle.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            // Configure the client to use when opening URLs
            wvArticle.setWebViewClient(new MyBrowser());

            wvArticle.loadUrl(web_url);
        }

    }
    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }
}
