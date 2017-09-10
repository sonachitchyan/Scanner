package com.example.asus.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
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
    volatile boolean stopWorker;
    String printable;
    BitSet dots;



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
        printable = getIntent().getStringExtra("nameish") + "\n\n";
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
                try {
                    findBT();
                    openBT();
                }
                catch (Exception e){
                    Log.i("kkkjkjk", "lklklklk");
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                final String currentDateandTime = sdf.format(new Date());
                double amount = 0.0;
                for (Data d: datas){
                    amount = amount + (d.getCount() * d.getPrice());
                }
                final double rounded = (double) Math.round(amount * 100) / 100;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main2Activity.this);
                alertDialog.setTitle("Տպել");
                alertDialog.setMessage("Տպել");
                alertDialog.setPositiveButton("ԱՅՈ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       printable =rounded + " AMD\n\n" + currentDateandTime + "\n";
                        try {

                            sendData(printable);

                        }
                        catch (Exception e){
                            Log.i("aaaa", "aaaaa");
                        }
                    }
                });
                alertDialog.setNegativeButton("ՈՉ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertDialog.show();




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

        }  catch (Exception e) {
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
                                                encodedBytes, "UNICODE");
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

            msg += "\n";

            mmOutputStream.write(msg.getBytes());
            print_image(DWriter.createBarCode(getIntent().getStringExtra("nameish"), BarcodeFormat.CODE_128, 100, 100));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void print_image(Bitmap bmp) throws IOException {
            convertBitmap(bmp);
            mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_12);

            int offset = 0;
            while (offset < bmp.getHeight()) {
                mmOutputStream.write(PrinterCommands.SELECT_BIT_IMAGE_MODE);
                for (int x = 0; x < bmp.getWidth(); ++x) {

                    for (int k = 0; k < 3; ++k) {

                        byte slice = 0;
                        for (int b = 0; b < 8; ++b) {
                            int y = (((offset / 8) + k) * 8) + b;
                            int i = (y * bmp.getWidth()) + x;
                            boolean v = false;
                            if (i < dots.length()) {
                                v = dots.get(i);
                            }
                            slice |= (byte) ((v ? 1 : 0) << (7 - b));
                        }
                        mmOutputStream.write(slice);
                    }
                }
                offset += bmp.getHeight();
                mmOutputStream.write(PrinterCommands.FEED_LINE);
                mmOutputStream.write(PrinterCommands.FEED_LINE);
                mmOutputStream.write(PrinterCommands.FEED_LINE);
                mmOutputStream.write(PrinterCommands.FEED_LINE);
                mmOutputStream.write(PrinterCommands.FEED_LINE);
                mmOutputStream.write(PrinterCommands.FEED_LINE);
            }
            mmOutputStream.write(PrinterCommands.SET_LINE_SPACING_30);
    }

    public String convertBitmap(Bitmap inputBitmap) {

        int mWidth = inputBitmap.getWidth();
        int mHeight = inputBitmap.getHeight();

        convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
        return "ok";

    }

    private void convertArgbToGrayscale(Bitmap bmpOriginal, int width,
                                        int height) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        dots = new BitSet();
        try {

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    // get one pixel color
                    pixel = bmpOriginal.getPixel(y, x);

                    // retrieve color of all channels
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);
                    // take conversion up to one single value by calculating
                    // pixel intensity.
                    R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                    // set bit into bitset, by calculating the pixel's luma
                    if (R < 55) {
                        dots.set(k);//this is the bitset that i'm printing
                    }
                    k++;
                }


            }


        } catch (Exception e) {
            // TODO: handle exception
            Log.e("sss", e.toString());
        }
    }

}