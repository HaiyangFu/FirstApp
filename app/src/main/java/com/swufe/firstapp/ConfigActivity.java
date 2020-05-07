package com.swufe.firstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    public final String TAG= "ConfigActivity";
    EditText dollarText;
    EditText euroText;
    EditText wonText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent=getIntent();
        float dollar2=intent.getFloatExtra("dollar_rate_key",0.00f);
        float euro2=intent.getFloatExtra("euro_rate_key",0.00f);
        float won2=intent.getFloatExtra("won_rate_key",0.00f );
        Log.i(TAG, "dollar2 "+dollar2);
        Log.i(TAG, "euro2 "+euro2);
        Log.i(TAG, "won2 "+won2);

        dollarText= (EditText)findViewById(R.id.dollar_rate);
        dollarText= (EditText)findViewById(R.id.euro_rate);
        dollarText= (EditText)findViewById(R.id.won_rate);
        //显示数据到控件
         dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));


    }
    public void save(View btn){
        Log.i(TAG,"save:");

        //获取新的值
        Float newDollar = Float.parseFloat(dollarText.getText().toString());
        Float newEuro = Float.parseFloat(euroText.getText().toString());
        Float newWon = Float.parseFloat(wonText.getText().toString());
        Log.i(TAG, "save:获取到新的值");
        Log.i(TAG, "save:newDollar "+newDollar);
        Log.i(TAG, "save:newEuro "+newEuro);
        Log.i(TAG, "save:newWon "+newWon);

        //保存到Bundle 或放到Extra
        Intent intent =getIntent();
        Bundle bdl=new Bundle();
        bdl.putFloat("key_dollar",newDollar);
        bdl.putFloat("key_euro",newEuro);
        bdl.putFloat("key_won",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);

        //返回到调用页面
        finish();
    }
}
