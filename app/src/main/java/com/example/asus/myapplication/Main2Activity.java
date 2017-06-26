package com.example.asus.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private ArrayList<Data> datas, data_search;
    private RecAdapter recAdapter;
    private RecyclerView recyclerView;
    private Gson gson = new Gson();
    private Button clear;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recyclerView = (RecyclerView) findViewById(R.id.rec);
        searchView = (SearchView) findViewById(R.id.search);
        data_search =new ArrayList<>();
        searchView.setActivated(true);
        searchView.setQueryHint("Փնտրել");
        searchView.onActionViewExpanded();

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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                    data_search = new ArrayList<Data>();
                    for (Data d : datas) {
                        if (d.getName().equals(query)) {
                            data_search.add(d);
                            recAdapter.setDataList(data_search);
                            recyclerView.setAdapter(recAdapter);
                        }
                    }

                 }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    recAdapter.setDataList(datas);
                    recyclerView.setAdapter(recAdapter);
                }
                return true;
            }
        });
    }
}
