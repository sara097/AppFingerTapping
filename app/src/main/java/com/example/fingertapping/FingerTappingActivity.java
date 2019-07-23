package com.example.fingertapping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class FingerTappingActivity extends AppCompatActivity {

    private static final String TAG = "FingerTapping";
    private long firstTime;
    private float width;
    private TextView info;
    private StringBuilder data = new StringBuilder(); //string builder przechowujacy dane, ktore mozna nastepnie zapisac do pliku
    private int counter = 0;

    private Map<String, Object> measuredData = new HashMap<>();
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<Integer> sideValue = new ArrayList<>(); // 0 - uniesienie paca, 10 - prawa strona, -10 - lewa strona
    private ArrayList<Float> distanceLeft = new ArrayList<>();

    private ArrayList<Float> distanceRight = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private float centreXR;
    private float centreYR;
    private float centreXL;
    private float centreYL;
    private ArrayList<CharSequence> userData = new ArrayList<>();
    private ArrayList<Integer> whichIsShown = new ArrayList<>();
    private ImageView leftAim;
    private ImageView rightAim;
    private TextView twoTapInstruction;
    private int secondsPass = 0;


    Handler handler = new Handler();
    int delay = 1000; //milliseconds


    void aimDisplay() {
        flag = !flag;
        if (flag) twoTapInstruction.setTextColor(Color.BLUE);
        else twoTapInstruction.setTextColor(Color.BLACK);
        rightAim.setVisibility(View.INVISIBLE);
        leftAim.setVisibility(View.INVISIBLE);

        Random r = new Random();
        int i1 = r.nextInt(2);
        if (i1 == 1) { //lewa
            leftAim.setVisibility(View.VISIBLE);
            whichIsShown.add(-1);
        } else { //prawa

            rightAim.setVisibility(View.VISIBLE);
            whichIsShown.add(1);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_tap);
        Intent intent = getIntent();
        userData = intent.getCharSequenceArrayListExtra("UserData");
        info = (TextView) findViewById(R.id.twoTapInfo);
        leftAim = (ImageView) findViewById(R.id.leftAim);
        rightAim = (ImageView) findViewById(R.id.rightAim);
        twoTapInstruction = (TextView) findViewById(R.id.twoTapInstruction);
        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setOnTouchListener(handleTouch);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        int[] i = new int[2];
        rightAim.getLocationInWindow(i);
        centreXR = i[0] + (rightAim.getWidth() / 2);
        centreYR = i[1] - (rightAim.getHeight() / 4);
        leftAim.getLocationInWindow(i);
        centreXL = i[0] + (leftAim.getWidth() / 2);
        centreYL = i[1] - (leftAim.getHeight() / 4);

        handler.postDelayed(new Runnable(){
            public void run(){
                if(counter>1){

                    if (secondsPass < 20) {

                        if (secondsPass % 2 == 0) {
                            aimDisplay();
                        } else {
                            rightAim.setVisibility(View.INVISIBLE);
                            leftAim.setVisibility(View.INVISIBLE);
                        }
                        secondsPass++;
                        if (secondsPass == 19) endOfMeasure();
                    }
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }



    private float calculateDist(float centreX, float centreY, float x, float y) {
        return (float) Math.sqrt(Math.pow((x - centreX), 2) + Math.pow((y - centreY), 2));
    }

    private boolean flag = true;
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (counter == 1) {
                leftAim.setVisibility(View.INVISIBLE);
                rightAim.setVisibility(View.INVISIBLE);
            }

            long time;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getX();
                    float y = event.getY();

                    //wziac czas dotkniecia i po ktorej stronie
                    Date date = new Date();
                    if (counter == 0) {
                        firstTime = date.getTime();
                        time = 0L;
                        times.add(time);
                        if (x > (width / 2)) { //prawa strona
                            float distance = calculateDist(centreXR, centreYR, x, y);
                            distanceRight.add(distance);
                            distanceLeft.add((float) -1);
                            String toData = time + ";" + 10 + ";" + distance + ";" + "-1" + "!";
                            data.append(toData);
                            sideValue.add(10);

                        } else { //lewa strona
                            float distance = calculateDist(centreXL, centreYL, x, y);
                            distanceLeft.add(distance);
                            distanceRight.add((float) -1);
                            String toData = time + ";" + "-10" + ";" + "-1" + ";" + distance + "!";
                            data.append(toData);
                            sideValue.add(-10);
                        }
                    } else {
                        time = date.getTime() - firstTime;
                        times.add(time);
                        if (x > (width / 2)) { //prawa strona
                            float distance = calculateDist(centreXR, centreYR, x, y);
                            distanceRight.add(distance);
                            distanceLeft.add((float) -1);
                            String toData = time + ";" + 10 + ";" + distance + ";" + "-1" + "!";
                            data.append(toData);
                            sideValue.add(10);
                        } else { //lewa strona
                            float distance = calculateDist(centreXL, centreYL, x, y);
                            distanceLeft.add(distance);
                            distanceRight.add((float) -1);
                            String toData = time + ";" + "-10" + ";" + "-1" + ";" + distance + "!";
                            data.append(toData);
                            sideValue.add(-10);
                        }
                    }

                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    date = new Date();
                    distanceLeft.add((float) -1);
                    distanceRight.add((float) -1);
                    time = date.getTime() - firstTime;
                    times.add(time);
                    String toData = time + ";" + "0" + ";" + "-1" + ";" + "-1" + "!";
                    data.append(toData);
                    sideValue.add(0);
                    counter++;
                    break;
            }
            return true;

        }

    };


    private void endOfMeasure() {
        rightAim.setVisibility(View.GONE);
        leftAim.setVisibility(View.GONE);
        info.setText(getString(R.string.endOfMeasure));
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd;HH:mm:ss");
        Date date = new Date();
        String fileName = "measurement " + dateFormat.format(date);
        data.append(userData.toString());
        new FileSave(this, fileName, data.toString()).saveData();

        measuredData.put("time", times);
        measuredData.put("sideValue", sideValue);
        measuredData.put("distL", distanceLeft);
        measuredData.put("distR", distanceRight);
        measuredData.put("user data", userData);
        measuredData.put("shownAim", whichIsShown);

        db.collection(fileName)
                .add(measuredData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


        measuredData = new HashMap<>();
        times = new ArrayList<>();
        sideValue = new ArrayList<>();
        distanceLeft = new ArrayList<>();
        distanceRight = new ArrayList<>();
        data = new StringBuilder();
        whichIsShown = new ArrayList<>();
    }


}
