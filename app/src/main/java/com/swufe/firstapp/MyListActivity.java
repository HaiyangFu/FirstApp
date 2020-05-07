package com.swufe.firstapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        ListView listView=findViewById(R.id.itemT);
        String date[] =  {"1111", "2222"};
        ListAdapter adapter = new ArrayAdapter<String>(MyListActivity.this, android.R.layout.simple_expandable_list_item_1,date);
        listView.setAdapter(adapter);
    }
}
