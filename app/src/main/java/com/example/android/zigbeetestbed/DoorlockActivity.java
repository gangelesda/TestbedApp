package com.example.android.zigbeetestbed;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class DoorlockActivity extends AppCompatActivity {
    private static final String KEY_PIN = "pin";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    String door_set_url = "http://www.kirbyatprescott.ga:5000/home/doorlock/action";
    String door_get_url = "http://www.kirbyatprescott.ga:5000/home/doorlock/status";
    DoorStatus ds;
    EditText pinNumber = findViewById(R.id.lbPin);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorlock);
        setButtonListeners();
    }

    private void setButtonListeners(){
        Button digitOne = findViewById(R.id.btOne);
        Button digitTwo = findViewById(R.id.btTwo);
        Button digitThree = findViewById(R.id.btThree);
        Button digitFour = findViewById(R.id.btFour);
        Button digitFive = findViewById(R.id.btFive);
        Button digitSix = findViewById(R.id.btSix);
        Button digitSeven = findViewById(R.id.btSeven);
        Button digitEight = findViewById(R.id.btEight);
        Button digitNine = findViewById(R.id.btNine);
        Button digitZero = findViewById(R.id.btZero);
        Button clearB = findViewById(R.id.btClear);
        Button btEnter = findViewById(R.id.btEnter);

        final String digitOneText = digitOne.getText().toString();
        final String digitTwoText = digitTwo.getText().toString();
        final String digitThreeText = digitThree.getText().toString();
        final String digitFourText = digitFour.getText().toString();
        final String digitFiveText = digitFive.getText().toString();
        final String digitSixText = digitSix.getText().toString();
        final String digitSevenText = digitSeven.getText().toString();
        final String digitEightText = digitEight.getText().toString();
        final String digitNineText = digitNine.getText().toString();
        final String digitZeroText = digitZero.getText().toString();

        digitOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitOneText);
            }
        });
        digitTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitTwoText);
            }
        });
        digitThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitThreeText);
            }
        });
        digitFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitFourText);
            }
        });
        digitFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitFiveText);
            }
        });
        digitSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitSixText);
            }
        });
        digitSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitSevenText);
            }
        });
        digitEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitEightText);
            }
        });
        digitNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitNineText);
            }
        });
        digitZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                digitEnter(digitZeroText);
            }
        });
        clearB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearText();
            }
        });
        btEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPin(pinNumber.getText().toString());
            }
        });
    }
    public void digitEnter(String digit){
        String currentNumber = pinNumber.getText().toString();
        if(currentNumber.length() < 4){
            pinNumber.setText(currentNumber+digit);
        }
    }

    private void sendPin(String pin){
        final JSONObject request = new JSONObject();
        try {
            request.put(KEY_PIN, pin);
        } catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsRequest = new JsonObjectRequest(Request.Method.POST, door_set_url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(KEY_STATUS) == 0 && request.getString(KEY_MESSAGE).equals("Success")) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        ds.getStat();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsRequest);
    }

    public void clearText(){
        EditText pinNumber = findViewById(R.id.lbPin);
        pinNumber.setText("");
    }

    private class DoorStatus extends AsyncTask <String, Void, String> {
        private ImageView doorlock;
        private Context context;

        DoorStatus(Context context, ImageView doorlock){
            this.context = context;
            this.doorlock = doorlock;
        }

        @Override
        protected String doInBackground(String... strings) {
            while(true){
                getStat();
                try {
                    TimeUnit.SECONDS.sleep(15);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        private void getStat() {
            JsonObjectRequest stat = new JsonObjectRequest(Request.Method.GET, door_get_url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt(KEY_STATUS) == 0) {
                            updateStat(response.getString(KEY_MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ErrorGET", "Failed to get door status from server");
                }
            });
            MySingleton.getInstance(context).addToRequestQueue(stat);
        }

        private void updateStat(String current){
            if (current.equals("Open")){
                doorlock.setImageResource(R.drawable.ic_lock_outline_white);
            }
            else {
                doorlock.setImageResource(R.drawable.ic_lock_outline_white);
            }
        }
    }
}
