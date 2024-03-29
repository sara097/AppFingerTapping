package com.example.fingertapping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class WaitForMeasure extends AppCompatActivity implements Initializable {

    private TextView left;
    private int[] order;
    private ArrayList<CharSequence> userData = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_measure);
        initializeActivityElements();

        Intent intent = getIntent();
        order = intent.getExtras().getIntArray("order");
        userData = intent.getCharSequenceArrayListExtra("UserData");

        left.setText(getString(R.string.measurementLeft) + order.length);
        if (order.length == 0 || order.length > 6) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            finish();
            startActivity(i);
        }
    }

    public void toMeasureClicked(View view) {
        Intent i = new Intent(getBaseContext(), FingerTappingActivity.class);
        i.putExtra("order", order);
        i.putExtra("UserData", userData);
        finish();
        startActivity(i);
    }

    @Override
    public void initializeActivityElements() {
        left = (TextView) findViewById(R.id.measuresLeft);
    }
}
