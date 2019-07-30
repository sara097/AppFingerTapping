package com.example.fingertapping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class WaitForMeasure extends AppCompatActivity {

    TextView left;
    int [] order;
    private ArrayList<CharSequence> userData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_measure);
        Intent intent = getIntent();
        order = intent.getExtras().getIntArray("order");
        userData = intent.getCharSequenceArrayListExtra("UserData");
        left=(TextView) findViewById(R.id.measuresLeft);
        left.setText("Zostało badań: "+order.length);
        System.out.println("AAAAAAA "+ order.length);
        if(order.length==0 || order.length>3){
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
}
