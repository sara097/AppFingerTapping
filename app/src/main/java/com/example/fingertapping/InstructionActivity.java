package com.example.fingertapping;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class InstructionActivity extends AppCompatActivity {

    VideoView vid;
    TextView instruction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        vid = (VideoView) findViewById(R.id.video);
        instruction=(TextView) findViewById(R.id.instructiontxt);
        MediaController m = new MediaController(this);
        vid.setMediaController(m);

        Intent intent = getIntent();
        int category = intent.getExtras().getInt("category");
        String path;
        String instruct;
        if (category == 0)
        {
            path = "android.resource://" + getPackageName() + "/" + R.raw.classic;
            instruct="Usiądź i oprzyj rękę stabilnie na blacie. \n" +
                    "Klikaj w ekran naprzemiennie dwoma palcami i staraj się trafić w cel.\n"+
                    "W zależności od dłoni, którą wykonywane jest zadanie, dla lewej palec środkowy powinień trafiać w lewy cel \n" +
                    "a wskazujący w prawy, dla dłoni prawej analogicznie.";
        }
        else if (category == 1)
        {
            path = "android.resource://" + getPackageName() + "/" + R.raw.synch;
            instruct="Usiądź i oprzyj rękę stabilnie na blacie. \n" +
                    "Klikaj w ekran naprzemiennie dwoma palcami \n" +
                    "starając się trafić w wyświetlany w równych odstępach czasu cel.\n"+
                    "W zależności od dłoni, którą wykonywane jest zadanie, dla lewej palec środkowy powinień trafiać w lewy cel \n" +
                    "a wskazujący w prawy, dla dłoni prawej analogicznie.";
        }
        else
        {
            path = "android.resource://" + getPackageName() + "/" + R.raw.rand;
            instruct="Usiądź i oprzyj rękę stabilnie na blacie. \n" +
                    "Klikając w ekran"+
                    "staraj się trafić w wyświetlany w równych odstępach czasu cel.\n" +
                    "Cel wyświetlany jest po losowej stronie ekranu.\n" +
                    "W zależności od dłoni, którą wykonywane jest zadanie, dla lewej palec środkowy powinień trafiać w lewy cel \n" +
                    "a wskazujący w prawy, dla dłoni prawej analogicznie.";
        }
        instruction.setText(instruct);
        Uri u = Uri.parse(path);
        vid.setVideoURI(u);
        vid.start();
        vid.setOnPreparedListener (mp -> mp.setLooping(true));


    }
}
