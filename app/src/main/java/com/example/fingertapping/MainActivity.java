package com.example.fingertapping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void tapTwoClicked(View view) {
        Intent i=new Intent(getBaseContext(), UserDataActivity.class);
        startActivity(i);
    }
}
