package com.example.nedved.ifindit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;

public class Main5Activity extends AppCompatActivity {
    private DefaultHttpClient mHttpClient;
    String h="";
    ArrayList<String> l1;
    int g=0;int current;
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
    TabHost tabHost;
    TabHost.TabSpec tabSpec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        /////////////////////

        ArrayList<String> l2 = new ArrayList<String>();
        l1 = new ArrayList<String>();
        ListView list1 = (ListView) findViewById(R.id.listView1);
        l1 = getIntent().getStringArrayListExtra("wb1");
      //  l2 = getIntent().getStringArrayListExtra("La_d");

     //   String fName = getIntent().getStringExtra("fname");
        mWeb = (WebView) findViewById(R.id.webv);
        mWeb.getSettings().setJavaScriptEnabled(true);

        ////////////////////////////////////////////////////////////////

        setTitle("TabHost");

        tabHost = (TabHost) findViewById(R.id.tabhost);

        tabHost.setup();




        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Похожие картинки");
        tabHost.addTab(tabSpec);
        ListView list2 = (ListView) findViewById(R.id.listv1);
        if (l1.size()<10){
            for(int ii=l1.size()+1;ii<10;ii++){
                l1.add("https://lh5.ggpht.com/iB9f47bKvIrm2bCEbP5eGFT8WxQ0rLovNp--i005sI_HrKuxba2zi575F12-p1kmVw=w300");

            }}
        Weat weather_data[] = new Weat[]
                {

                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(0)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(1)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(2)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(3)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(4)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(5)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(6)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(7)),
                        new Weat(R.drawable.common_google_signin_btn_icon_dark_disabled, l1.get(8)),
                };

        WeatherAdapter adapter = new WeatherAdapter(this,
                R.layout.listview_item_row, weather_data);



        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
        list2.addHeaderView(header);

        list2.setAdapter(adapter);
        registerForContextMenu(list2);


        tabHost.setCurrentTab(0);

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                // метод, выполняющий действие при редактировании пункта меню
              //  Intent Intent22 = new Intent(Main5Activity.this, Main4Activity.class);
                g++;
                if(g<2){
                tabSpec = tabHost.newTabSpec("tag1");

                tabSpec.setContent(R.id.tab1);
                tabSpec.setIndicator("Сайти с похожей картинкой");
                tabHost.addTab(tabSpec);

                    current=g;

                }
                android.util.Log.d("decddddddd",Integer.toString(current) );
                tabHost.setCurrentTab(current);
     /*   Intent int1= new Intent(Intent.ACTION_SEND);
        int1.setType("text/plain");
        String shareBody ="df";
        String sBody ="df1";
        int1.putExtra(Intent.EXTRA_SUBJECT,shareBody);
        int1.putExtra(Intent.EXTRA_TEXT,sBody);
        startActivity(Intent.createChooser(int1,"mychooser"));*/


                mWeb.loadUrl("https://www.google.com/searchbyimage?&image_url="+l1.get(info.position-1));
                mWeb.setWebViewClient(new Main5Activity.WebViewer());
               // Intent22.putExtra("site", l1.get(info.position-1));
               // startActivity(Intent22);
                return true;
            case R.id.delete:
                //метод, выполняющий действие при удалении пункта меню
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
///////////////////////////////////////



}
