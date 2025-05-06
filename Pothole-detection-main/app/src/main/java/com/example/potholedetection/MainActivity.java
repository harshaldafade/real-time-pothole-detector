package com.example.potholedetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor Magno, Acc, Gyro;
    TextView xValue, yValue, zValue, accxValue, accyValue, acczValue, gyroxValue, gyroyValue, gyrozValue, PotholeValue;
    String url = "https://dd1f-129-63-248-48.ngrok-free.app/predict"; //replace with your flask app link
    final Handler handler = new Handler();
    final int delay = 6000;

    String magx,magy,magz,accx,accy,accz,gyrox,gyroy,gyroz;

    double cur1, cur2;

    CurrentLocationListener currentLocationListener;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);
        accxValue = (TextView) findViewById(R.id.accxValue);
        accyValue = (TextView) findViewById(R.id.accyValue);
        acczValue = (TextView) findViewById(R.id.acczValue);

        gyroxValue = (TextView) findViewById(R.id.gyroxValue);
        gyroyValue = (TextView) findViewById(R.id.gyroyValue);
        gyrozValue = (TextView) findViewById(R.id.gyrozValue);


        PotholeValue = (TextView) findViewById(R.id.PotholeValue);

        currentLocationListener = new CurrentLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if this check fails, the app will not function.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No Permission");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, currentLocationListener);


        //Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Magno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (Magno != null) {
            sensorManager.registerListener((SensorEventListener) MainActivity.this, Magno, 6000000);
        }
        Acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (Acc != null) {
            sensorManager.registerListener((SensorEventListener) MainActivity.this, Acc, 6000000);
        }
        Gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (Gyro != null) {
            sensorManager.registerListener((SensorEventListener) MainActivity.this, Gyro, 6000000);
        }

        // Python Flask API
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateLocation();
                CallApi();
                handler.postDelayed(this, delay);
            }
        }, delay);
        //
    }

    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //Log.d(TAG, "onSensorChanged: magX: " + sensorEvent.values[0] + " magY: " + sensorEvent.values[1] + " magZ: " + sensorEvent.values[2]);

            xValue.setText("X-Value: " + sensorEvent.values[0]);
            yValue.setText("Y-Value: " + sensorEvent.values[1]);
            zValue.setText("Z-Value: " + sensorEvent.values[2]);

            magx = String.valueOf(sensorEvent.values[0]);
            magy = String.valueOf(sensorEvent.values[1]);
            magz = String.valueOf(sensorEvent.values[2]);
        }
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Log.d(TAG, "onSensorChanged: accX: " + sensorEvent.values[0] + " accY: " + sensorEvent.values[1] + " accZ: " + sensorEvent.values[2]);

            accxValue.setText("X-Value: " + sensorEvent.values[0]);
            accyValue.setText("Y-Value: " + sensorEvent.values[1]);
            acczValue.setText("Z-Value: " + sensorEvent.values[2]);

            accx = String.valueOf(sensorEvent.values[0]);
            accy = String.valueOf(sensorEvent.values[1]);
            accz = String.valueOf(sensorEvent.values[2]);
        }
        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //Log.d(TAG, "onSensorChanged: gyroX: " + sensorEvent.values[0] + " gyroY: " + sensorEvent.values[1] + " gyroZ: " + sensorEvent.values[2]);

            gyroxValue.setText("X-Value: " + sensorEvent.values[0]);
            gyroyValue.setText("Y-Value: " + sensorEvent.values[1]);
            gyrozValue.setText("Z-Value: " + sensorEvent.values[2]);

            gyrox = String.valueOf(sensorEvent.values[0]);
            gyroy = String.valueOf(sensorEvent.values[1]);
            gyroz = String.valueOf(sensorEvent.values[2]);
        }
    }

    public void CheckPothole(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("Is Pothole");
                            Log.d(TAG, data);
                            if (data.equals("Yes")) {
                                PotholeValue.setText("Pothole !!");
                            }
                            else PotholeValue.setText("Not a Pothole");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.getMessage());
                        //Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap();
                params.put("acc_x",accx);
                params.put("acc_y",accy);
                params.put("acc_z",accz);
                params.put("gyro_x",gyrox);
                params.put("gyro_y",gyroy);
                params.put("gyro_z",gyroz);
                params.put("mag_x",magx);
                params.put("mag_y",magy);
                params.put("mag_z",magz);
                Log.d(TAG, String.valueOf(params));
                return params;
            }

            public Map<String,String> getHeaders(){
                Map<String,String> headers = new HashMap();
                headers.put("Content-Type","application/json");
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);
    }

    public void CallApi(){
        Button ShowButton = (Button) findViewById(R.id.ShowMapButton);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("acc_x",Float.parseFloat(accx));
            postData.put("acc_y",Float.parseFloat(accy));
            postData.put("acc_z",Float.parseFloat(accz));
            postData.put("gyro_x",Float.parseFloat(gyrox));
            postData.put("gyro_y",Float.parseFloat(gyroy));
            postData.put("gyro_z",Float.parseFloat(gyroz));
            postData.put("mag_x",Float.parseFloat(magx));
            postData.put("mag_y",Float.parseFloat(magy));
            postData.put("mag_z",Float.parseFloat(magz));

           /* postData.put("acc_x",3);
            postData.put("acc_y",2);
            postData.put("acc_z",9);
            postData.put("gyro_x",0);
            postData.put("gyro_y",0);
            postData.put("gyro_z",0);
            postData.put("mag_x",0);
            postData.put("mag_y",0);
            postData.put("mag_z",0);*/

            Log.d(TAG, String.valueOf(postData));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Iterator<String> keys = response.keys();
                String KeyName = keys.next();
                String Value = response.optString(KeyName);
                Log.d(TAG, Value);
                PotholeValue.setText("Is Pothole : " + Value);
                if(Value.equals("Yes"))
                    ShowButton.setVisibility(View.VISIBLE);
                else
                    ShowButton.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    void updateLocation() {
        Location location;//new location object

        //check permissions. If grantedm get last known location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No Permission");
            return;
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, currentLocationListener);

        if (location == null) {
            location = currentLocationListener.getLocation();
            cur1 = location.getLatitude();
            cur2= location.getLongitude();

        }else {
            cur1 = location.getLatitude();
            cur2 = location.getLongitude();
        }

        Log.d(TAG, String.valueOf(cur1));
        Log.d(TAG, String.valueOf(cur2));
    }

    // Function for starting map
    public void goToMap(View View){
        Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("lat1", String.valueOf(cur1));
        bundle.putString("lon1", String.valueOf(cur2));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}