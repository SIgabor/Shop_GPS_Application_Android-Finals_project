package com.example.komplex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.view.View;
import android.widget.Button;
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
    private static final int PERMISSIONS_FINE_LOCATION = 99; //!!!DO NOT CHANGE!!!
    public static final int GPS_PRECISION = 1000000/2;
    public static final int RECT_LR_SIZE = 25;
    public static final int RECT_TB_SIZE = 125;
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
    private static ImageView arrowToNorth;
    private static ImageView currentPosition;
    private static int degree;
    private static Bitmap largeBitmap;
    private static int screenWidth;
    private static int screenHeight;
    private static int visibleWidth;
    private static int visibleHeight;
    private static int xOffset;
    private static int yOffset;
    private static float[] lineOffsetX = { -360, -240, -120, 0, 120, 240, 360 };
    private static float[] lineOffsetY = { -680, -510, -340, -170, 0, 170, 340, 510, 680 };
    private static Node[][] nodes;
    private static ArrayList<Node> neighbours = new ArrayList<>();
    private static ArrayList<Node> extendedList = new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);




        grid_map = findViewById(R.id.grid_map);
        tv_deltaY = findViewById(R.id.tv_deltaY);
        tv_deltaX = findViewById(R.id.tv_deltaX);
        tv_heading = findViewById(R.id.tv_heading);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.d("MyTag", "ok 1");
        options.inSampleSize = 3;// Adjust the sample size as needed
        Log.d("MyTag", "ok 2");
        Bitmap helperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grid_map, options);
        largeBitmap = helperBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Log.d("MyTag", "ok 3");


// Calculate visible portion based on screen dimensions and position
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        visibleWidth = screenWidth;  // Adjust as needed
        visibleHeight = screenHeight;// Adjust as needed
        xOffset = largeBitmap.getWidth() / 2 - screenWidth / 2; // Adjust based on current screen position
        yOffset = largeBitmap.getHeight() / 2 - screenHeight / 2; // Adjust based on current screen position


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
        currentPosition = findViewById(R.id.iv_currentPosition);

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

        float[] rectOffsetX = {60, 180, 300, 420};
        float[] rectOffsetY = {170, 510};


        for(int i = 0; i < rectOffsetX.length; i++){
            for(int j = 0; j < rectOffsetY.length; j++){
                drawRectangle(largeBitmap.getWidth()/2 - rectOffsetX[i], largeBitmap.getHeight()/2 - rectOffsetY[j]);
                drawRectangle(largeBitmap.getWidth()/2 + rectOffsetX[i], largeBitmap.getHeight()/2 - rectOffsetY[j]);
                drawRectangle(largeBitmap.getWidth()/2 - rectOffsetX[i], largeBitmap.getHeight()/2 + rectOffsetY[j]);
                drawRectangle(largeBitmap.getWidth()/2 + rectOffsetX[i], largeBitmap.getHeight()/2 + rectOffsetY[j]);
            }
        }

        nodes = new Node[lineOffsetX.length][lineOffsetY.length];
        for (int i = 0; i < lineOffsetY.length; i++) {
            for (int j = 0; j < lineOffsetX.length; j++) {
                nodes[j][i] = new Node("" + i + j, lineOffsetX[j], lineOffsetY[i], 0, 0);

            }
        }

        textView.setText(str);
        updateGPS();
        startLocationUpdates();
        //drawCircles(largeBitmap, largeBitmap.getWidth() / 2, largeBitmap.getHeight() / 2 - 800);
        //drawRectangle(largeBitmap.getWidth()/2 - 60 - 130, largeBitmap.getHeight()/2 - 170 - 280);
        //drawLine(nodes[0][0], nodes[6][3]);
        routePlanner(nodes[1][1], nodes[6][5]);






    }

    public static void routePlanner(Node start, Node goal) {
        System.out.println("hello");

        Node currentNode = new Node(start.getName(), start.getX(), start.getY(), start.getDistanceTravelled(),
                start.getHeuristicDistance());
        Node previousNode = new Node(start.getName(), start.getX(), start.getY(), start.getDistanceTravelled(),
                start.getHeuristicDistance());

        // set heuristicDistance
        for (int i = 0; i < lineOffsetY.length; i++) {
            for (int j = 0; j < lineOffsetX.length; j++) {
                nodes[j][i].setHeuristicDistance((float) distance(nodes[j][i], goal));
            }
        }

        while (!currentNode.getName().equals(goal.getName())) {
            int[] indexes = getIndex(currentNode);

            // add neighbours
            if (indexes[0] == 0 && indexes[1] == 0) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
            } else if (indexes[0] == lineOffsetX.length - 1 && indexes[1] == 0) {
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
            } else if (indexes[0] == lineOffsetX.length - 1 && indexes[1] == lineOffsetY.length - 1) {
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
            } else if (indexes[0] == 0 && indexes[1] == lineOffsetY.length - 1) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
            } else if (indexes[1] == 0) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
            } else if (indexes[0] == lineOffsetX.length - 1 && indexes[1] % 2 == 0) {
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
            } else if (indexes[1] == lineOffsetY.length-1) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
            } else if (indexes[0] == 0 && indexes[1] % 2 == 0) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
            } else if (indexes[1] % 2 != 0) {
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
            } else {
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
                System.out.println("beakadtam");
            }

            nodeCopy(currentNode, previousNode);
            extendedList.add(currentNode);

            // set distance travelled
            for (int i = 0; i < neighbours.size(); i++) {
                neighbours.get(i).setDistanceTravelled(
                        currentNode.getDistanceTravelled() + (float) distance(currentNode, neighbours.get(i)));
            }


            // choose best neighbour
            if (!isExtended(neighbours.get(0))) {
                nodeCopy(neighbours.get(0), currentNode);
            }
            for (int i = 1; i < neighbours.size(); i++) {
                if (!isExtended(neighbours.get(i)) && (neighbours.get(i).getDistanceTravelled()
                        + neighbours.get(i).getHeuristicDistance()) < (currentNode.getDistanceTravelled()
                        + currentNode.getHeuristicDistance())) {
                    nodeCopy(neighbours.get(i), currentNode);
                }
            }
            drawLine(previousNode, currentNode);
            //drawCircle(currentNode);
            neighbours.clear();
        }

    }

    private static void drawLine(Node startPoint, Node stopPoint){
        Canvas canvas = new Canvas(largeBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(20);

        float startX = largeBitmap.getWidth()/2 + startPoint.getX();
        float startY = largeBitmap.getHeight()/2 + startPoint.getY();
        float stopX = largeBitmap.getWidth()/2 + stopPoint.getX();
        float stopY = largeBitmap.getHeight()/2 + stopPoint.getY();


        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private static void drawCircle(Node centerPoint) {
        Canvas canvas = new Canvas(largeBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);

        float radius = 50f;
        float centerX = largeBitmap.getWidth()/2 + centerPoint.getX();
        float centerY = largeBitmap.getHeight()/2 + centerPoint.getY();

        // Draw the circle on the canvas
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private void drawRectangle(float centerX, float centerY){
        Canvas canvas = new Canvas(largeBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(centerX - RECT_LR_SIZE,
                centerY - RECT_TB_SIZE,
                centerX + RECT_LR_SIZE,
                centerY + RECT_TB_SIZE, paint);
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

        deltaX = (defaultX - currentX);
        deltaY = -(defaultY - currentY);

        //changePosition(grid_map, deltaX, deltaY);
        //changePosition(locationMarker, deltaX, deltaY);

        defaultX = currentX;
        defaultY = currentY;
        tv_deltaY.setText(String.valueOf(deltaY));
        tv_deltaX.setText(String.valueOf(deltaX));

        try {
            xOffset +=(int)Math.round(deltaX);
            yOffset +=(int)Math.round(deltaY);
            Log.d("MyTag", String.valueOf(yOffset));
            Log.d("MyTag", String.valueOf(largeBitmap.getHeight()));
            Bitmap visibleBitmap = Bitmap.createBitmap(largeBitmap, xOffset, yOffset, visibleWidth, visibleHeight);// Set the cropped bitmap to the ImageView
            grid_map.setImageBitmap(visibleBitmap);// Your code here

            Log.d("MyTag", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyTag", "catched");// Log the exception
            Toast.makeText(this, "Bolt területe elhagyva!" , Toast.LENGTH_SHORT).show();
        }







    }

    public static void nodeCopy(Node from, Node to) {
        to.setName(from.getName());
        to.setX(from.getX());
        to.setY(from.getY());
        to.setDistanceTravelled(from.getDistanceTravelled());
        to.setHeuristicDistance(from.getHeuristicDistance());
    }

    public static boolean isExtended(Node A) {
        for (int i = 0; i < extendedList.size(); i++) {
            if (A.getName().equalsIgnoreCase(extendedList.get(i).getName())) {
                return true;
            }
        }
        return false;
    }

    public static int[] getIndex(Node A) {
        int[] indexes = new int[2];
        for (int i = 0; i < lineOffsetY.length; i++) {
            for (int j = 0; j < lineOffsetX.length; j++) {
                if (nodes[j][i].getName().equalsIgnoreCase(A.getName())) {
                    indexes[0] = j;
                    indexes[1] = i;
                }
            }
        }

        return indexes;
    }

    public static double distance(Node A, Node B) {
        return Math.sqrt(Math.pow((B.getX() - A.getX()), 2) + Math.pow((B.getY() - A.getY()), 2));
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}