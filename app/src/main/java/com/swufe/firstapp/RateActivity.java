package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {
    EditText rmb;
    TextView show;
    Handler handler;
    public float dollarRate = 6.7f;
    public float euroRate = 11f;
    public float wonRate = 500f;
    private final String TAG = "RateActivity";
    String UpdateDate;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText) findViewById(R.id.inp_rmb);
        show = (TextView) findViewById(R.id.show_money);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);

        UpdateDate = sharedPreferences.getString("Update_date", "");

        //从当前系统获取时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        //判断时间是否相等
        if (!todayStr.equals(UpdateDate)) {
            //开启子线程
            Thread t = new Thread(this);
            t.start();
            Log.i(TAG, "OnCreate:需要更新");
        } else {
            Log.i(TAG, "OnCreate:不需要更新");
        }


        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar_rate");
                    euroRate = bdl.getFloat("euro_rate");
                    wonRate = bdl.getFloat("won_rate");
                    Log.i(TAG, "handleMessage:" + dollarRate);
                    Log.i(TAG, "handleMessage:" + euroRate);
                    Log.i(TAG, "handleMessage:" + wonRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("dollar_rate", dollarRate);
                    editor.putFloat("euro_rate", euroRate);
                    editor.putFloat("won_rate", wonRate);
                    editor.putString("Update_date", todayStr);
                    editor.apply();

                    Toast.makeText(RateActivity.this, "汇率已更新", Toast.LENGTH_SHORT);

                }
                super.handleMessage(msg);
            }
        };

    }

    public void onClick(View btn) {
        String str = rmb.getText().toString();
        float r = 0;
        float val = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            Toast.makeText(this, "inputMoney", Toast.LENGTH_LONG).show();
        }
        if (btn.getId() == R.id.btn_dollar) {
            val = r * (1 / dollarRate);
        }
        if (btn.getId() == R.id.button_euro) {
            val = r * (1 / euroRate);
        }
        if (btn.getId() == R.id.button_mon)
            val = r * wonRate;
        show.setText(String.valueOf(val));
    }

    public void openOne(View btn) {
        openConfig();
    }

    public void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);

        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        Log.i(TAG, "openone:dollarRate=" + dollarRate);
        Log.i(TAG, "openone:euroRate=" + euroRate);
        Log.i(TAG, "openone:wonRate=" + wonRate);
        startActivityForResult(config, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();

            dollarRate = bundle.getFloat("key_dollar", 0.1f);
            euroRate = bundle.getFloat("key_euro", 0.1f);
            wonRate = bundle.getFloat("key_won", 0.1f);

            Log.i(TAG, "openone:dollarRate=" + dollarRate);
            Log.i(TAG, "openone:euroRate=" + euroRate);
            Log.i(TAG, "openone:wonRate=" + wonRate);

            //将新设置的汇率写到sp里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate", dollarRate);
            editor.putFloat("euro_rate", euroRate);
            editor.putFloat("won_rate", wonRate);
            editor.commit();
            Log.i(TAG, "onActivityResult:数据已保存到onActivityResult");
        }
    }

    @Override
    public void run() {
        Log.i(TAG, "run");

        //用于保存获取的数据
        Document doc=null;
        Bundle bundle = new Bundle();
        //获取MSG对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        msg.obj = "Hello from run()";
        handler.sendMessage(msg);
        //获取网络数据
        URL url;
        try {
            url = new URL("https://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();
            String html = inputStream2String(in);
            doc = Jsoup.parse(html);
            Log.i(TAG, "run: html=" + html);
            Log.i(TAG, "run:" + doc.title());
            /*Elements trs=doc.getElementsByTag("tr");
            int i=1;
            for(Element tr:trs){
                Log.i(TAG,"run:tr["+i+"]="+tr);
                i++;
            }*/

            Elements tds = doc.getElementsByTag("td");

            /*for(Element td:tds){
                //Log.i(TAG,"run:td="+td);
                Log.i(TAG,"run:text="+td.text());
               // Log.i(TAG,"run:html="+td.html());
            }*/
            for (int i = 0; i < tds.size(); i += 6) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                //Log.i(TAG,"run:text="+td1.text()+"==>"+td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));

                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
                } else if ("韩元".equals(str1)) {
                    bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
                }

            }
            Log.i(TAG, "run: " + doc.title());
            Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                Log.i(TAG, "%s\n\t%s" + headline.attr("title") + headline.absUrl("href"));
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    public String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;

        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "GB2312");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }

        return out.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {
            openConfig();
        } else if (item.getItemId() == R.id.open_list) {
            //打开新的列表窗口
            Intent list = new Intent(this, RateActivity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }
}

