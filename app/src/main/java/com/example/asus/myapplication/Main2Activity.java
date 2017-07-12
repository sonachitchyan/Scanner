package com.example.asus.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
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
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private List<Data> datas, data_search;
    private RecAdapter recAdapter;
    private RecyclerView recyclerView;
    private Gson gson = new Gson();
    private Button clear, back;
    private SearchView searchView;
    IntentFilter intentFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
    BroadcastReceiver b;
    String text;
    List<Data> datalist;
    DataBaseHandler db;
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
        datalist = new ArrayList<>();
        datas = new ArrayList<>();
        db = new DataBaseHandler(this);
        if (savedInstanceState!=null){
            text = savedInstanceState.getString("infoo");
        }
        for (Data d: db.getAllInfo()){
            if (d.getCount()!=0){
                datas.add(d);
            }
        }


        b=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String status = intent.getStringExtra("SCAN_STATE");
                String bar = "";
                if ("ok".equals(status)) {
                    bar = intent.getStringExtra("SCAN_BARCODE1");
                    searchView.setQuery(bar, false);
                }
            }
        };
        registerReceiver(b, intentFilter);

        final Intent intent = getIntent();
        text = intent.getStringExtra("data");
        JsonReader jsonReader = new JsonReader(new StringReader(text));
        jsonReader.setLenient(true);
        //datas = gson.fromJson(jsonReader, new TypeToken<ArrayList<Data>>(){}.getType());
        recAdapter = new RecAdapter(datas, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recAdapter);
        recyclerView.invalidate();
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = new ArrayList<>();
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
                        if (d.getBarcode().equals(query)) {
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


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(b);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("infoo", gson.toJson(recAdapter.getDataList()));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (int i=0; i<recyclerView.getAdapter().getItemCount(); i++){
            Data data = db.getInfoByCode(recAdapter.getDataList().get(i).getCode());
            datalist.add(data);
        }
        datas = datalist;
        super.onConfigurationChanged(newConfig);
    }
}
