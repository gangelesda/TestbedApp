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
    private static final String KEY_TURN = "temp";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private String thermo_set_url = "http://www.kirbyatprescott.ga:5000/home/thermostat/set";
    private String thermo_get_url = "http://www.kirbyatprescott.ga:5000/home/thermostat/status";

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

        TempDataChange myChange = new TempDataChange(this);
        myChange.setEdit(editText);
        myChange.setMyGraph(tempGraph);
        myChange.setMySeries(tempSeries);
        myChange.execute();
        //Run the async task in parallel
//        job.setEdit(editText);
//        job.setMyGraph(tempGraph);
//        job.setMySeries(tempSeries);
//        job.execute("temp");
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

    @Override
    public void onBackPressed() {
        //Kill the async task when pressing the back button
        super.onBackPressed();
//        job.endExecution();
    }

    private class TempDataChange extends AsyncTask <String,Void,String>{
        private EditText editText;
        private GraphView myGraph;
        private LineGraphSeries mySeries;
        private Context context;
        private int lastX = 0;

        protected void setEdit(EditText editText){ this.editText = editText; }
        protected void setMyGraph(GraphView myGraph){ this.myGraph = myGraph; }
        protected void setMySeries(LineGraphSeries mySeries){ this.mySeries = mySeries; }

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

        public void updateTemp(final String newTemp){
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
    //We need an Async class in order to perform networking jobs

    /**
     * SendPostData handles the networking through the use of HTTP requests
     * Once it has gotten the correct string, it will update the visible widget to
     * display the received value from the server.
     * SendPostData is an Asynchronous task as it needs to run in the background, and in Android it is a necessity
     * when dealing with networking.
     */
//    private class SendPostData extends AsyncTask<String,Void,String>{
//        boolean stillOn = true;
//        private  EditText editText;
//        private GraphView myGraph;
//        private LineGraphSeries mySeries;
//        private int lastX = 0;
//
//        protected void setEdit(EditText editText){
//            this.editText = editText;
//        }
//        protected void setMyGraph(GraphView myGraph){ this.myGraph = myGraph; }
//        protected void setMySeries(LineGraphSeries mySeries){ this.mySeries = mySeries; }
//
//        @Override
//        protected String doInBackground(String[] params){
//            //Call function to perform in background
//            //EditText editText = view.findViewById(R.id.tempText);
//            while(stillOn) {
//                postText(params[0]);
//                try{
//                    TimeUnit.SECONDS.sleep(1);
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//            return "yay";
//        }
//
//        @Override
//        protected void onPostExecute(String message){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //myGraph.getViewport().setMaxX(currDate.getTime());
//                    try {
//                        mySeries.appendData(new DataPoint(lastX++, Double.parseDouble(result)), true, 40);
//                        myGraph.addSeries(mySeries);
//
//                        editText.setText(editText.getText() + result + "\n");
//                        editText.setSelection(editText.getText().length());
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//
//
//        protected void endExecution(){
//            stillOn = false;
//        }
//        // Post data to server
//        private void postText(String temp){
//            try{
//                // url where the data will be posted
//                String postReceiverUrl = "http://kirbyatprescott.ga/t_data.php";
//                URL url = new URL(postReceiverUrl);
//
//                //Data to be sent
//                Map<String,Object> params = new LinkedHashMap<>();
//                if(temp.equals("temp"))
//                    params.put("data","temp");
//                //Build the data to correct format
//                StringBuilder postData = new StringBuilder();
//                for(Map.Entry<String,Object> param : params.entrySet()){
//                    if(postData.length() != 0) postData.append('&');
//                    postData.append(URLEncoder.encode(param.getKey(),"UTF-8"));
//                    postData.append('=');
//                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()),"UTF-8"));
//                }
//                byte[] postBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
//
//                //Start the connection to the receiving server
//                HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
//                try{
//                    //Set the required parameters to send a POST request to the server
//                    urlConnection.setRequestMethod("POST");
//                    urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//                    urlConnection.setRequestProperty("Content-Length",String.valueOf(postBytes.length));
//                    urlConnection.setDoOutput(true);
//
//                    //Send the request
//                    OutputStream outputStream = urlConnection.getOutputStream();
//                    outputStream.write(postBytes);
//
//                    //Look for any output
//                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    StringBuilder sb = new StringBuilder();
//                    String inputLine;
//                    while((inputLine = in.readLine()) != null){
//                        sb.append(inputLine);
//                        //Log.d("tcp",inputLine);
//                    }
//                    //Set result to be the string gotten from the server
//                    final String result  = sb.toString();
//
//
//                    //We change the editText widget to be updated every time we get a message
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //myGraph.getViewport().setMaxX(currDate.getTime());
//                            try {
//                                mySeries.appendData(new DataPoint(lastX++, Double.parseDouble(result)), true, 40);
//                                myGraph.addSeries(mySeries);
//
//                                editText.setText(editText.getText() + result + "\n");
//                                editText.setSelection(editText.getText().length());
//                            }
//                            catch (Exception e){
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                    //Close connections
//                    outputStream.close();
//                    in.close();
//                }
//                catch (IOException e){
//                    e.printStackTrace();
//                }
//                finally {
//                    //Disconnect once finished
//                    urlConnection.disconnect();
//                }
//            }
//            catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//    }
}
