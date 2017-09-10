package com.example.asus.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private RadioButton radio_barcode;
    private EditText barcode, count, number;
    private TextView name_text, result_text, price_text;
    private Button undo, zero;
    private String hints[] = {"Barcode"};
    private BroadcastReceiver receiver;
    private LinearLayout linearLayout;
    private String temp_code = "";
    private Data temp_data;
    Gson gs = new Gson();
    IntentFilter intentfilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
    DataBaseHandler db = new DataBaseHandler(this);
    private List<Data> dataList, dataList1;
    String ex_client;
    String prefix;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radio_barcode = (RadioButton) findViewById(R.id.radio_barcode);
        barcode = (EditText) findViewById(R.id.barcode);
        number = (EditText) findViewById(R.id.file_name);
        count = (EditText) findViewById(R.id.count);
        name_text = (TextView) findViewById(R.id.name_text);
        result_text = (TextView) findViewById(R.id.results);
        undo = (Button) findViewById(R.id.undo);
        linearLayout = (LinearLayout) findViewById(R.id.main_act);
        price_text = (TextView) findViewById(R.id.price_text);
        zero = (Button) findViewById(R.id.zero);
        prefix = getIntent().getStringExtra("prefix");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        Intent intent_broad = new Intent("ACTION_BAR_SCANCFG");
        intent_broad.putExtra("EXTRA_SCAN_MODE", 3);
        this.sendBroadcast(intent_broad);


        if (getIntent().getBooleanExtra("cleared", false)) {
            dataList1 = new ArrayList<>();
        }
        if (!db.isEmpty()){
            db.deletAll();
        }


        number.requestFocus();
        number.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    ex_client = number.getText().toString();
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+prefix+"_"+ex_client+"export.json");
                    if (file.exists()){
                        JsonReader jsonReader = new JsonReader(new StringReader(filereader(prefix + "_" + ex_client+"export.json")));
                        dataList1 = gs.fromJson(jsonReader, new TypeToken<ArrayList<Data>>(){}.getType());

                        for(Data d: dataList1){
                            db.updateInfoByBarcode(d);
                        }
                    }
                    else {
                        dataList1 = new ArrayList<>();
                        db.makeAllZero();
                    }
                    Async async = new Async();
                    async.execute();
                    barcode.requestFocus();
                    name_text.setText("");
                    result_text.setText("");
                    price_text.setText("");
                    return true;
                }
                return false;
            }
        });


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String status = intent.getStringExtra("SCAN_STATE");
                String bar = "";
                if (hints[0].equals("Barcode")) {
                    if ("ok".equals(status) && hints[0].equals("Barcode")) {
                        bar = intent.getStringExtra("SCAN_BARCODE1");
                        barcode.setText(bar);
                        dataList = db.getAllInfo();
                        for (Data d: dataList){
                            if (d.getBarcode().equals(barcode.getText().toString())){
                                name_text.setText(d.getName());
                                price_text.setText(String.valueOf(d.getPrice()));
                                result_text.setText(d.getCode() + "\n" +"\n" + d.getCount());
                                temp_code = d.getCode();
                                break;
                            }
                        }
                        new Aaaa().execute();

                        count.requestFocus();
                        barcode.setEnabled(false);
                        unregisterReceiver(receiver);

                    } else {
                        Toast.makeText(context, "Փորձեք կրկին", Toast.LENGTH_SHORT).show();
                        barcode.setText("");
                    }
                }

            }
        };

        this.registerReceiver(receiver, intentfilter);

        count.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Reader reader = new Reader();
                    reader.execute();
                    return true;
                }
                return false;
            }
        });
        barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    boolean a = false;
                    if (!barcode.getText().toString().equals("")) {
                        dataList = db.getAllInfo();
                        AAA:
                        for (Data d : dataList) {
                            switch (hints[0]) {
                                case "Barcode":
                                    if (d.getBarcode().equals(barcode.getText().toString())) {
                                        name_text.setText(d.getName());
                                        price_text.setText(String.valueOf(d.getPrice()));
                                        result_text.setText(d.getCode() + "\n" + "\n" + d.getCount());
                                        temp_code = d.getCode();
                                        a = true;
                                        break AAA;

                                    }
                                    break;
                                case "Article":
                                    if (d.getArticle().equals(barcode.getText().toString())) {
                                        name_text.setText(d.getName());
                                        price_text.setText(String.valueOf(d.getPrice()));
                                        result_text.setText(d.getCode() + "\n" + "\n" + d.getCount());
                                        temp_code = d.getCode();
                                        a = true;
                                        break AAA;

                                    }
                                    break;
                                case "Code":
                                    if (d.getCode().equals(barcode.getText().toString())) {
                                        name_text.setText(d.getName());
                                        price_text.setText(String.valueOf(d.getPrice()));
                                        result_text.setText(d.getCode() + "\n" + "\n" + d.getCount());
                                        temp_code = d.getCode();
                                        a = true;
                                        break AAA;

                                    }
                                    break;
                            }
                            barcode.setEnabled(false);
                        }
                        if (!a) {
                            Toast.makeText(MainActivity.this, "Առկա չէ բազայում", Toast.LENGTH_SHORT).show();
                            barcode.setText("");

                        } else {
                            new Aaaa().execute();
                        }
                        return true;
                    } else {
                        Toast.makeText(MainActivity.this, "Ոչինչ մուտքագրված չէ", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number.setText("");
                barcode.setText("");
                count.setText("");
                dataList1 = new ArrayList<Data>();
                db.makeAllZero();
                name_text.setText("");
                price_text.setText("");
                result_text.setText("");
                number.requestFocus();
            }
        });


        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BREAKING_POINT:
                if (temp_data!=null){
                    for (Data d: dataList1){
                        if (d.getBarcode().equals(temp_data.getBarcode()) && d.getCount()>=temp_data.getCount()){
                            int t = d.getCount();
                            t-= temp_data.getCount();
                            d.setCount(t);
                            db.changeCountForBarcode(d.getBarcode(), t);
                            name_text.setText(temp_data.getName());
                            result_text.setText(d.getCode() + "\n" + "\n" +
                                    d.getCount());
                            price_text.setText(String.valueOf(d.getPrice()));
                            break BREAKING_POINT;
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Հրամանը սխալ է", Toast.LENGTH_SHORT).show();
                }
            }
        });
        undo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    return true;
                }
                return false;
            }
        });


        radio_barcode.setChecked(true);
        count.setCursorVisible(false);
        barcode.setCursorVisible(false);
        number.setCursorVisible(false);

    }


    public void onRadioButtonChecked(View view) {
        boolean c = ((RadioButton) view).isChecked();
        if (c) {
            switch (view.getId()) {
                case R.id.radio_article:
                    barcode.setHint("արտիկուլ");
                    barcode.setText("");
                    hints[0] = "Article";
                    barcode.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case R.id.radio_code:
                    barcode.setHint("կոդ");
                    hints[0] = "Code";
                    barcode.setText("");
                    barcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case R.id.radio_barcode:
                    barcode.setHint("շտրիխ");
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
            if (count.getText().toString().equals("")) {
                def = true;
            }
            if (!barcode.getText().toString().equals("")) {
                int temp = 0;
                switch (hints[0]) {
                    case "Barcode":
                        temp_data = db.getInfoByCode(temp_code);
                        data = db.getInfoByBarcode(barcode.getText().toString());
                        if (data != null) {
                            temp = data.getCount();
                            if (def) {
                                temp++;
                                temp_data.setCount(1);
                            } else {
                                temp += Integer.parseInt(count.getText().toString());
                                temp_data.setCount(Integer.parseInt(count.getText().toString()));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Առկա չէ բազայում", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Code":
                        temp_data = db.getInfoByCode(temp_code);
                        data = db.getInfoByCode(temp_code);
                        if (data != null) {
                            temp = data.getCount();
                            if (def) {
                                temp++;
                                temp_data.setCount(1);
                            } else {
                                temp += Integer.parseInt(count.getText().toString());
                                temp_data.setCount(Integer.parseInt(count.getText().toString()));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Առկա չէ բազայում", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Article":
                        temp_data = db.getInfoByArticle(temp_code);
                        data = db.getInfoByArticle(temp_code);
                        if (data != null) {
                            temp = data.getCount();
                            if (def) {
                                temp++;
                                temp_data.setCount(1);
                            } else {
                                temp += Integer.parseInt(count.getText().toString());
                                temp_data.setCount(Integer.parseInt(count.getText().toString()));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Առկա չէ բազայում", Toast.LENGTH_SHORT).show();
                            barcode.requestFocus();
                        }
                        break;
                }
                if (data != null) {
                    data.setCount(temp);
                    boolean ex = false;
                    if (dataList1.isEmpty()) {
                        dataList1.add(data);
                    } else {
                        for (Data d : dataList1) {
                            switch (hints[0]) {
                                case "Barcode":
                                    if (d.getBarcode().equals(barcode.getText().toString())) {
                                        d.setCount(temp);
                                        ex = true;
                                    }
                                    break;
                                case "Code":
                                    if (d.getCode().equals(barcode.getText().toString())) {
                                        d.setCount(temp);
                                        ex = true;
                                    }
                                    break;
                                case "Article":
                                    if (d.getArticle().equals(barcode.getText().toString())) {
                                        d.setCount(temp);
                                        ex = true;
                                    }
                                    break;
                            }

                        }
                        if (!ex) {
                            dataList1.add(data);
                        }
                    }
                } else {
                    barcode.setText("");
                    count.setText("");
                }

            }

        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (data != null) {
                if (!barcode.getText().toString().equals("")) {
                    switch (hints[0]) {
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
                    result_text.setText(data.getCode() +"\n" + "\n" +
                            data.getCount());
                    price_text.setText(String.valueOf(data.getPrice()));
                    String info = gs.toJson(dataList1);
                    String info_2 = forSergey(info);
                    String file_path_name = prefix +"_"+ ex_client;
                    filewriter(file_path_name + "export.json", info_2);
                    barcode.setText("");
                    count.setText("");
                    barcode.requestFocus();
                    registerReceiver(receiver, intentfilter);
                    barcode.setEnabled(true);
                } else {
                    Toast.makeText(MainActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public String filereader(String filename) {
        File extStore = Environment.getExternalStorageDirectory();
        String path = extStore.getAbsolutePath() + "/" + filename;
        Log.i("ExternalStorageDemo", "Read file: " + path);


        String s = "";
        String fileContent = "";
        StringBuilder ss = new StringBuilder();
        try {
            File myFile = new File(path);
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));

            while ((s = myReader.readLine()) != null) {
                ss.append(s);
            }
        } catch (Exception e) {

        }
        fileContent = ss.toString();
        return fileContent;

    }


    public void filewriter(String name, String text) {
        if (isExternalStorageWritable()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + name);
                FileOutputStream fileOutput = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
                outputStreamWriter.write(text);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                Toast.makeText(this, "Problem occurred", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Lost connection", Toast.LENGTH_SHORT).show();
        }


    }

    class Async extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            linearLayout.setEnabled(true);
            barcode.requestFocus();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            linearLayout.setEnabled(false);
            fillData();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.listofitems:
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                String info = gs.toJson(dataList1);
                intent.putExtra("data", info);
                intent.putExtra("nameish", prefix + "_" + ex_client);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void fillData(){
        if (db.isEmpty()){
            String text = filereader("import_main.json");
            JsonReader jsonReader = new JsonReader(new StringReader(text));
            jsonReader.setLenient(true);
            dataList = gs.fromJson(jsonReader, new TypeToken<ArrayList<Data>>() {
            }.getType());
            for (Data d : dataList) {
                boolean a = false;
                for (Data dd: dataList1){
                    if (dd.getCode().equals(d.getCode())){
                        d.setCount(dd.getCount());
                        a = true;
                    }
                }
                if (!a){
                    d.setCount(0);
                }
                db.addInfo(d);
            }
        }
        dataList = db.getAllInfo();
    }

    @Override
    public void onBackPressed() {
        if (!sharedPreferences.getString("prefix", "").equals("")){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("ԶԳՈՒՇԱՑՈՒՄ");
            alert.setMessage("Փակել ծրագիրը?");
            alert.setPositiveButton("ԱՅՈ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.finishAffinity();
                }
            });
            alert.setNegativeButton("ՈՉ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alert.show();

        }
    }
    public String forSergey(String data){
        String finall = "";
        char[] d = data.toCharArray();
        for (char dd:d){
            finall = finall + dd;
            if (dd == ','){
                finall = finall + '\n';
            }
        }
        return finall;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentfilter);
    }
    public class Aaaa extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TimeUnit.MILLISECONDS.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            count.requestFocus();
        }
    }
}