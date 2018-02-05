package com.example.nedved.ifindit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main4Activity extends AppCompatActivity {
    private WebView mWeb;
    String fName2;
    private class WebViewer1 extends WebViewClient {
       @Override
       public boolean shouldOverrideUrlLoading (WebView view, String url)
      {
           view.loadUrl(url);
            return true;
        }
      }
   int m;
    int i,kl=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        fName2 = getIntent().getStringExtra("site");
        String k="";
        m=0;


        conversion(fName2);

       mWeb = (WebView) findViewById(R.id.webWiev2);
       mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.loadUrl( fName2.substring(0,m));

        mWeb.setWebViewClient(new Main4Activity.WebViewer1());



    }
    public void conversion(String days){
        for(int i = 0; i < days.toCharArray().length; i++){
            char p = days.toCharArray()[i];
            System.out.println(p);
            if(days.toCharArray()[i] == '/'){
                kl++;
                m=i;
                Log.d("FFFFFFFFFF",fName2.substring(0,m));
                if(kl==3){
                i=1000;}
            }

        }
    }
}
