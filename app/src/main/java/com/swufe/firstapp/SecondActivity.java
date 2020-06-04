package com.swufe.firstapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        score=(TextView)findViewById(R.id.score);


    }
    public void butAdd1(View but){
        showScore(1);

    }
    public void butAdd2(View but){
        showScore(2);
    }
    public void butAdd3(View but){
        showScore(3);

    }
    public void butreset(View but){
        score.setText("0");

    }
    private void showScore(int inc){
        String oldScore= (String) score.getText();
        int newScore=Integer.parseInt(oldScore)+inc;
        score.setText(""+newScore);





    }
}
