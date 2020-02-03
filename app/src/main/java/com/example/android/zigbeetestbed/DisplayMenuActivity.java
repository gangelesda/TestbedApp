package com.example.android.zigbeetestbed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DisplayMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu);
    }

    /**
     * Each of this classes corresponds to a button on the Menu.
     * They will start a new intent and change the screen
     */
    public void tempDisplay(View view){
        //Intent for going to the temperature display
        Intent intent = new Intent(this,TemperatureArduinoActivity.class);
        startActivity(intent);
    }
    public void motionDisplay(View view){
        Intent intent = new Intent(this, MotionActivity.class);
        startActivity(intent);
    }
    public void cameraDisplay(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
    public void doorlockDisplay(View view){
        Intent intent = new Intent(this, DoorlockActivity.class);
        startActivity(intent);
    }

}
