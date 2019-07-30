package com.example.fingertapping;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private String time;
    private String interval;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    1);
        }

        Intent intent = getIntent();

    }


    public void tapTwoClicked(View view) {
        ArrayList<Integer> measures =Lists.newArrayList(0,1,2);
        Collections.shuffle(measures);
        int [] order= new int[3];
        order[0]= measures.get(0);
        order[1]=measures.get(1);
        order[2]= measures.get(2);
        Intent i = new Intent(getBaseContext(), UserDataActivity.class);
        i.putExtra("order", order);
        startActivity(i);
    }


    public void randomClicked(View view) {
        Intent i = new Intent(getBaseContext(), UserDataActivity.class);
        i.putExtra("order", new int[]{1,2,3,4});
        i.putExtra("category", 2);
        startActivity(i);
    }

    public void synchClicked(View view) {
        Intent i = new Intent(getBaseContext(), UserDataActivity.class);
        i.putExtra("order", new int[]{1,2,3,4});
        i.putExtra("category", 1);
        startActivity(i);
    }

    public void classicClicked(View view) {
        Intent i = new Intent(getBaseContext(), UserDataActivity.class);
        i.putExtra("order", new int[]{1,2,3,4});
        i.putExtra("category", 0);
        startActivity(i);
    }

    public void settingsClicked(View view) {
        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(i);
    }
}
