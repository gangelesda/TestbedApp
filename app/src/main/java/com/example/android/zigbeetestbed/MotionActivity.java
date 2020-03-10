package com.example.android.zigbeetestbed;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class MotionActivity extends AppCompatActivity {
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    String trash_get_url = "https://www.kirbyatprescott.ga:5000/home/trashcan/status";
    TrashcanFullness tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        final TextView tvFullness = findViewById(R.id.tvFullness);
        tf = new TrashcanFullness(this, tvFullness);
        tf.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tf.cancel(true);
    }

    private class TrashcanFullness extends AsyncTask<String, Void, String>{
        private TextView tvFullness;
        private Context context;

        TrashcanFullness(Context context, TextView tvFullness) {
            this.context = context;
            this.tvFullness = tvFullness;
        }

        @Override
        protected String doInBackground(String... strings) {
            while(true){
                getFullness();
                try{
                    TimeUnit.SECONDS.sleep(10);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        private void getFullness(){
            JsonObjectRequest setFull = new JsonObjectRequest(Request.Method.GET, trash_get_url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt(KEY_STATUS) == 0) {
                            updateFullness(response.getString(KEY_MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ErrorGET", "Failed to get fullness from server");
                }
            });
            MySingleton.getInstance(context).addToRequestQueue(setFull);
        }

        private void updateFullness(final String fullness){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvFullness.setText(fullness);
                }
            });
        }
    }

}


