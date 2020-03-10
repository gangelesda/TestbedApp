package com.example.android.zigbeetestbed;

import android.content.Context;
import android.os.AsyncTask;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jjoe64.graphview.GraphView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class TemperatureArduinoActivity extends AppCompatActivity {
    private static final String KEY_TEMP = "temp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    String thermo_set_url = "http://www.kirbyatprescott.ga:5000/home/thermostat/set";
    String thermo_get_url = "http://www.kirbyatprescott.ga:5000/home/thermostat/status";

    //Create a new instance of the Async Class
//    SendPostData job = new SendPostData();

    //Setting up initial variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_arduino);
        //This editText widget will be manipulated to show the numbers
        EditText editText = findViewById(R.id.tempText);
        final TextView setTemp = findViewById(R.id.SetTemp);
        setTemp.setText(R.string.set_temp);

        //Buttons to change temperature
        ImageButton temp_up = findViewById(R.id.button_set_temp_up);
        ImageButton temp_down = findViewById(R.id.button_set_temp_down);
        Button send_temp = findViewById(R.id.button_set_temp);

        temp_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTemp(true, setTemp);
            }
        });
        temp_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTemp(false, setTemp);
            }
        });

        setTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishTemp(setTemp.getText().toString());
            }
        });

        //Graph that we are gonna update
        GraphView tempGraph = findViewById(R.id.TempSeries);
        LineGraphSeries<DataPoint> tempSeries = new LineGraphSeries<>();
        tempGraph.addSeries(tempSeries);

        //Set graph bounds
        tempGraph.getGridLabelRenderer().setHumanRounding(false);
        tempGraph.getViewport().setMinX(0);
        tempGraph.getViewport().setMaxX(40);
        tempGraph.getViewport().setXAxisBoundsManual(true);
        tempGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        tempGraph.getViewport().setYAxisBoundsManual(true);
        tempGraph.getViewport().setMinY(0);
        tempGraph.getViewport().setMaxY(100);
        tempGraph.getViewport().setScrollable(true);

        // Run Graph update in Async Class
        TempDataChange myChange = new TempDataChange(this);
        myChange.setEdit(editText);
        myChange.setMyGraph(tempGraph);
        myChange.setMySeries(tempSeries);
        myChange.execute();

    }

    private void changeTemp(boolean up, TextView setTemp){
        if(up) {
            int newTemp = Integer.parseInt(setTemp.getText().toString()) + 1;
            setTemp.setText(String.valueOf(newTemp));
        }
        else {
            int newTemp = Integer.parseInt(setTemp.getText().toString()) - 1;
            setTemp.setText(String.valueOf(newTemp));
        }
    }

    private void publishTemp(String temp){
        JSONObject request = new JSONObject();
        try {
            request.put(KEY_TEMP, temp);
        } catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsRequest = new JsonObjectRequest(Request.Method.POST, thermo_set_url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt(KEY_STATUS) == 0) {
                        Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onBackPressed() {
        //Kill the async task when pressing the back button
        super.onBackPressed();
//        job.endExecution();
    }

    // Async task since we are dealing with a background job
    private class TempDataChange extends AsyncTask <String,Void,String>{
        private EditText editText;
        private GraphView myGraph;
        private LineGraphSeries mySeries;
        private Context context;
        private int lastX = 0;

        private void setEdit(EditText editText){ this.editText = editText; }
        private void setMyGraph(GraphView myGraph){ this.myGraph = myGraph; }
        private void setMySeries(LineGraphSeries mySeries){ this.mySeries = mySeries; }

        TempDataChange(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(String... strings) {
            while(true){
                getTemp();
                try{
                    TimeUnit.SECONDS.sleep(5);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        private void getTemp(){
            JsonObjectRequest getTemp = new JsonObjectRequest(Request.Method.GET, thermo_get_url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt(KEY_STATUS) == 0) {
                            updateTemp(response.getString(KEY_MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ErrorGET","Failed to get temp from server");
                }
            });
            MySingleton.getInstance(context).addToRequestQueue(getTemp);
        }

        private void updateTemp(final String newTemp){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //myGraph.getViewport().setMaxX(currDate.getTime());
                    try {
                        mySeries.appendData(new DataPoint(lastX++, Double.parseDouble(newTemp)), true, 40);
                        myGraph.addSeries(mySeries);

                        editText.setText(editText.getText() + newTemp + "\n");
                        editText.setSelection(editText.getText().length());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
