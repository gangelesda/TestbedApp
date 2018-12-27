package com.example.android.zigbeetestbed;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SendMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_menu);
    }

    public void sendTurnOn(View view){
        Context context = getApplicationContext();
        CharSequence text = "Light bulb turned on";
        int duration = Toast.LENGTH_SHORT;
        //Toast for turn on
        Toast toast = Toast.makeText(context,text,duration);
        toast.show();
    }

    public void sendTurnOff(View view){
        Context context = getApplicationContext();
        CharSequence text = "Light bulb turned off";
        int duration = Toast.LENGTH_SHORT;
        //Toast for turn off
        Toast toast = Toast.makeText(context,text,duration);
        toast.show();
    }
}
