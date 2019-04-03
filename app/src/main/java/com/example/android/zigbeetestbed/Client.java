package com.example.android.zigbeetestbed;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<String, Void, String> {

    String dstAddress;
    int dstPort;
    String response = "";
    EditText textResponse;
    Handler handler;
    StringBuilder toUpdate;
    private final static int MESSAGE_UPDATE_TEXT_CHILD_THREAD =1;

    Client(String addr, int port, EditText textResponse, Handler handler, StringBuilder toUpdate) {
        dstAddress = addr;
        dstPort = port;
        this.textResponse = textResponse;
        this.handler = handler;
        this.toUpdate = toUpdate;
    }

    private void updateScreen() {
        Log.d("Test2", response);
        Thread workerThread = new Thread(){
            @Override
            public void run(){
                toUpdate.append(response + "\n");
                Message message = new Message();
                message.what = MESSAGE_UPDATE_TEXT_CHILD_THREAD;
                handler.sendMessage(message);
            }
        };
        workerThread.start();
    }

    @Override
    protected String doInBackground(String... arg0) {

        Socket socket = null;
        Log.d("Test", "Connecting...");
        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

            /*
             * notice: inputStream.read() will block if no data return
             */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response = byteArrayOutputStream.toString("UTF-8");
                byteArrayOutputStream.reset();
                updateScreen();
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        Log.d("Test",response);
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        textResponse.setText(response);
        super.onPostExecute(result);
    }

}