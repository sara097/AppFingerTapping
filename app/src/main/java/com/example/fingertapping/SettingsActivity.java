package com.example.fingertapping;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity implements Initializable {

    private EditText time;
    private EditText interval;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeActivityElements();
        FileOperations fs = new FileOperations(this);
        if (fs.readSettings() != null && fs.readSettings().size() > 0) {
            time.setText(fs.readSettings().get(0).toString());
            interval.setText(fs.readSettings().get(1).toString());
        }
    }

    @Override
    public void initializeActivityElements() {
        time = (EditText) findViewById(R.id.time);
        interval = (EditText) findViewById(R.id.interval);
    }

    public void saveClicked(View view) {
        String settings = time.getText().toString() + ";" + interval.getText().toString();
        FileOperations fs = new FileOperations(this, "settings", settings);
        fs.saveSettingsData(false);

    }
}
