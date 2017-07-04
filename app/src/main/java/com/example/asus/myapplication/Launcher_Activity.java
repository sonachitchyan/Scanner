package com.example.asus.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Launcher_Activity extends AppCompatActivity {

    private EditText terminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_);
        terminal = (EditText) findViewById(R.id.terminal);

        terminal.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (terminal.getText().toString().equals("")) {
                        Toast.makeText(Launcher_Activity.this, "Ոչինչ մուտքագրված չէ", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Launcher_Activity.this, MainActivity.class);
                        intent.putExtra("prefix", terminal.getText().toString());
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });
    }
}