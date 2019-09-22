package com.example.android.zigbeetestbed;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    /**Called when user click on the send message button**/
    public void sendMessageMenu(View view){
        //Intent for going to the next menu
        Intent intent = new Intent(this,SendMenuActivity.class);
        startActivity(intent);
    }

    /**Called when user clicks on the display button**/
    public void displayMenu(View view){
        //Intent for going to the display menu
        Intent intent = new Intent(this,DisplayMenuActivity.class);
        startActivity(intent);
    }
}
