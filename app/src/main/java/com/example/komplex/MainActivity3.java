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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity implements SensorEventListener {

    public static final int DEFAULT_UPDATE_INTERVAL = 1;
    public static final int FAST_UPDATE_INTERVAL = 1;
    private static final int PERMISSIONS_FINE_LOCATION = 99; //!!!DO NOT CHANGE!!!
    public static final int GPS_PRECISION = 1000000;
    public static final int RECT_LR_SIZE = 25;
    public static final int RECT_TB_SIZE = 125;
    private static final float[][] LINE_OFFSET_X = {{ -360, -240, -120, 0, 120, 240, 360 }, {-120, 0, 120}};
    private static final float[][] LINE_OFFSET_Y = {{ -680, -510, -340, -170, 0, 170, 340, 510, 680 }, { -340, -170, 0, 170, 340}};
    private static final float[][] RECT_OFFSET_X = {{60, 180, 300, 420}, {60, 180}};
    private static final float[][] RECT_OFFSET_Y = {{170, 510}, {170}};
    private static final int CENTER_OFFSET = 350;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallBack;
    private static FusedLocationProviderClient fusedLocationProviderClient;
    private static SensorManager sensorManager;
    private static double defaultX = 0;
    private static double defaultY = 0;
    private static int xOffset;
    private static int yOffset;
    private static ImageView iv_map;
    private static ImageView currentPosition;
    private static Bitmap largeBitmap;
    private static Bitmap helperBitmap;
    private static int screenWidth;
    private static int screenHeight;
    private static int visibleWidth;
    private static int visibleHeight;
    private static Node[][] nodes;
    private static Node currentPositionNode;
    private static ArrayList<String> extendedList = new ArrayList<>();
    private static List<Item> bagItems;
    private static List<Node> itemNodes;
    private static Button btn_nextItem;
    private static int desiredShop = -1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        iv_map = findViewById(R.id.iv_map);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        try {
            helperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grid_map, options);
        }catch (Exception e){
            e.printStackTrace();
        }
        largeBitmap = helperBitmap.copy(Bitmap.Config.ARGB_8888, true);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        visibleWidth = screenWidth;
        visibleHeight = screenHeight;
        xOffset = largeBitmap.getWidth() / 2 - screenWidth / 2;
        yOffset = largeBitmap.getHeight() / 2 - screenHeight / 2;

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

        Intent recieverIntent = getIntent();
        Bundle args = recieverIntent.getBundleExtra("BUNDLE");
        bagItems = (ArrayList<Item>) args.getSerializable("BAG_ITEMS");

        desiredShop = recieverIntent.getIntExtra("desiredShop", -1);

        nodes = new Node[LINE_OFFSET_X[desiredShop].length][LINE_OFFSET_Y[desiredShop].length];
        for (int i = 0; i < LINE_OFFSET_Y[desiredShop].length; i++) {
            for (int j = 0; j < LINE_OFFSET_X[desiredShop].length; j++) {
                nodes[j][i] = new Node("" + i + j, LINE_OFFSET_X[desiredShop][j], LINE_OFFSET_Y[desiredShop][i], 0, 0);

            }
        }

        itemNodes = new ArrayList<>();
        for (Item item: bagItems) {
            itemNodes.add(nodes[item.getX()][item.getY()]);
        }

        currentPosition = findViewById(R.id.iv_currentPosition);

        float currX = currentPosition.getX();
        float currY = currentPosition.getY() + 725;
        if(desiredShop == 1){
            currY -= CENTER_OFFSET;
            yOffset -= CENTER_OFFSET;
        }
        currentPositionNode = new Node("current", currX, currY, 0, 0);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(itemNodes.size());
        progressBar.setProgress(0);

        btn_nextItem = findViewById(R.id.btn_nextItem);
        btn_nextItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!itemNodes.isEmpty()){
                    try{
                        itemNodes.remove(0);
                        progressBar.setProgress(progressBar.getProgress() + 1);
                    }catch (Exception e){
                        Log.d("MyTag", "cathed at button");
                    }

                }
                else{
                    progressBar.setProgress(progressBar.getMax());
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        drawShop();

        updateGPS();
        startLocationUpdates();

    }

    private static void drawShop() {
        for(int i = 0; i < RECT_OFFSET_X[desiredShop].length; i++){
            for(int j = 0; j < RECT_OFFSET_Y[desiredShop].length; j++){
                drawRectangle(largeBitmap.getWidth()/2 - RECT_OFFSET_X[desiredShop][i], largeBitmap.getHeight()/2 - RECT_OFFSET_Y[desiredShop][j]);
                drawRectangle(largeBitmap.getWidth()/2 + RECT_OFFSET_X[desiredShop][i], largeBitmap.getHeight()/2 - RECT_OFFSET_Y[desiredShop][j]);
                drawRectangle(largeBitmap.getWidth()/2 - RECT_OFFSET_X[desiredShop][i], largeBitmap.getHeight()/2 + RECT_OFFSET_Y[desiredShop][j]);
                drawRectangle(largeBitmap.getWidth()/2 + RECT_OFFSET_X[desiredShop][i], largeBitmap.getHeight()/2 + RECT_OFFSET_Y[desiredShop][j]);
            }
        }
    }

    public static Node closestNode(Node goal){
        Node closestNode =new Node("closest", -screenHeight, -screenWidth,0, 0);
        ArrayList<Node> neighbours = new ArrayList<>();

        for (int i = 0; i < LINE_OFFSET_Y[desiredShop].length; i++) {
            for (int j = 0; j < LINE_OFFSET_X[desiredShop].length; j++) {
                if(distance(currentPositionNode, nodes[j][i]) <= 170 && i % 2 == 0){
                    neighbours.add(nodes[j][i]);
                }
            }
        }

        for(int i = 0; i < neighbours.size(); i++){
            if(distance(neighbours.get(i), goal) < distance(closestNode, goal)){
                closestNode.copyNode(neighbours.get(i));
            }
        }

        return closestNode;
    }

    private static void routePlanner(Node goal) {
        Node start = new Node("", 0, 0, 0, 0);
        start.copyNode(closestNode(goal));

        Node currentNode = new Node(start.getName(), start.getX(), start.getY(), start.getDistanceTravelled(),
                start.getHeuristicDistance());
        Node previousNode = new Node(start.getName(), start.getX(), start.getY(), start.getDistanceTravelled(),
                start.getHeuristicDistance());

        drawLine(currentPositionNode, start);

        // set heuristicDistance
        for (int i = 0; i < LINE_OFFSET_Y[desiredShop].length; i++) {
            for (int j = 0; j < LINE_OFFSET_X[desiredShop].length; j++) {
                nodes[j][i].setHeuristicDistance((float) distance(nodes[j][i], goal));
            }
        }

        ArrayList<Node> neighbours = new ArrayList<>();

        while (!currentNode.getName().equals(goal.getName())) {
            int[] indexes = getIndex(currentNode);

            // add neighbours
            if (indexes[0] == 0 && indexes[1] == 0) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);

            } else if (indexes[0] == LINE_OFFSET_X[desiredShop].length - 1 && indexes[1] == 0) {
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);

            } else if (indexes[0] == LINE_OFFSET_X[desiredShop].length - 1 && indexes[1] == LINE_OFFSET_Y[desiredShop].length - 1) {
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);

            } else if (indexes[0] == 0 && indexes[1] == LINE_OFFSET_Y[desiredShop].length - 1) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);

            } else if (indexes[1] == 0) {
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);

            } else if (indexes[0] == LINE_OFFSET_X[desiredShop].length - 1 && indexes[1] % 2 == 0) {
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);

            } else if (indexes[1] == LINE_OFFSET_Y[desiredShop].length-1) {
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
                neighbours.add(nodes[indexes[0] + 1][indexes[1]]);
                neighbours.add(nodes[indexes[0] - 1][indexes[1]]);
                neighbours.add(nodes[indexes[0]][indexes[1] + 1]);
                neighbours.add(nodes[indexes[0]][indexes[1] - 1]);
            }

            previousNode.copyNode(currentNode);
            extendedList.add(currentNode.getName());

            // set distance travelled
            for (int i = 0; i < neighbours.size(); i++) {
                neighbours.get(i).setDistanceTravelled(
                        currentNode.getDistanceTravelled() + (float) distance(currentNode, neighbours.get(i)));
            }

            // choose best neighbour
            if (!isExtended(neighbours.get(0))) {
                currentNode.copyNode(neighbours.get(0));
            }
            for (int i = 1; i < neighbours.size(); i++) {
                if (!isExtended(neighbours.get(i)) && (neighbours.get(i).getDistanceTravelled()
                        + neighbours.get(i).getHeuristicDistance()) < (neighbours.get(0).getDistanceTravelled()
                        + neighbours.get(0).getHeuristicDistance())) {
                    currentNode.copyNode(neighbours.get(i));
                }
            }
            drawLine(previousNode, currentNode);
            neighbours.clear();
        }
        extendedList.clear();

    }

    public static void drawItems(){
        for(int i = 0; i < bagItems.size(); i++){
            drawCircle(nodes[bagItems.get(i).getX()][bagItems.get(i).getY()]);
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

    private static void drawRectangle(float centerX, float centerY){
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
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity3.this);
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

        if(defaultX  == 0 && defaultY == 0){
            defaultX = -(int)Math.round(location.getLongitude() * GPS_PRECISION);
            defaultY = -(int)Math.round(location.getLatitude() * GPS_PRECISION);
        }

        double currentX = -(int)Math.round(location.getLongitude() * GPS_PRECISION);
        double currentY = -(int)Math.round(location.getLatitude() * GPS_PRECISION);

        double deltaX = (defaultX - currentX);
        double deltaY = -(defaultY - currentY);

        defaultX = currentX;
        defaultY = currentY;

        try {
            xOffset +=(int)Math.round(deltaX);
            yOffset +=(int)Math.round(deltaY);
            Bitmap visibleBitmap = Bitmap.createBitmap(largeBitmap, xOffset, yOffset, visibleWidth, visibleHeight);
            iv_map.setImageBitmap(visibleBitmap);
            currentPositionNode.setX(currentPositionNode.getX() + (float)Math.round(deltaX));
            currentPositionNode.setY(currentPositionNode.getY() + (float)Math.round(deltaY));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Bolt területe elhagyva!" , Toast.LENGTH_SHORT).show();
        }

        cleanMap();

        if(itemNodes.isEmpty()){
            btn_nextItem.setVisibility(btn_nextItem.GONE);
        }else{
            try{
                routePlanner(itemNodes.get(0));

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    private static boolean isExtended(Node A) {
        for (int i = 0; i < extendedList.size(); i++) {
            if (A.getName().equalsIgnoreCase(extendedList.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static int[] getIndex(Node A) {
        int[] indexes = new int[2];
        for (int i = 0; i < LINE_OFFSET_Y[desiredShop].length; i++) {
            for (int j = 0; j < LINE_OFFSET_X[desiredShop].length; j++) {
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

    private static void cleanMap(){
        largeBitmap.recycle();
        largeBitmap = helperBitmap.copy(Bitmap.Config.ARGB_8888, true);
        drawShop();
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
        int degree = Math.round(sensorEvent.values[0]);
        currentPosition.setRotation(-degree);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}