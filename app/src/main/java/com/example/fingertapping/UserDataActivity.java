package com.example.fingertapping;

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
    private  EditText age;
    private EditText additionalInfo;
    private Spinner gender;
    private Spinner parkinsonL;
    private Button startMeasure;
    private Spinner dominantHand;
    private Spinner measuredHand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        alert=(TextView) findViewById(R.id.alertLabel);
        id = (EditText) findViewById(R.id.id);
        age = (EditText) findViewById(R.id.age);
        additionalInfo = (EditText) findViewById(R.id.additionalInfo);
        gender = (Spinner) findViewById(R.id.gender);
        parkinsonL = (Spinner) findViewById(R.id.parkinsonL);
        startMeasure = (Button) findViewById(R.id.startMeasure);
        dominantHand = (Spinner) findViewById(R.id.dominantHand);
        measuredHand = (Spinner) findViewById(R.id.measuredHand);
    }

    public void startFingerTapping(View view) {
        if(age.getText().toString().isEmpty()|| gender.getSelectedItem().toString().isEmpty()|| parkinsonL.getSelectedItem().toString().isEmpty()
        || measuredHand.getSelectedItem().toString().isEmpty() || dominantHand.toString().isEmpty()){
            alert.setText("Wprowadź wiek, płeć, czy jestes chory, dominująca rękę i rękę którą przeprowadzany jest pomiar.");
        }else{
            ArrayList<String> data = new ArrayList<String>();
            data.add(id.getText().toString());
            data.add(age.getText().toString());
            data.add(gender.getSelectedItem().toString());
            data.add(parkinsonL.getSelectedItem().toString());
            data.add(dominantHand.getSelectedItem().toString());
            data.add(measuredHand.getSelectedItem().toString());
            data.add(additionalInfo.getText().toString());
            Intent i=new Intent(getBaseContext(), FingerTappingActivity.class);
            i.putExtra("UserData", data);
            startActivity(i);
        }

    }
}
