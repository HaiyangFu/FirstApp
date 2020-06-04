package com.swufe.firstapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable {
    private static final String TAG =  "RateActivity";
    String date[]={"Name","SchoolNumber","Major"};
    Handler handler;
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_rate_list);
        SharedPreferences sp = getSharedPreferences ("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY,"");
        Log.i(TAG, "onCreate: lastRateDateStr"+logDate);

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
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i(TAG, "run: curDateStr"+curDateStr + " logDate"+logDate);

        if(curDateStr.equals(logDate)){
            //如果日期相等，则不用从网络中获取数据
            Log.i(TAG, "run: 日期相等，产品哪个数据库中获取数据");
            RateManager manager = new RateManager(this);
            for(RateItem item : manager.listAll()){
                retList.add(item.getCurName() +"-->"+item.getCurRate());
            }
            }else{
            //从网络中获取在线数据
            Log.i(TAG, "run: 日期不等，从网络中获取在线数据");
            Document doc=null;
            try {
                doc = (Document) Jsoup.connect("http://www.boc.cn/sourcedb/whpj").get();
                Elements tables = (Elements) doc.getElementById("table");
                Element table2 = tables.get(1);
                //获取TD中的数据
                Elements tds = table2.getElementsByTag("td");
                List<RateItem> rateList = new ArrayList<RateItem>();
                for (int i = 0; i < tds.size(); i += 8) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    Log.i(TAG,"run:text="+td1.text()+"==>"+td2.text());
                    String str1 = td1.text();
                    String val = td2.text();
                    retList.add(td1.text()+"==>"+td2.text());
                    rateList.add(new RateItem(str1,val));

                    //把数据写入数据库
                    RateManager manager = new RateManager(this);
                    manager.deleteAll();
                    manager.addAll(rateList);

                    //更新记录日期
                    SharedPreferences sp = getSharedPreferences("myrate",Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(DATE_SP_KEY,curDateStr);
                    edit.commit();
                    Log.i(TAG, "run: 更新日期结束"+curDateStr);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        //返回主线程
        Message msg = handler.obtainMessage(7);
         msg.obj = retList;
        handler.sendMessage(msg);
    }
}
