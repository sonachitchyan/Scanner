package com.example.asus.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Launcher_Activity extends AppCompatActivity {

    private EditText terminal;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_);
        terminal = (EditText) findViewById(R.id.terminal);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        intent = new Intent(Launcher_Activity.this, MainActivity.class);
        editor = sharedPreferences.edit();

        terminal.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN && sharedPreferences.getString("prefix", "").equals("")) {
                    if (terminal.getText().toString().equals("")) {
                        Toast.makeText(Launcher_Activity.this, "Ոչինչ մուտքագրված չէ", Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra("prefix", terminal.getText().toString());
                        editor.putString("prefix", terminal.getText().toString());
                        editor.commit();
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });
        if (sharedPreferences.getString("prefix", "").equals("")) {
            terminal.setVisibility(View.VISIBLE);
        } else {
            terminal.setVisibility(View.GONE);
            intent.putExtra("prefix", sharedPreferences.getString("prefix", ""));
            startActivity(intent);
        }

    }
}