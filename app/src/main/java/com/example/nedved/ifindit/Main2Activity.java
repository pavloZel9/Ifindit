package com.example.nedved.ifindit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    int a;
    String h="";
    ArrayList<String> l1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent Intent2 = new Intent(Main2Activity.this, Main3Activity.class);
        a = 0;


        ArrayList<String> l2 = new ArrayList<String>();
       l1 = new ArrayList<String>();
        ListView list1 = (ListView) findViewById(R.id.listView1);
        l1 = getIntent().getStringArrayListExtra("wb1");
        l2 = getIntent().getStringArrayListExtra("La_d");

        String fName = getIntent().getStringExtra("fname");


        Intent2.putExtra("fname1", fName);
        Intent2.putStringArrayListExtra("La_d1", l2);
        if (l2.get(0).length() == 0) {
            if (fName.length() != 0) {

                a = 1;
                startActivity(Intent2);
            }
        } else {

            a = 1;
            startActivity(Intent2);
        }
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
        list1.addHeaderView(header);

        list1.setAdapter(adapter);
        registerForContextMenu(list1);
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
                Intent Intent22 = new Intent(Main2Activity.this, Main4Activity.class);

                Intent22.putExtra("site", l1.get(info.position-1));
                startActivity(Intent22);
                return true;
            case R.id.delete:
               //метод, выполняющий действие при удалении пункта меню
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    }




