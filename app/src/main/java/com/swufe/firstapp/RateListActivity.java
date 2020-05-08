package com.swufe.firstapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable {
    private static final String TAG =  "RateActivity";
    String date[]={"Name","SchoolNumber","Major"};
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_rate_list);
        ListAdapter adapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,date);
        setListAdapter(adapter);
        List list1=new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==7) {
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this, android.R.layout.simple_expandable_list_item_1, list2);
                    setListAdapter(adapter);
                    Thread t=new Thread();
                    t.start();
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        //获取网络数据，放入List带回到主线程中
        List retList=new ArrayList<String>();
        Document doc=null;
        try {
            doc = (Document) Jsoup.connect("http://www.boc.cn/sourcedb/whpj").get();
            Elements tables = (Elements) doc.getElementById("table");
            Element table2 = tables.get(1);
             //获取TD中的数据
            Elements tds = table2.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 8) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                Log.i(TAG,"run:text="+td1.text()+"==>"+td2.text());
                String str1 = td1.text();
                String val = td2.text();
                retList.add(td1.text()+"==>"+td2.text());
              }
        }
            catch (IOException e) {
            e.printStackTrace();
        }
        //返回主线程
        Message msg = handler.obtainMessage(7);
         msg.obj = retList;
        handler.sendMessage(msg);
    }
}
