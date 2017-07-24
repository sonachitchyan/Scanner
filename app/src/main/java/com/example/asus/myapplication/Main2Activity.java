package com.example.asus.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.zxing.BarcodeFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Main2Activity extends AppCompatActivity {

    private List<Data> datas, data_search;
    private RecAdapter recAdapter;
    private RecyclerView recyclerView;
    private Gson gson = new Gson();
    private Button back;
    private SearchView searchView;
    IntentFilter intentFilter = new IntentFilter("nlscan.action.SCANNER_RESULT");
    BroadcastReceiver b;
    String text;
    List<Data> datalist;
    DataBaseHandler db;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    BluetoothSocket mmSocket;
    InputStream mmInputStream;
    OutputStream mmOutputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;



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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.print_menu:
                double amount = 0.0;
                for (Data d: datas){
                    amount = amount + (d.getCount() * d.getPrice());
                }
                double rounded = (double) Math.round(amount * 100) / 100;
                Bitmap barc = DWriter.createBarCode(getIntent().getStringExtra("nameish"), BarcodeFormat.CODE_128, 40, 50);
                try {
                    findBT();
                    openBT();
                    String msg = getIntent().getStringExtra("nameish") + "\n\n"+
                            rounded + " AMD\n";
                    sendData(msg);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    sendData(currentDateandTime + "\n\n");

                }
                catch (Exception e){
                    Log.i("aaaa", "aaaaa");
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(Main2Activity.this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {


                    if (device.getName().equals("B1ueTooth printer")) {
                        mmDevice = device;
                        Toast.makeText(Main2Activity.this, "Connected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("8fa87c0d0-afac-11de-8a39-0800200c9a66");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendData(String msg) throws IOException {
        try {

            // the text typed by the user
            msg += "\n";

            mmOutputStream.write(msg.getBytes());


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void sendData(Bitmap bitmap){
        try {
            mmOutputStream.write(bitmap.getRowBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printPhoto(Bitmap bmp) {
        try {
            if(bmp!=null){
                ByteArrayOutputStream output = new ByteArrayOutputStream(bmp.getByteCount());
                bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
                byte[] imageBytes = output.toByteArray();
                sendData(output.toString());
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }




    public static String POS_PrintBMP(Bitmap bitmap) {
        // 先转黑白，再调用函数缩放位图
        ByteArrayOutputStream output = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] imageBytes = output.toByteArray();

// Convert byte[] to string
// I have also tried using Base64.encodeToString(imageBytes, 0);

        String encodedString =Base64.encodeToString(imageBytes, Base64.NO_WRAP);


        return encodedString;
    }
}