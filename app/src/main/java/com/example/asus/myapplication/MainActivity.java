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
import android.util.Log;
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
import java.io.IOException;
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
    private BroadcastReceiver receiver;
    Gson gs = new Gson();
    IntentFilter intentfilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
    DataBaseHandler db = new DataBaseHandler(this);


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

        String text = filereader("import.txt");
        JsonReader jsonReader = new JsonReader(new StringReader(text));
        jsonReader.setLenient(true);
        final List<Data> dataList = gs.fromJson(jsonReader, new TypeToken<ArrayList<Data>>(){
        }.getType());
        if (!dataList.isEmpty()){
            for (Data d: dataList){
                d.setCount(0);
                db.addInfo(d);
            }
        }


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

        Data data = new Data();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            boolean def = false;
            if (count.getText().toString().equals("")){
                def = true;
            }
            int temp = 1;
            switch (hints[0]){
                case "Barcode":
                    data = db.getInfoByBarcode(barcode.getText().toString());
                    temp = data.getCount();
                    if (def){
                        temp++;
                    }
                    else{
                        temp+= Integer.parseInt(count.getText().toString());
                    }
                    break;
                case "Code":
                    data = db.getInfoByCode(barcode.getText().toString());
                    temp = data.getCount();
                    if (def){
                        temp++;
                    }
                    else{
                        temp+= Integer.parseInt(count.getText().toString());
                    }
                    break;
                case "Article":
                    data = db.getInfoByArticle(Integer.parseInt(barcode.getText().toString()));
                    temp = data.getCount();
                    if (def){
                        temp++;
                    }
                    else{
                        temp+= Integer.parseInt(count.getText().toString());
                    }
                    break;
            }
            data.setCount(temp);

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
            switch (hints[0]){
                case "Barcode":
                    db.updateInfoByBarcode(data);
                    break;
                case "Code":
                    db.updateInfoByCode(data);
                    break;
                case "Article":
                    db.updateInfoByArticle(data);
                    break;
            }
            name_text.setText(data.getName());
            count_text.setText(data.getCount() + "/" + data.getCount_db());
            value_text.setText(String.valueOf(data.getPrice()));
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
        File result = null;
        if (isExternalStorageWritable()) {
            result = new File(Environment.getExternalStorageDirectory().getPath() + exportname);

        } else {
            Toast.makeText(MainActivity.this, "failed to connect", Toast.LENGTH_SHORT).show();
        }
        String finaltext ="[]";
        filewriter(result, finaltext);
        return result;
    }


    public String filereader(String filename) {


        File extStore = Environment.getExternalStorageDirectory();
        String path = extStore.getAbsolutePath() + "/" + filename;
        Log.i("ExternalStorageDemo", "Read file: " + path);

        String s = "";
        String fileContent = "";
        try {
            File myFile = new File(path);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));

            while ((s = myReader.readLine()) != null) {
                fileContent += s + "\n";
            }
            myReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;

    }


    public void filewriter(File file, String text) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
            outputStreamWriter.write(text);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch(IOException e){
            Toast.makeText(this, "a", Toast.LENGTH_SHORT).show();
        }


    }
}