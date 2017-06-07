package com.example.asus.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {

    private RadioButton radio_barcode, radio_code, radio_article;
    private Button clear, add;
    private EditText barcode, count;
    private TextView name_text, count_text, value_text;
    private String hints[] = {"Barcode"};
    private List<Data> dataList = new ArrayList<>();
    BroadcastReceiver receiver;
    IntentFilter intentfilter = new IntentFilter("nlscan.action.SCANNER_RESULT");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radio_article = (RadioButton) findViewById(R.id.radio_article);
        radio_barcode = (RadioButton) findViewById(R.id.radio_barcode);
        radio_code = (RadioButton) findViewById(R.id.radio_code);
        barcode = (EditText) findViewById(R.id.barcode);
        count = (EditText) findViewById(R.id.count);
        clear = (Button) findViewById(R.id.clear);
        add = (Button) findViewById(R.id.add);
        name_text = (TextView) findViewById(R.id.name_text);
        count_text = (TextView) findViewById(R.id.count_text);
        value_text = (TextView) findViewById(R.id.value_text);


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String status = intent.getStringExtra("SCAN_STATE");
                if (hints[0].equals("Barcode")) {
                    if ("ok".equals(status) && hints[0].equals("Barcode")) {
                        barcode.setText(intent.getStringExtra("SCAN_BARCODE1"));
                    } else {
                        Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
                        barcode.setText("");
                    }
                }
            }
        };

        this.registerReceiver(receiver, intentfilter);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataList = new ArrayList<>();
                count_text.setText("");
                name_text.setText("");
                value_text.setText("");
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reader reader = new Reader();
                reader.execute();
            }
        });

        radio_barcode.setChecked(true);
        barcode.setFocusable(false);
        barcode.setFocusableInTouchMode(false);
        count.setCursorVisible(false);
        barcode.setCursorVisible(false);

    }

    public void onRadioButtonChecked(View view) {
        boolean c = ((RadioButton) view).isChecked();
        if (c) {
            switch (view.getId()) {
                case R.id.radio_article:
                    barcode.setHint("Article");
                    barcode.setText("");
                    hints[0] = "Article";
                    barcode.setInputType(InputType.TYPE_CLASS_TEXT);
                    barcode.setFocusable(true);
                    barcode.setFocusableInTouchMode(true);
                    break;
                case R.id.radio_code:
                    barcode.setHint("Code");
                    hints[0] = "Code";
                    barcode.setText("");
                    barcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    barcode.setFocusable(true);
                    barcode.setFocusableInTouchMode(true);
                    break;
                case R.id.radio_barcode:
                    barcode.setHint("Barcode");
                    barcode.setFocusable(false);
                    barcode.setText("");
                    barcode.setFocusableInTouchMode(false);
                    hints[0] = "Barcode";
                    barcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case 7:
                    if (!((EditText) view).getText().toString().equals("")) {
                        adder(0, view);
                    }
                    break;
                case 10:
                    adder(1, view);
                    break;
                case 29:
                    adder(2, view);
                    break;
                case 32:
                    adder(3, view);
                    break;
                case 35:
                    adder(4, view);
                    break;
                case 38:
                    adder(5, view);
                    break;
                case 41:
                    adder(6, view);
                    break;
                case 44:
                    adder(7, view);
                    break;
                case 48:
                    adder(8, view);
                    break;
                case 51:
                    adder(9, view);
                    break;
                case 67:
                    String text = ((EditText) view).getText().toString();
                    if (!text.equals("")) {
                        text = text.substring(0, text.length() - 1);
                    }
                    ((EditText) view).setText(text);
                    break;
            }
        }
        return true;
    }


    class Reader extends AsyncTask<String, Void, String> {
        Gson gson = new Gson();
        String a = null;
        String filename = "data.txt";
        File file;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Data data = new Data();
            boolean exists = false;
            data.setName("");
            switch (hints[0]) {
                case "Barcode":

                    for (Data d : dataList) {
                        if (d.getBarcode().equals(barcode.getText().toString())) {
                            int temp = d.getCount();
                            if (count.getText().toString().equals("")) {
                                d.setCount(temp + 1);
                            } else {
                                d.setCount(temp + Integer.parseInt(count.getText().toString()));
                            }
                            exists = true;
                        }
                    }
                    if (!exists) {
                        data.setBarcode(barcode.getText().toString());
                        data.setCode(0);
                        data.setArticle("");
                        dataList.add(data);
                    }
                    break;
                case "Code":
                    if (!barcode.getText().equals("")) {
                        for (Data d : dataList) {
                            if (d.getCode() == Integer.parseInt(barcode.getText().toString())) {
                                int temp = d.getCount();
                                if (count.getText().toString().equals("")) {
                                    d.setCount(temp + 1);
                                } else {
                                    d.setCount(temp + Integer.parseInt(count.getText().toString()));
                                }
                                exists = true;
                            }
                        }
                        if (!exists) {
                            data.setBarcode("");
                            data.setCode(Integer.parseInt(barcode.getText().toString()));
                            data.setArticle("");
                            dataList.add(data);
                        }
                    }
                    break;
                case "Article":
                    if (!barcode.getText().equals("")) {
                        for (Data d : dataList) {
                            if (d.getArticle().equals(barcode.getText().toString())) {
                                int temp = d.getCount();
                                if (count.getText().toString().equals("")) {
                                    d.setCount(temp + 1);
                                } else {
                                    d.setCount(temp + Integer.parseInt(count.getText().toString()));
                                }
                                exists = true;
                            }
                        }
                        if (!exists) {
                            data.setBarcode("");
                            data.setCode(0);
                            data.setArticle(barcode.getText().toString());
                            dataList.add(data);
                        }
                    }
                    break;
            }
            if (count.getText().toString().equals("")) {
                data.setCount(1);
            } else {

                data.setCount(Integer.parseInt(count.getText().toString()));
            }

            a = gson.toJson(dataList);
            if(!isExternalStorageWritable()){
                Toast.makeText(MainActivity.this, "failed to connect", Toast.LENGTH_SHORT).show();
            }
            else {
                file = new File(Environment.getExternalStorageDirectory(), a);
            }
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(a.getBytes());
                a = "";
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            try {
                FileInputStream fis = new FileInputStream(filename);
                DataInputStream dis = new DataInputStream(fis);
                BufferedReader buff = new BufferedReader(new InputStreamReader(dis));
                String strline;
                while ((strline = buff.readLine()) != null){
                    a = a + strline;
                }
                dis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Data> dataList1 = gson.fromJson(a, new TypeToken<ArrayList<Data>>() {
            }.getType());
            Data data;
            if (!dataList.isEmpty()) {
                data = dataList.get(dataList1.size() - 1);
                name_text.setText(data.getName());
                count_text.setText("" + data.getCount() + "/" + data.getCount_db());
                value_text.setText("" + data.getPrice());
            }
        }

    }

    public void adder(int i, View v) {
        String a = ((EditText) v).getText().toString();
        a += i;
        ((EditText) v).setText(a);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
