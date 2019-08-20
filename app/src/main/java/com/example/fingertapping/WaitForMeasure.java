package com.example.fingertapping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class WaitForMeasure extends AppCompatActivity implements Initializable {

    TextView left;
    int[] order;
    private ArrayList<CharSequence> userData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_measure);
        initializeActivityElements();

        Intent intent = getIntent();
        order = intent.getExtras().getIntArray("order");
        userData = intent.getCharSequenceArrayListExtra("UserData");

        left.setText("Zostało badań: " + order.length);
        if (order.length == 0 || order.length > 6) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
        }
    }

    public void toMeasureClicked(View view) {
        Intent i = new Intent(getBaseContext(), FingerTappingActivity.class);
        i.putExtra("order", order);
        i.putExtra("UserData", userData);
        startActivity(i);
    }

    @Override
    public void initializeActivityElements() {
        left = (TextView) findViewById(R.id.measuresLeft);
    }
}
