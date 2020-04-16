package com.swufe.firstapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RateActivity extends AppCompatActivity {
     EditText rmb;
     TextView show;

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
            val=r*(1/6.7f);
        }
        if(btn.getId()==R.id.button_euro){
            val=r*(1/11f);
        }
        if (btn.getId()==R.id.button_mon)
            val=r*500;
        show.setText(String.valueOf(val));
    }
}
