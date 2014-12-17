package com.discaddy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;


public class Discs extends Activity {

    private EditText discView;
    private String disc;
    private WebView wView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wView = new WebView(this);
        setContentView(wView);
        wView.getSettings().setJavaScriptEnabled(true);

        wView.loadUrl("http://flightanalyzer.com");
    }
}
