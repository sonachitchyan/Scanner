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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private RadioButton radio_barcode, radio_code, radio_article;
    private Button clear, add;
    private EditText barcode, count;
    private TextView name_text, count_text, value_text;
    private String hints[] = {"Barcode"};
    private List<Data> dataList = new ArrayList<>();
    private BroadcastReceiver receiver;
    IntentFilter intentfilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
    Gson gson = new Gson();
    String a = null;
    String filename = "export.txt", filetext;
    File file;
    List<Data> dataList1 = null;

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
        file = filemaker(filename);
        filetext = filereader(filename);
        android.util.JsonReader jsonReader = new android.util.JsonReader(new StringReader(filetext));
        jsonReader.setLenient(true);
        dataList = gson.fromJson(String.valueOf(jsonReader), new TypeToken<ArrayList<Data>>() {
        }.getType());


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
                barcode.setText("");
                count.setText("");
                if (file != null) {
                    file.delete();
                }

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
                    break;
                case R.id.radio_code:
                    barcode.setHint("Code");
                    hints[0] = "Code";
                    barcode.setText("");
                    barcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case R.id.radio_barcode:
                    barcode.setHint("Barcode");
                    hints[0] = "Barcode";
                    barcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
        }
    }


    class Reader extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            switch (hints[0]) {
                case "Barcode":
                    if (!barcode.getText().toString().equals("")) {
                        for (Data d : dataList) {
                            if (d.getBarcode().equals(barcode.getText().toString())) {
                                int temp = d.getCount();
                                if (count.getText().toString().equals("")) {
                                    d.setCount(temp + 1);
                                } else {
                                    d.setCount(temp + Integer.parseInt(count.getText().toString()));
                                }
                            }
                        }
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
                            }
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
                            }
                        }

                    }
                    break;

            }

            a = gson.toJson(dataList);
            filewriter(file, filename);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String t = filereader(filename);
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(t));
            reader.setLenient(true);

            dataList1 = gson.fromJson(reader, new TypeToken<ArrayList<Data>>() {
            }.getType());

            if (!dataList1.isEmpty()) {
                switch (hints[0]) {
                    case "Barcode":
                        for (Data d : dataList1) {
                            if (d.getBarcode().equals(barcode.getText().toString())) {
                                name_text.setText(d.getName());
                                count_text.setText("" + d.getCount() + "/" + d.getCount_db());
                                value_text.setText("" + d.getPrice());
                                break;
                            }
                        }
                        break;
                    case "Code":
                        for (Data d : dataList1) {
                            if (d.getCode() == Integer.parseInt(barcode.getText().toString())) {
                                name_text.setText(d.getName());
                                count_text.setText("" + d.getCount() + "/" + d.getCount_db());
                                value_text.setText("" + d.getPrice());
                                Toast.makeText(MainActivity.this, "a", Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                Toast.makeText(MainActivity.this, "b", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case "Article":
                        for (Data d : dataList1) {
                            if (d.getArticle().equals(barcode.getText().toString())) {
                                name_text.setText(d.getName());
                                count_text.setText("" + d.getCount() + "/" + d.getCount_db());
                                value_text.setText("" + d.getPrice());
                                break;
                            }
                        }
                        break;

                }
            }
            barcode.setText("");
            count.setText("");
        }

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

    public File filemaker(String exportname) {
        String importname = "import.txt";
        File result = null;
        if (isExternalStorageWritable()) {
            result = new File(Environment.getExternalStorageDirectory(), exportname);
        } else {
            Toast.makeText(MainActivity.this, "failed to connect", Toast.LENGTH_SHORT).show();
        }
        String text = "";
        List<Data> datas = null;
        text = filereader(exportname);
        Gson gson = new Gson();
        android.util.JsonReader jsonReader = new android.util.JsonReader(new StringReader(text));
        jsonReader.setLenient(true);
        datas = gson.fromJson(String.valueOf(jsonReader), new TypeToken<ArrayList<Data>>() {
        }.getType());
        for (Data d : datas) {
            d.setCount(0);
        }
        String finaltext = gson.toJson(datas);
        filewriter(result, finaltext);
        return result;
    }


    public String filereader(String filename) {
        String result = "";
        try {
            InputStream inputStream = openFileInput(filename);

            if (inputStream != null) {
                File myFile = new File(Environment.getExternalStorageDirectory().getPath() + filename);
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(
                        new InputStreamReader(fIn));
                String strline = "";
                StringBuffer stringBuffer = new StringBuffer();
                while ((strline = myReader.readLine()) != null) {
                    result = result + strline + "\n";
                }
                myReader.close();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    public void filewriter(File file, String text) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
            outputStreamWriter.write(text);
            outputStreamWriter.flush();
            fileOutput.getFD().sync();
            outputStreamWriter.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Lost connection", Toast.LENGTH_SHORT).show();
        }

    }
}