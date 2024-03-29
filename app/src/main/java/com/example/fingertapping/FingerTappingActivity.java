package com.example.fingertapping;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class FingerTappingActivity extends AppCompatActivity implements Initializable {


    private static final String TAG = "FingerTapping";

    private Button start;
    private Button instruct;
    private ImageView leftAim;
    private ImageView rightAim;
    private TextView twoTapInstruction;
    private LinearLayout left;
    private LinearLayout right;
    private TextView hand;
    private ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private long firstTime = 0;
    private float width;
    private float centreXR;
    private float centreYR;
    private float centreXL;
    private float centreYL;

    private Map<String, Object> measuredData = new HashMap<>();
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<String> sideValue = new ArrayList<>(); // 0 - uniesienie paca, 10 - prawa strona, -10 - lewa strona
    private ArrayList<Float> XR = new ArrayList<>();
    private ArrayList<Float> XL = new ArrayList<>();
    private ArrayList<Float> YR = new ArrayList<>();
    private ArrayList<Float> YL = new ArrayList<>();
    private ArrayList<Long> aimTime = new ArrayList<>();
    private ArrayList<CharSequence> userData = new ArrayList<>();
    private ArrayList<String> whichIsShown = new ArrayList<>();

    private int secondsPass = 0;
    private Handler handler = new Handler();

    private boolean flag = true;
    private boolean measureFlag = false;
    private int time = 20;
    private int interval = 750;

    private int[] measures;
    private int category = 0;
    private ArrayList<Integer> order;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapping);

        initializeActivityElements();
        measureFlag = false;
        readSettings();

        Intent intent = getIntent();
        userData = intent.getCharSequenceArrayListExtra("UserData");
        measures = new int[0];
        order = readOrderOfMeasurements(intent);
        getCategory(order);

        if (order.size() < 3) hand.setText(getString(R.string.leftHandInfo));
        else hand.setText(getString(R.string.rightHandInfo));

        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setOnTouchListener(handleTouch);

        calculateAimCentre();
        createTimeHandler();
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void createTimeHandler() {
        final int numberOfMeasurements = ((time * 1000) / interval + 1);
        handler.postDelayed(new Runnable() {
            public void run() {
                if (measureFlag) {
                    Date date = new Date();
                    if (secondsPass < numberOfMeasurements) {
                        if (category != 0)
                            if (secondsPass % 2 == 0) {
                                aimDisplay();
                            } else {
                                changeAllVisibility(View.INVISIBLE);
                            }
                        else aimDisplay();
                        secondsPass++;
                        aimTime.add(date.getTime() - firstTime);
                        if (secondsPass == (numberOfMeasurements - 1)) endOfMeasure();
                    }
                }
                handler.postDelayed(this, interval);
            }
        }, interval);
    }

    private void calculateAimCentre() {
        instDisplay();
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        width = size.x;

        rightAim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                rightAim.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] locations = new int[2];
                rightAim.getLocationOnScreen(locations);
                centreXR = locations[0];
                centreYR = locations[1];
            }
        });


        leftAim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                leftAim.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int[] locations = new int[2];
                leftAim.getLocationOnScreen(locations);
                centreXL = locations[0];
                centreYL = locations[1];
            }
        });


        centreXL = leftAim.getX() + leftAim.getWidth() / 2;
        centreYL = leftAim.getY() + leftAim.getWidth() / 2;
    }

    @Override
    public void initializeActivityElements() {
        start = (Button) findViewById(R.id.start);
        instruct = (Button) findViewById(R.id.instruct);
        left = (LinearLayout) findViewById(R.id.left);
        right = (LinearLayout) findViewById(R.id.right);
        leftAim = (ImageView) findViewById(R.id.leftAim);
        rightAim = (ImageView) findViewById(R.id.rightAim);
        twoTapInstruction = (TextView) findViewById(R.id.twoTapInstruction);
        hand = (TextView) findViewById(R.id.handTxt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void getCategory(ArrayList<Integer> order) {
        if (order.size() < 7) {
            category = order.get(0);
            order.remove(0);
            measures = new int[order.size()];
            for (int i = 0; i < order.size(); i++)
                measures[i] = order.get(i);

        }
    }

    private ArrayList<Integer> readOrderOfMeasurements(Intent intent) {
        ArrayList<Integer> order = new ArrayList<>();
        for (int i : intent.getExtras().getIntArray("order"))
            order.add(i);

        return order;
    }

    private void readSettings() {
        FileOperations fs = new FileOperations(this);
        try {
            time = fs.readSettings().get(0);
            interval = fs.readSettings().get(1);
        } catch (Exception e) {
            String settings = time + ";" + interval;
            FileOperations fss = new FileOperations(this, "settings", settings);
            fss.saveSettingsData(false);
        }
    }

    public void instructionClicked(View view) {
        Intent i = new Intent(getBaseContext(), InstructionActivity.class);
        i.putExtra("order", measures);
        i.putExtra("UserData", userData);
        i.putExtra("category", category);
        startActivity(i);
    }

    public void startClicked(View view) {
        measureFlag = true;
        progressBar.setVisibility(View.VISIBLE);
        firstTime = new Date().getTime();
        if (category != 0)
            changeAllVisibility(View.INVISIBLE);
        start.setVisibility(View.GONE);
        instruct.setVisibility(View.GONE);
        ValueAnimator animator = prepareProgressBarAnimator();
        animator.start();
    }

    @NotNull
    private ValueAnimator prepareProgressBarAnimator() {
        progressBar.setScaleY(3f);
        ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
        animator.setDuration(time * 1000);
        animator.addUpdateListener(animation -> progressBar.setProgress((Integer) animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setProgress(0);
            }
        });
        return animator;
    }

    private float calculateDist(float centreX, float centreY, float x, float y) {
        return (float) Math.sqrt(Math.pow((x - centreX), 2) + Math.pow((y - centreY), 2));
    }


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getX();
                    float y = event.getY();
                    Date date = new Date();
                    if (measureFlag)
                        performMeasurement(x, y, date.getTime() - firstTime);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    actionUp();
                    break;
            }
            return true;

        }

        private void actionUp() {
            Date date;
            long time;
            if (measureFlag) {
                date = new Date();
                XL.add((float) 0); //odległość nieistotna przy podnoszeniu palca
                YL.add((float) 0);
                XR.add((float) 0);
                YR.add((float) 0);
                time = date.getTime() - firstTime;
                times.add(time);
                sideValue.add("UP");
            }
        }

        private void performMeasurement(float x, float y, long time) {
            times.add(time);
            if (x > (width / 2)) { //prawa strona
                float distance = calculateDist(centreXR, centreYR, x, y);
                XR.add(x);
                YR.add(y);
                YL.add((float) -1);
                XL.add((float) -1);
                sideValue.add("R");
            } else { //lewa strona
                XL.add(x);
                YL.add(y);
                XR.add((float) -1);
                YR.add((float) -1);
                sideValue.add("L");
            }
        }

    };

    private void changeAllVisibility(int visibility) {
        leftAim.setVisibility(visibility);
        rightAim.setVisibility(visibility);
        left.setVisibility(visibility);
        right.setVisibility(visibility);
    }


    private void endOfMeasure() {
        measureFlag = false;
        progressBar.setVisibility(View.INVISIBLE);
        changeAllVisibility(View.GONE);
        String fileName = generateFileName();
        saveDataToFile(fileName, dataToFile());
        collectData();
        saveToDatabase(fileName);
        clearData();
        Intent i = new Intent(this, WaitForMeasure.class);
        i.putExtra("order", measures);
        i.putExtra("UserData", userData);
        finish();
        startActivity(i);
    }

    private void clearData() {
        measuredData = new HashMap<>();
        times = new ArrayList<>();
        sideValue = new ArrayList<>();
        XR = new ArrayList<>();
        YR = new ArrayList<>();
        XL = new ArrayList<>();
        YL = new ArrayList<>();
        whichIsShown = new ArrayList<>();
        aimTime = new ArrayList<>();
    }

    private void saveDataToFile(String fileName, String data) {
        new FileOperations(this, fileName, data).saveData(true);
    }

    @NotNull
    private String generateFileName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd;HH:mm:ss");
        Date date = new Date();
        return "measurement" + category + "-" + dateFormat.format(date);
    }

    private void saveToDatabase(String fileName) {
        db.collection("measurements").document(fileName).set(measuredData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    private void collectData() {
        Map<String, Integer> settings = new HashMap<>();
        settings.put("time", time);
        settings.put("interval", interval);
        settings.put("category", category);
        Map<String, Float> aims = new HashMap<>();
        aims.put("XR", centreXR);
        aims.put("YR", centreYR);
        aims.put("XL", centreXL);
        aims.put("YL", centreYL);
        measuredData.put("time", times);
        measuredData.put("sideValue", sideValue);
        measuredData.put("xR", XR);
        measuredData.put("yR", YR);
        measuredData.put("xL", XL);
        measuredData.put("yL", YL);
        measuredData.put("user data", convertArrayToMap(userData));
        measuredData.put("shownAim", whichIsShown);
        measuredData.put("aimTime", aimTime);
        measuredData.put("settings", settings);
        measuredData.put("aimsCoordinates", aims);
    }

    private String dataToFile() {
        StringBuilder output = new StringBuilder();
        String endOfLine = ";\n";
        output.append("time:");
        for (Long aLong : times) output.append(aLong).append(",");
        output.append(endOfLine);

        output.append("sideValue:");
        for (String i : sideValue) output.append(i).append(",");
        output.append(endOfLine);

        output.append("XR:");
        for (Float aFloat : XR) output.append(aFloat).append(",");
        output.append(endOfLine);

        output.append("YR:");
        for (Float a : YR) output.append(a).append(",");
        output.append(endOfLine);

        output.append("XL:");
        for (Float a : XL) output.append(a).append(",");
        output.append(endOfLine);

        output.append("YL:");
        for (Float a : YL) output.append(a).append(",");
        output.append(endOfLine);

        output.append("userData:");
        for (CharSequence ud : userData) output.append(ud).append(",");
        output.append(endOfLine);

        output.append("shownAim:");
        for (String i : whichIsShown) output.append(i).append(",");
        output.append(endOfLine);

        output.append("aimTime:");
        for (Long aLong : aimTime) output.append(aLong).append(",");
        output.append(endOfLine);

        output.append("settings:").append(time).append(",").append(interval).append(",").append(category).append(endOfLine);

        output.append("aimsCoordinates:");
        output.append(centreXR).append(",").append(centreYR).append(",").append(centreXL).append(",").append(centreYL);
        output.append(endOfLine);

        return output.toString();
    }


    private Map<String, String> convertArrayToMap(ArrayList<CharSequence> user) {
        Map<String, String> map = new TreeMap<>();
        map.put("identity", user.get(0).toString());
        map.put("age", user.get(1).toString());
        map.put("gender", user.get(2).toString());
        map.put("parkinson", user.get(3).toString());
        map.put("dominantHand", user.get(4).toString());
        map.put("description", user.get(5).toString());
        map.put("tremor", user.get(6).toString());
        map.put("group", user.get(7).toString());
        map.put("id", user.get(8).toString());
        map.put("patientName", user.get(9).toString());
        map.put("patientLastName", user.get(10).toString());

        if (order.size() < 3) map.put("hand", "left");
        else map.put("hand", "right");
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

    private void instDisplay() {
        switch (category) {
            case 0:
                twoTapInstruction.setText(getString(R.string.classicInstruction));
                break;
            case 1:
                twoTapInstruction.setText(getString(R.string.synchInstruction));
                break;
            case 2:
                twoTapInstruction.setText(getString(R.string.randInstruction));
                break;
        }
    }

    private void randomTapping() {
        changeAllVisibility(View.INVISIBLE);
        Random r = new Random();
        int i1 = r.nextInt(2);
        if (i1 == 1) { //lewa
            greenAndVisible(left);
            leftAim.setVisibility(View.VISIBLE);
            whichIsShown.add("L");
        } else { //prawa
            greenAndVisible(right);
            rightAim.setVisibility(View.VISIBLE);
            whichIsShown.add("R");
        }
    }

    private void synchTapping() {
        flag = !flag;
        changeAllVisibility(View.INVISIBLE);
        if (flag) { //lewa
            greenAndVisible(left);
            leftAim.setVisibility(View.VISIBLE);
            whichIsShown.add("L");
        } else { //prawa
            greenAndVisible(right);
            rightAim.setVisibility(View.VISIBLE);
            whichIsShown.add("R");
        }
    }

    private void greenAndVisible(LinearLayout side) {
        side.setBackgroundColor(Color.GREEN);
        side.setVisibility(View.VISIBLE);
    }

    private void classicTapping() {
        whichIsShown.add("");
        leftAim.setVisibility(View.VISIBLE);
        rightAim.setVisibility(View.VISIBLE);
    }
}
