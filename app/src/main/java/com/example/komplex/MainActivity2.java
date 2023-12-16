package com.example.komplex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements SensorEventListener {

    public static final int DEFAULT_UPDATE_INTERVAL = 1;
    public static final int FAST_UPDATE_INTERVAL = 1;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    public static final int GPS_PRECISION = 100000;
    private static TextView textView;
    private static TextView tv_lat, tv_lon, tv_deltaY, tv_deltaX, tv_heading;
    private static String str;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallBack;
    private static FusedLocationProviderClient fusedLocationProviderClient;
    private static SensorManager sensorManager;
    private static double defaultX = 0;
    private static double defaultY = 0;
    private static double currentX;
    private static double currentY;
    private static double deltaX;
    private static double deltaY;
    private static ImageView grid_map;
    private static ImageView locationMarker;
    private static ImageView arrowToNorth;

    private static int degree;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);




        grid_map = findViewById(R.id.grid_map);
        locationMarker = findViewById(R.id.locationMarker);
        tv_deltaY = findViewById(R.id.tv_deltaY);
        tv_deltaX = findViewById(R.id.tv_deltaX);
        tv_heading = findViewById(R.id.tv_heading);


        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        textView =  findViewById(R.id.tv_baggedItems);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        arrowToNorth = findViewById(R.id.iv_arrowToNorth);

        Intent recieverIntent = getIntent();
        Bundle args = recieverIntent.getBundleExtra("BUNDLE");
        List<Item> bagItems = (ArrayList<Item>) args.getSerializable("BAG_ITEMS");

        for(int i = 0; i < bagItems.size(); i++){
            if(i == 0){
                str = bagItems.get(i).getName() + "\n";
            }else {
                str = str + bagItems.get(i).getName() + "\n";
            }
        }

        textView.setText(str);
        updateGPS();
        startLocationUpdates();


    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper());
            updateGPS();
            //ha beszarna akkor csak a felső 2 sor kell, az ifek nem
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
        Log.d("MyTag", "Location is being tracked");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }else{
                    Toast.makeText(this, "This app needs permission to be granted in order to work properly!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity2.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));// = y
        tv_lon.setText(String.valueOf(location.getLongitude()));// = x

        if(defaultX  == 0 && defaultY == 0){
            defaultX = -(int)Math.round(location.getLongitude() * GPS_PRECISION);
            defaultY = -(int)Math.round(location.getLatitude() * GPS_PRECISION);
        }

        currentX = -(int)Math.round(location.getLongitude() * GPS_PRECISION);
        currentY = -(int)Math.round(location.getLatitude() * GPS_PRECISION);

        deltaX = -(defaultX - currentX);
        deltaY = -(defaultY - currentY);

        changePosition(grid_map, deltaX, deltaY);
        changePosition(locationMarker, deltaX, deltaY);

        defaultX = currentX;
        defaultY = currentY;
        tv_deltaY.setText(String.valueOf(deltaY));
        tv_deltaX.setText(String.valueOf(deltaX));




    }

    public static void changePosition(ImageView imageView, double deltaX, double deltaY){
        imageView.setX(imageView.getX() + (int)Math.round(deltaX));
        imageView.setY(imageView.getY() - (int)Math.round(deltaY));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_rigth);
    }
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        degree = Math.round(sensorEvent.values[0]);
        tv_heading.setText("Heading: " + degree);
        arrowToNorth.setRotation(-degree);
        //TODO: kitalálni valamit a kép forgatására, mivel annaka középpontja mindig máshol lesz
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}