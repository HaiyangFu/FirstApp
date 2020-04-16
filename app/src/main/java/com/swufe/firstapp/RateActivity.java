package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {
     EditText rmb;
     TextView show;
     private float dollarRate=6.7f;
    private float euroRate=11f;
    private float wonRate=500f;
    public final String TAG= "  RateActivity";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb=(EditText)findViewById(R.id.inp_rmb);
        show=(TextView)findViewById(R.id.show_money);

    }



    public void onClick(View btn){
        String str=rmb.getText().toString();
        float r=0;
        float val=0;
        if(str.length()>0){
            r=Float.parseFloat(str);
        }
        else{
            Toast.makeText(this,"inputMoney",Toast.LENGTH_LONG).show();
        }
        if(btn.getId()==R.id.btn_dollar){
            val=r*(1/dollarRate);
        }
        if(btn.getId()==R.id.button_euro){
            val=r*(1/euroRate);
        }
        if (btn.getId()==R.id.button_mon)
            val=r*wonRate;
        show.setText(String.valueOf(val));
    }
    public void  openone(View btn){
        Intent config=new Intent(this,ConfigActivity.class);

        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);
        Log.i(TAG,"openone:dollarRate="+dollarRate);
        Log.i(TAG,"openone:euroRate="+euroRate);
        Log.i(TAG,"openone:wonRate="+wonRate);
        startActivityForResult(config,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && requestCode ==2){
        Bundle bundle = data.getExtras();

        dollarRate=bundle.getFloat("key_dollar",0.1f);
        euroRate = bundle.getFloat("key_euro",0.1f);
        wonRate = bundle.getFloat("key_won",0.1f);

            Log.i(TAG,"openone:dollarRate="+dollarRate);
            Log.i(TAG,"openone:euroRate="+euroRate);
            Log.i(TAG,"openone:wonRate="+wonRate);
        }
    }
}
