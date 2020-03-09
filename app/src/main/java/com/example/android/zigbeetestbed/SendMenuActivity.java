package com.example.android.zigbeetestbed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class SendMenuActivity extends AppCompatActivity {

    private static final String KEY_TURN = "turn";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private String lightbulb_set_url = "http://www.kirbyatprescott.ga:5000/home/lightbulb/turn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_menu);

        Button on = findViewById(R.id.btTurnOn);
        Button off = findViewById(R.id.btTurnOff);

        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInfo("on");
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInfo("off");
            }
        });
    }

    private void sendInfo(String on_off){
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_TURN, on_off);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, lightbulb_set_url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getInt(KEY_STATUS) == 0){
                        Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE),Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}
