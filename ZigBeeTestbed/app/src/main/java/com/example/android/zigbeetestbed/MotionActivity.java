package com.example.android.zigbeetestbed;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class MotionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        EditText editText = findViewById(R.id.motionText);
        Client myClient = new Client("34.219.240.37",8080, editText);
        myClient.execute();
    }
}
