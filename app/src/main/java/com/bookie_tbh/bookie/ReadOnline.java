package com.bookie_tbh.bookie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class ReadOnline extends AppCompatActivity {

    private WebView webView;
    private String pdfUrl, url;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_online);

//      initialize web-view
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

//      getting intent and pdf url
        Intent intent = getIntent();
        pdfUrl = intent.getStringExtra("pdfUrl");

        // chromium, enable hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url) {
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }
        });

        url = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;

        checkPageFinished();
    }

    public void checkPageFinished() {
        //If view is blank:
        if (webView.getContentHeight() == 0) {

            //Run off main thread to control delay
            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Load url into the "WebView"
                    webView.loadUrl(url);
                }
                //Set 2s delay to give the view a longer chance to load before
                // setting the view (or more likely to display blank)
            }, 2000);

            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //If view is still blank:
                    if (webView.getContentHeight() == 0) {
                        //Loop until it works
                        checkPageFinished();
                    }
                }
                //Safely loop this function after 2.5s delay if page is not loaded
            }, 2500);

        }
    }

}