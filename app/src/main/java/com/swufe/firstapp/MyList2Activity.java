package com.swufe.firstapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    Handler handler;
    private SimpleAdapter listItemAdapter;
    private List<HashMap<String,String>> listItems;
    private String TAG = "MyList2Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list2);
        initListView();

        this.setlistItemAdapter(listItemAdapter);

        Thread t=new Thread(this);
        t.start();

        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==7) {
                    listItems = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter= new SimpleAdapter(MyList2Activity.this,listItems,
                    R.layout.list_item,
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail});
                    setListAdapter(listItemAdapter);
                     }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    private void setlistItemAdapter(SimpleAdapter listItemAdapter) {
    }




    private void initListView(){
        listItems=new ArrayList<HashMap<String,String>>();
        for(int i=0;i<10;i++){
            HashMap<String,String> map=new HashMap<String, String>();
            map.put("ItemTitle","Rate"+i);
            map.put("ItemDetail","detail"+i);
            listItems.add(map);
        }
//        //生成适配器的Item 和动态数组对应的元素
//        listItemAdapter =new SimpleAdapter(this,listItems,//listItems的数据源
//                R.layout.list_item ,//ListItem的XML布局实现
//                new String[]{"ItemTitle","ItemDetail"},
//                new int[]{R.id.itemTitle,R.id.itemDetail});
    }

    public void run() {
        //获取网络数据，放入 List 带回到主线程中
        List<HashMap<String,String>> retlist=new ArrayList<HashMap<String,String>>();
        Document doc=null;
        try {
            Thread.sleep(3000);
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
                HashMap<String,String> map=new HashMap<String,String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);
                retlist.add(map);

            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7);
        msg.obj = retlist;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");

        TextView title = view.findViewById(R.id.itemTitle);
        TextView detail = view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());

        //打开新的页面，带入参数
        Intent rateCalc = new Intent(this,rateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("detail",Float.parseFloat(detailStr));
        startActivity(rateCalc);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        //构造对话框进行确认操作
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("确认是否删除该数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // 删除数据操作
        Log.i(TAG, "onItemLongClick: 长按删除列表 position：" + position);
        listItems.remove(position);
        listItemAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton("否",null);
        builder.create().show();
        //区分是否屏蔽短按事件
        return true;
    }
}
