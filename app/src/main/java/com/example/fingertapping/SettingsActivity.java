package com.example.fingertapping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    EditText time;
    EditText interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        time = (EditText) findViewById(R.id.time);
        interval = (EditText) findViewById(R.id.interval);
    }

    public void saveClicked(View view) {
        String settings = time.getText().toString() + ";" + interval.getText().toString();
        FileSave fs = new FileSave(this, "settings", settings);
        fs.saveData(false);

    }
}
