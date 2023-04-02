package com.example.iat359_a3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener {

    private TextView item1,item2,item3,item4;
    private TextView mutip2,mutip3,mutip5,mutip10;
    private Button b_retrieveAll,b_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkConnection();

        item1 = (TextView) findViewById(R.id.item1);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4 = (TextView) findViewById(R.id.item4);

        mutip2 = (TextView) findViewById(R.id.mutip2);
        mutip3 = (TextView) findViewById(R.id.mutip3);
        mutip5 = (TextView) findViewById(R.id.mutip5);
        mutip10 = (TextView) findViewById(R.id.mutip10);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }


    //check if device is connected to wifi at the start of the app
    public void checkConnection(){
        ConnectivityManager connectMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            //fetch data

            String networkType = networkInfo.getTypeName().toString();
            Toast.makeText(this, "connected to " + networkType, Toast.LENGTH_LONG).show();
        }
        else {
            //display error
            Toast.makeText(this, "no network connection", Toast.LENGTH_LONG).show();
        }
    }

    private String result, url;
    public void buttonGetData(View view) {
        Log.d("buttonGetData", "running");

        url = "https://qrng.anu.edu.au/API/jsonI.php?length=4&type=uint8";
        Thread myThread = new Thread(new GetWeatherThread());
        myThread.start();

        try {
            JSONObject jsonObject = new JSONObject(result);
//            JSONObject weatherObservationItems = new JSONObject(jsonObject.getString("weatherObservation"));

            JSONArray numarr = new JSONArray(jsonObject.getJSONArray("data"));

            for (int i=0;i<numarr.length();i++){
                Object element = numarr.get(i);
                String dataEach = element.toString();
                Log.d("result", dataEach);
            }

            //set result to textviews
        } catch (Exception e) {
            Log.d("ReadWeatherJSONDataTask", e.getLocalizedMessage());
        }
    }

    private class GetWeatherThread implements Runnable
    {
        @Override
        public void run() {
//            Log.d("GetWeatherThread", "running");

            Exception exception = null;
            try{
                result = readJSONData(url);
            }catch(IOException e){
                exception = e;
            }
            try{
                JSONObject jsonObj =new JSONObject(result);
                Log.d("run", jsonObj.toString());

                JSONArray numarr = jsonObj.getJSONArray("data");

                for (int i=0;i<numarr.length();i++){
                    JSONObject element = numarr.getJSONObject(i);
                    String dataEach = element.toString();
                    Log.d("result", dataEach);
                }

            }
            catch (Exception e){
                Log.d("ReadWeatherJSON", e.getLocalizedMessage());
            }

        }
    }

    private String readJSONData(String myurl) throws IOException {
        Log.d("readJSONData", "running");

        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 2500;

        URL url = new URL(myurl);
        HttpsURLConnection conn = (HttpsURLConnection ) url.openConnection();

        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();

            Log.d("readJSONData", "running");
            Log.d("readJSONData", "connect");

            int response = conn.getResponseCode();
            Log.d("tag", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
            conn.disconnect();
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Log.d("readIt","running");

        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        Log.d("readIt",new String(buffer));
        return new String(buffer);
    }

}