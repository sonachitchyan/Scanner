package com.example.asus.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private ArrayList<Data> datas;
    private RecAdapter recAdapter;
    private RecyclerView recyclerView;
    private Gson gson = new Gson();
    private Button clear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recyclerView = (RecyclerView) findViewById(R.id.rec);
        final Intent intent = getIntent();
        String text = intent.getStringExtra("data");
        JsonReader jsonReader = new JsonReader(new StringReader(text));
        jsonReader.setLenient(true);
        datas = gson.fromJson(jsonReader, new TypeToken<ArrayList<Data>>(){}.getType());
        recAdapter = new RecAdapter(datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recAdapter);
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = new ArrayList<Data>();
                Intent intent1 = new Intent(Main2Activity.this, MainActivity.class);
                intent1.putExtra("cleared", true);
                startActivity(intent1);
            }
        });
    }
}
