package com.example.fingertapping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class InstructionActivity extends AppCompatActivity implements Initializable {

    private VideoView vid;
    private TextView instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        initializeActivityElements();
        MediaController m = new MediaController(this);
        vid.setMediaController(m);
        initializeActivityElements();
        Intent intent = getIntent();
        int category = intent.getExtras().getInt("category");
        String path;
        String instruct;
        if (category == 0) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.classic;
            instruct = getString(R.string.instructionClassic);
        } else if (category == 1) {
            path = "android.resource://" + getPackageName() + "/" + R.raw.synch;
            instruct = getString(R.string.instructionSynch);
        } else {
            path = "android.resource://" + getPackageName() + "/" + R.raw.rand;
            instruct = getString(R.string.instructionRand);
        }
        instruction.setText(instruct);
        Uri u = Uri.parse(path);
        vid.setVideoURI(u);
        vid.start();
        vid.setOnPreparedListener(mp -> mp.setLooping(true));


    }

    @Override
    public void initializeActivityElements() {
        vid = (VideoView) findViewById(R.id.video);
        instruction = (TextView) findViewById(R.id.instructiontxt);
    }
}
