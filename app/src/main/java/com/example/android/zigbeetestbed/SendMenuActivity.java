package com.example.android.zigbeetestbed;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class SendMenuActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_menu);
    }

    public void sendTurnOn(View view){
        SendPostData job = new SendPostData();
        job.execute("true");
        //Toast for turn on
        Toast toast = Toast.makeText(getApplicationContext(),"Light bulb turned on",Toast.LENGTH_SHORT);
        toast.show();
    }

    public void sendTurnOff(View view){
        SendPostData job = new SendPostData();
        job.execute("false");
        //Toast for turn off
        Toast toast = Toast.makeText(getApplicationContext(),"Light bulb turned off",Toast.LENGTH_SHORT);
        toast.show();
    }

    //We need an Async class in order to perform networking jobs
    private static class SendPostData extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String[] params){
            //Call function to perform in background
            postText(params[0]);
            return "yay";
        }

        @Override
        protected void onPostExecute(String message){

        }
        // Post data to server
        private void postText(String on){
            try{
                // url where the data will be posted
                String postReceiverUrl = "http://ec2-34-219-240-37.us-west-2.compute.amazonaws.com/app_receive.php";
                URL url = new URL(postReceiverUrl);

                //Data to be sent
                Map<String,Object> params = new LinkedHashMap<>();
                if(on.compareTo("true")==0)
                    params.put("on","1");
                else
                    params.put("on","0");

                //Build the data to correct format
                StringBuilder postData = new StringBuilder();
                for(Map.Entry<String,Object> param : params.entrySet()){
                    if(postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(),"UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()),"UTF-8"));
                }
                byte[] postBytes = postData.toString().getBytes("UTF-8");

                //Start the connection to the receiving server
                HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
                try{
                    //Set the required parameters to send a POST request to the server
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Content-Length",String.valueOf(postBytes.length));
                    urlConnection.setDoOutput(true);

                    //Send the request
                    OutputStream outputStream = urlConnection.getOutputStream();
                    outputStream.write(postBytes);

                    //Look for any output
                    InputStream inputStream = urlConnection.getInputStream();
                    inputStream.read();

                    //Close connections
                    outputStream.close();
                    inputStream.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                finally {
                    //Disconnect once finished
                    urlConnection.disconnect();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}