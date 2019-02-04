package com.example.android.zigbeetestbed;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class MotionActivity extends AppCompatActivity {
    private EditText editText;
    private StringBuilder p;
    private Handler updateUIHandler = null;
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);
        createUpdateUiHandler();
        editText = findViewById(R.id.motionText);
        p=new StringBuilder("");
        Client myClient = new Client("34.219.240.37",8080, editText, updateUIHandler, p);
        myClient.execute();
    }

    public void updateText() {
        editText.setText(p.toString());
    }

    private void createUpdateUiHandler(){
        if(updateUIHandler==null){
            updateUIHandler= new Handler(){
                @Override
                public void handleMessage(Message msg){
                    if(msg.what==MESSAGE_UPDATE_TEXT_CHILD_THREAD)
                        updateText();
                }
            };
        }
    }


}
