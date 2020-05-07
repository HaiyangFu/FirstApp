package com.swufe.firstapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class rateCalcActivity extends AppCompatActivity {
    String TAG = "rateCalcActivity";
    float rate=0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_calc);
        String title = getIntent().getStringExtra("title");
        rate = getIntent().getFloatExtra("rate",0f);

        ((TextView)findViewById(R.id.show_money)).setText(title);
    }
}
