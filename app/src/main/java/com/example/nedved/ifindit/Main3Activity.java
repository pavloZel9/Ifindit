package com.example.nedved.ifindit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {
    private WebView mWeb;
    private class WebViewer extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
    String fName1;
    int a,m=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ArrayList<String> l11 = new ArrayList<String>();
        l11 = getIntent().getStringArrayListExtra("La_d1");

        mWeb = (WebView) findViewById(R.id.webWiev1);
        mWeb.getSettings().setJavaScriptEnabled(true);

       fName1 = getIntent().getStringExtra("fname1").toString();

        if(l11.get(0).length()==0) {


       }else{
           mWeb.loadUrl("https://en.wikipedia.org/wiki/" + l11.get(0));

           mWeb.setWebViewClient(new WebViewer());

          a=1;
       }
         if (fName1.length() != 0 ){

           mWeb.loadUrl("https://www.google.com/search?q=" + fName1 + "&tbm=bks");

           mWeb.setWebViewClient(new WebViewer());

           a=1;
       }

     if(a==0) {

            mWeb.loadUrl("https://en.wikipedia.org/");

            mWeb.setWebViewClient(new WebViewer());
       }

        }

    }
