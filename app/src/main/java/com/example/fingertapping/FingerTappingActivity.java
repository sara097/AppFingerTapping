package com.example.fingertapping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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

    private Button start;
    private Button instruct;
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
    private Handler handler = new Handler();
    private int category = 0;

    private boolean flag = true;
    private boolean measureFlag = false;
    int time;
    int interval;
    int[] measures;

    private LinearLayout left;
    private LinearLayout right;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapping);
        start=(Button) findViewById(R.id.start);
        instruct=(Button) findViewById(R.id.instruct);
        left = (LinearLayout) findViewById(R.id.left);
        right = (LinearLayout)  findViewById(R.id.right);
        measureFlag = false;
        readSettings();
        Intent intent = getIntent();
        userData = intent.getCharSequenceArrayListExtra("UserData");
        ArrayList<Integer> order = new ArrayList<>();
        measures = new int[0];
        for (int i : intent.getExtras().getIntArray("order")) {
            order.add(i);
        }
        System.out.println(order.size());
        if (order.size() < 4) {
            category = order.get(0);
            System.out.println(category);
            order.remove(0);

            measures = new int[order.size()];
            for (int i = 0; i < order.size(); i++) {
                measures[i] = order.get(i);
            }
        } else {
            category = intent.getExtras().getInt("category");
        }

        info = (TextView) findViewById(R.id.twoTapInfo);
        leftAim = (ImageView) findViewById(R.id.leftAim);
        rightAim = (ImageView) findViewById(R.id.rightAim);
        twoTapInstruction = (TextView) findViewById(R.id.twoTapInstruction);
        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setOnTouchListener(handleTouch);
        instDisplay();
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

        final int numberOfMeasurements= (time*1000)/interval+1;
        handler.postDelayed(new Runnable() {
            public void run() {
                if (measureFlag) {

                    if (secondsPass < numberOfMeasurements) {
                        if (secondsPass % 2 == 0) {
                            aimDisplay();
                        } else {
                            rightAim.setVisibility(View.INVISIBLE);
                            leftAim.setVisibility(View.INVISIBLE);
                            left.setVisibility(View.INVISIBLE);
                            right.setVisibility(View.INVISIBLE);
                        }
                        secondsPass++;
                        if (secondsPass == (numberOfMeasurements-1)) endOfMeasure();
                    }
                }
                handler.postDelayed(this, interval);
            }
        }, interval);



    }

    private void readSettings() {
        FileSave fs = new FileSave(this);
        time = fs.readSettings().get(0);
        interval = fs.readSettings().get(1);
    }

    public void instructionClicked(View view) {
    }

    public void startClicked(View view) {
        measureFlag = true;
        leftAim.setVisibility(View.INVISIBLE);
        rightAim.setVisibility(View.INVISIBLE);
        left.setVisibility(View.INVISIBLE);
        right.setVisibility(View.INVISIBLE);
        start.setVisibility(View.GONE);
        instruct.setVisibility(View.GONE);
    }

    private float calculateDist(float centreX, float centreY, float x, float y) {
        return (float) Math.sqrt(Math.pow((x - centreX), 2) + Math.pow((y - centreY), 2));
    }


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (counter == 0) {
                leftAim.setVisibility(View.INVISIBLE);
                rightAim.setVisibility(View.INVISIBLE);
                left.setVisibility(View.INVISIBLE);
                right.setVisibility(View.INVISIBLE);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getX();
                    float y = event.getY();

                    Date date = new Date();
                    if (counter == 0 && measureFlag)
                        firstTime(x, y, date);
                    else if (measureFlag)
                        restOfMeasurement(x, y, date.getTime() - firstTime);


                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    actionDown();
                    break;

            }
            return true;

        }

        private void actionDown() {
            Date date;
            long time;
            if (measureFlag) {
                date = new Date();
                distanceLeft.add((float) -1);
                distanceRight.add((float) -1);
                time = date.getTime() - firstTime;
                times.add(time);
                String toData = time + ";" + "0" + ";" + "-1" + ";" + "-1" + "!";
                data.append(toData);
                sideValue.add(0);
                counter++;
            }
        }

        private void restOfMeasurement(float x, float y, long l) {
            long time;
            time = l;
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

        private void firstTime(float x, float y, Date date) {
            firstTime = date.getTime();
            restOfMeasurement(x, y, 0L);
        }

    };


    private void endOfMeasure() {
        measureFlag = false;
        rightAim.setVisibility(View.GONE);
        leftAim.setVisibility(View.GONE);
        left.setVisibility(View.GONE);
        right.setVisibility(View.GONE);
        info.setText(getString(R.string.endOfMeasure));
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd;HH:mm:ss");
        Date date = new Date();
        String fileName = "measurement"+category+"-" + dateFormat.format(date);
        data.append(userData.toString());
        new FileSave(this, fileName, data.toString()).saveData(true);

        measuredData.put("time", times);
        measuredData.put("sideValue", sideValue);
        measuredData.put("distL", distanceLeft);
        measuredData.put("distR", distanceRight);
        measuredData.put("user data", convertArrayToMap(userData));
        measuredData.put("shownAim", whichIsShown);

        db.collection("measurements").document(fileName).set(measuredData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        measuredData = new HashMap<>();
        times = new ArrayList<>();
        sideValue = new ArrayList<>();
        distanceLeft = new ArrayList<>();
        distanceRight = new ArrayList<>();
        data = new StringBuilder();
        whichIsShown = new ArrayList<>();

        System.out.println(measures.length);
        Intent i = new Intent(this, WaitForMeasure.class);
        i.putExtra("order", measures);
        i.putExtra("UserData", userData);
        startActivity(i);
    }


    private Map<String, String> convertArrayToMap(ArrayList<CharSequence> user) {
        Map<String, String> map = new TreeMap<>();
        map.put("identity", user.get(0).toString());
        map.put("age", user.get(1).toString());
        map.put("gender", user.get(2).toString());
        map.put("parkinson", user.get(3).toString());
        map.put("dominantHand", user.get(4).toString());
        map.put("usedHand", user.get(5).toString());
        map.put("description", user.get(6).toString());
        return map;
    }

    private void aimDisplay() {
        switch (category) {
            case 0:
                classicTapping();
                break;
            case 1:
                synchTapping();
                break;
            case 2:
                randomTapping();
                break;
        }


    }

    @SuppressLint("SetTextI18n")
    private void instDisplay() {
        switch (category) {
            case 0:
                twoTapInstruction.setText("Badanie finger tapping. " +
                        "Klikaj w ekran naprzemiennie dwoma palcami (wskazującym i środkowym). " +
                        "Pomiar rozpocznie się po kliknięciu przycisku");

                break;
            case 1:
                twoTapInstruction.setText("Badanie synchroniczny finger tapping. " +
                        "Klikaj w ekran naprzemiennie dwoma palcami (wskazującym i środkowym) w wyświetlane cele. " +
                        "Pomiar rozpocznie się po kliknięciu przycisku");

                break;
            case 2:
                twoTapInstruction.setText("Badanie synchroniczny finger tapping z losowym celem. " +
                        "Klikaj w ekran naprzemiennie dwoma palcami (wskazującym i środkowym) w wyświetlane cele. " +
                        "Pomiar rozpocznie się po kliknięciu przycisku");

                break;
        }


    }

    private void randomTapping() {
        rightAim.setVisibility(View.INVISIBLE);
        leftAim.setVisibility(View.INVISIBLE);
        left.setVisibility(View.INVISIBLE);
        right.setVisibility(View.INVISIBLE);
        Random r = new Random();
        int i1 = r.nextInt(2);
        if (i1 == 1) { //lewa
            left.setBackgroundColor(Color.GREEN);
            left.setVisibility(View.VISIBLE);
            leftAim.setVisibility(View.VISIBLE);
            whichIsShown.add(-1);
        } else { //prawa
            right.setBackgroundColor(Color.GREEN);
            right.setVisibility(View.VISIBLE);
            rightAim.setVisibility(View.VISIBLE);
            whichIsShown.add(1);
        }
    }


    private void synchTapping() {
        flag = !flag;
        rightAim.setVisibility(View.INVISIBLE);
        leftAim.setVisibility(View.INVISIBLE);
        left.setVisibility(View.INVISIBLE);
        right.setVisibility(View.INVISIBLE);
        if (flag) { //lewa
            left.setBackgroundColor(Color.GREEN);
            left.setVisibility(View.VISIBLE);
            leftAim.setVisibility(View.VISIBLE);
            whichIsShown.add(-1);
        } else { //prawa
            right.setBackgroundColor(Color.GREEN);
            right.setVisibility(View.VISIBLE);
            rightAim.setVisibility(View.VISIBLE);
            whichIsShown.add(1);
        }
    }

    private void classicTapping() {
        whichIsShown.add(-1);
    }
}
