package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyWorkActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {
    EditText inp;
    String TAG = "MyWorkActivity";
    Handler handler;
    String UpdateDate;
    List<String> list2;
    String word = list2.toString();


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_my_work);
          inp = (EditText)findViewById(R.id.keyWords);
          final ListView listView=(ListView)findViewById(R.id.work_list);
          //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("mywork", Activity.MODE_PRIVATE);
        UpdateDate = sharedPreferences.getString("Update_date", "");
        word = sharedPreferences.getString("word","");

        //从当前系统获取时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        //判断时间是否相等
        while (!todayStr.equals(UpdateDate+7)) {
            //开启子线程
            Thread t = new Thread(this);
            t.start();
            Log.i(TAG, "OnCreate:需要更新");
        }

        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==7) {
                    list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(MyWorkActivity.this, android.R.layout.simple_expandable_list_item_1, list2);
                    listView.setAdapter(adapter);
                    //保存更新
                    SharedPreferences sharedPreferences = getSharedPreferences("mywork", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("new_text",word);
                    editor.putString("Update_date", todayStr);
                    editor.apply();
                }
               super.handleMessage(msg);
            }
        };
    }
    public void onClick(View btn) {
        String str = inp.getText().toString();
        compileKeyWord(word,str);

    }
    //关键字匹配
    public boolean compileKeyWord(String word, String keyWord) {
        Pattern pn = Pattern.compile(keyWord+"\\w|\\w"+keyWord+"\\w|\\w"+keyWord);
        Matcher mr = null;
        mr = pn.matcher(word);
        if (mr.find())  {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        Log.i(TAG, "run");
        List list=new ArrayList<String>();
        //获取网络数据
        URL url;
        try {
            Document doc = null;
            doc = Jsoup.connect("https://www.swufe.edu.cn/").get();
            //Log.i(TAG, "run: html=" + html);
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByClass("i-noticlist");

            Element tb =  tables.get(0);
            Elements tds = tb.getElementsByTag("li");
            for(int i=0;i<tds.size();i++){
                Element td1 = tds.get(i);
                Log.i(TAG, "run: text = "+td1.text());
                list.add(td1.text());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将msg带回到主线程
        Message msg = handler.obtainMessage(7);
        msg.obj =  list;
        handler.sendMessage(msg);
        Log.i(TAG, "run: list="+list);
    }
    public String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;

        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }

        return out.toString();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //打开新的页面
        Intent newPage = new Intent(this,rateCalcActivity.class);
        startActivity(newPage);

    }
}
//url = new URL("https://www.swufe.edu.cn/");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream in = http.getInputStream();
//            String html = inputStream2String(in);
//            Document doc = Jsoup.parse(html);
