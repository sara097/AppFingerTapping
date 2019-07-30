package com.example.fingertapping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class UserDataActivity extends AppCompatActivity {

    private TextView alert;
    private EditText id;
    private EditText name;
    private EditText lastName;
    private EditText age;
    private EditText additionalInfo;
    private Spinner gender;
    private Spinner parkinsonL;
    private Spinner dominantHand;
    private Spinner measuredHand;
    private int[] order=new int[]{0,1,2};
    private int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        Intent intent = getIntent();
        order = intent.getExtras().getIntArray("order");
        category=intent.getExtras().getInt("category");
        alert = (TextView) findViewById(R.id.alertLabel);
        id = (EditText) findViewById(R.id.ident);
        name = (EditText) findViewById(R.id.name);
        lastName = (EditText) findViewById(R.id.lastName);
        age = (EditText) findViewById(R.id.age);
        additionalInfo = (EditText) findViewById(R.id.additionalInfo);
        gender = (Spinner) findViewById(R.id.gender);
        parkinsonL = (Spinner) findViewById(R.id.parkinsonL);
        dominantHand = (Spinner) findViewById(R.id.dominantHand);
        measuredHand = (Spinner) findViewById(R.id.measuredHand);
    }

    @SuppressLint("SetTextI18n")
    public void startFingerTapping(View view) {
        if (age.getText().toString().isEmpty() ||
                gender.getSelectedItem().toString().isEmpty() ||
                parkinsonL.getSelectedItem().toString().isEmpty() ||
                measuredHand.getSelectedItem().toString().isEmpty() ||
                dominantHand.toString().isEmpty() ||
                id.getText().toString().isEmpty() ||
                name.getText().toString().isEmpty() ||
                lastName.getText().toString().isEmpty()) {
            alert.setText("Wprowadź dane.");
        } else {
            ArrayList<String> data = new ArrayList<>();
            data.add(name.getText().toString().substring(0,2).toUpperCase()+
                    lastName.getText().toString().substring(0,2).toUpperCase()+
                    id.getText().toString());
            data.add(age.getText().toString());
            data.add(gender.getSelectedItem().toString());
            data.add(parkinsonL.getSelectedItem().toString());
            data.add(dominantHand.getSelectedItem().toString());
            data.add(measuredHand.getSelectedItem().toString());
            data.add(additionalInfo.getText().toString());
            Intent i = new Intent(getBaseContext(), FingerTappingActivity.class);
            i.putExtra("UserData", data);
            i.putExtra("order", order);
            i.putExtra("category", category);
            startActivity(i);
        }

    }
}
