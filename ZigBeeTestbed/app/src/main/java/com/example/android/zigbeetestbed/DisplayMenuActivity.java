package com.example.android.zigbeetestbed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DisplayMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu);
    }
    public void tempDisplay(View view){
        //Intent for going to the temperature display
        Intent intent = new Intent(this,TemperatureArduinoActivity.class);
        startActivity(intent);
    }
    public void motionDisplay(View view){
        Intent intent = new Intent(this, MotionActivity.class);
        startActivity(intent);
    }
    public void soundDisplay(View view){
        Intent intent = new Intent(this, SoundActivity.class);
        startActivity(intent);
    }
    public void accelDisplay(View view){
        Intent intent = new Intent(this, AccelerometerActivity.class);
        startActivity(intent);
    }
    public void tempPiDsplay(View view){
        Intent intent = new Intent(this, TemperaturePiActivity.class);
        startActivity(intent);
    }
    public void tbdT(View view){
        //Toast for not implemented feature
        Toast toast = Toast.makeText(getApplicationContext(),"Feature to be implemented!",Toast.LENGTH_SHORT);
        toast.show();
    }
}
