package com.systems.persistent.navigation.persistentnavigationsystem.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom.LocationUpdater;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom.LocationUpdaterAsync;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom.TargetPointsReceiver;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.CoordinateModel;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.EndPointsModel;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.Route;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.util.JSONReaderWriter;
import com.systems.persistent.navigation.persistentnavigationsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class Navigation extends AppCompatActivity implements View.OnClickListener,
        EditText.OnEditorActionListener,TargetPointsReceiver.TargetPointReceiverAsync {

    final int READ_EXTERNAL_STORAGE = 1;
    final int READ_WIFI_STATE = 2;

    //Location Updater
    private LocationUpdater locationUpdater=null;

    //For training
    private String location="";
    //JSONReaderWriter
    private JSONReaderWriter jsonReaderWriter = new JSONReaderWriter();

    //Handler
    private Handler handler;
    //Wifi manager
    private WifiManager mWifiManager;

    //Text to Speech
    TextToSpeech textToSpeech;

    // Texts.
    private TextInputLayout tilFrom;
    private TextInputLayout tilTo;
    private EditText edFrom;
    private EditText edTo;

    //Switcher.
    private ImageView ivLocationSwitch;

    //Marker.
    private ImageView ivLocationMarker;

    //Floor plan view
    private  ImageView ivFloorPlan;

    //Root view.
    private CoordinatorLayout rootView;

    //Bitmaps for canvas processing
    private Bitmap mutableBitmap;
    private Bitmap tempBitmap;

    //Strings.
    private String strFrom;
    private String strTo;

    //List of x,y coordinates as per screen
    Map<String,Route> targetPoints;

    Map<String,Route> routePoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //Ask for Permissions
        askPermission();
        //Receive Targets
        receiveTarget();

        //Receive Points
        //receivePoints();

        // Initialize views.
        initViews();

        //start Tracking
        startTracking();

        // Set the listeners.
        setListeners();
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},READ_EXTERNAL_STORAGE);
        }else{
            //Permission exists
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Navigation.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void receiveTarget() {
        new TargetPointsReceiver(this,jsonReaderWriter.readJSON("target"),"target").execute();
    }

    private  void  receivePoints(){
        new TargetPointsReceiver(this,jsonReaderWriter.readJSON("path"),"path").execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_location_switch:
                if (Utility.isValidString(strFrom)
                        && Utility.isValidString(strTo)) {
                    edFrom.setText(strTo);
                    edTo.setText(strFrom);
                } else {
                    showSnackBar(getString(R.string.snackbar_location_switch_error),
                            Snackbar.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (v.getId() == R.id.ed_location_from) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                if (edFrom.getText().toString().isEmpty()) {
                    tilFrom.setError(getString(R.string.til_error));
                    edFrom.setText("");

                    // Indicate that the view has consumed the event.
                    return true;
                } else {
                    if (Utility.isValidString(edFrom.getText().toString())) {
                        strFrom = edFrom.getText().toString();

                        //Transfer control to next editText.
                        edFrom.setNextFocusDownId(edTo.getId());
                    }
                }
            }
        } else if (v.getId() == R.id.ed_location_to) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (edTo.getText().toString().isEmpty()) {
                    tilTo.setError(getString(R.string.til_error));
                    edTo.setText("");

                    return true;
                } else {
                    if (Utility.isValidString(edTo.getText().toString())) {
                        strTo = edTo.getText().toString();

                        // Start the tracking procedure.
                        startTracking();
                    }
                }
            }
        }

        return false;
    }

    private void startTracking() {
        /*EndPointsModel model = new EndPointsModel();
        model.setStrTo(strTo);
        model.setStrFrom(strFrom);

        // Start the Async thread here.
        try {
            (new LocationUpdaterAsync(this)).execute(model);
        } catch (Exception e) {
            // Catching a broad-level exception.
            showSnackBar(e.getMessage() != null ? e.getMessage()
                            : getString(R.string.something_went_wrong),
                    Snackbar.LENGTH_LONG);
        }*/
        handler = new Handler();
        handler.post(runnable);
    }

    private void showSnackBar(String message, int duration) {
        Snackbar.make(
                rootView,
                message,
                duration
        ).show();
    }

    private void setListeners() {
        ivLocationSwitch.setOnClickListener(this);
        ivLocationMarker.setOnClickListener(this);
        edFrom.setOnEditorActionListener(this);
        edTo.setOnEditorActionListener(this);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            /*Long timeStamp = System.currentTimeMillis()/1000;
            List<ScanResult> mResults = mWifiManager.getScanResults();
            JSONObject wifiResults = new JSONObject();
            JSONArray wifiJsonArray=new JSONArray();

            for (ScanResult result : mResults) {
                try {
                    wifiResults.put("mac", result.BSSID);
                    wifiResults.put("rssi", result.level);
                    wifiJsonArray.put(wifiResults);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject=new JSONObject();

            try {
                jsonObject.put("group","semicolons98");
                jsonObject.put("username","semicolons98");
                jsonObject.put("location","check");
                jsonObject.put("time", timeStamp);
                jsonObject.put("wifi-fingerprint", wifiJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Data",jsonObject.toString());*/
            locationUpdater=new LocationUpdater(getApplicationContext());
            locationUpdater.execute("abc","track");
            locationUpdater.callBack =new LocationUpdater.LocationUpdateCallback() {
                @Override
                public void onLocationUpdate(String route) {
                    Log.d("Res7",route);
                    //textToSpeech.speak(route,TextToSpeech.QUEUE_FLUSH,null);
                    if (route != null) {
                        Log.d("Route", route);
                        edFrom.setText(route);
                        Route currentRoute = targetPoints.get(route);
                        moveImageView(ivLocationMarker, currentRoute.get_x(), currentRoute.get_y(), 100);
                    }
                }
            };

            handler.postDelayed(runnable, 10000);
        }
    };


    private void initViews() {
        ivLocationSwitch = findViewById(R.id.iv_location_switch);
        ivLocationMarker = findViewById(R.id.iv_location_marker);
        ivFloorPlan=findViewById(R.id.iv_floor_plan);

        ivFloorPlan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(Navigation.this, "X:"+motionEvent.getX()+" Y:"+motionEvent.getY(), Toast.LENGTH_SHORT).show();
                float x = motionEvent.getX()-105;
                float y = motionEvent.getY()-105;
                location=x+","+y;
                jsonReaderWriter.JSONWriter(edFrom.getText().toString(),location);
                moveImageView(ivLocationMarker,x,y,300);
                return false;
            }
        });

        edFrom = findViewById(R.id.ed_location_from);
        edTo = findViewById(R.id.ed_location_to);
        tilTo = findViewById(R.id.til_location_to);
        tilFrom = findViewById(R.id.til_location_from);
        rootView = findViewById(R.id.root_view);
        mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void moveMarker() {
        // Core mechanics - Original contributor: @www.github.com/YMOS/
        int i=50;

        while(i<600) {
            moveImageView(ivLocationMarker, i, 0, 50000);
            i=i+10;
        }
    }


    @SuppressWarnings("SameParameterValue")
    private void moveImageView(View view, float toX, float toY, int duration) {
        view.animate()
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationX(toX)
                .translationY(toY)
                .setDuration(duration);
    }

    /*@Override
    public void onLocationUpdated(CoordinateModel model) {

        //TODO: Update the UI here.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // Display general alert.
                showSnackBar(getString(R.string.snackbar_update_location),
                        Snackbar.LENGTH_SHORT);
                // prepare floor map as canvas
                prepBitmap();
                // Draw the navigation route
                drawRoute(50,150, 600, 150);
                // Call the marker update method here
                moveMarker();
            }
        });
    }*/

    private void drawRoute(int startX, int startY, int stopX, int stopY) {

        Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(mutableBitmap,0,0,null);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        //canvas.drawLine(200, 0, 0, 200, paint);
        ivFloorPlan.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
    }

    private void prepBitmap(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.floor_plan);
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        tempBitmap = Bitmap.createBitmap(mutableBitmap.getWidth(), mutableBitmap.getHeight(), Bitmap.Config.RGB_565);
    }


    @Override
    public void onPointsReceived(Map<String, Route> targetMap, String type) {
        if(type.equals("target")){
            targetPoints = targetMap;
            /*key = 0;
            final Set<String> keyset = targetPoints.keySet();
            final String[] keysetArr = keyset.toArray(new String[0]);
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(key<keyset.size()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //edFrom.setText(targetPoints.get(keysetArr[key]).getLabel());
                            }
                        });
                        Log.d("X", String.valueOf(targetPoints.get(keysetArr[key]).get_x()));
                        moveImageView(ivLocationMarker, targetPoints.get(keysetArr[key]).get_x(), targetPoints.get(keysetArr[key]).get_y(), 100);
                        key++;
                    }
                }
            },0,1000);*/
            /*for (String key:keyset){
                moveImageView(ivLocationMarker,targetPoints.get(key).get_x(),targetPoints.get(key).get_y(),100);
            }*/
            Log.d("Targets", String.valueOf(targetPoints));
        }else{
            routePoints = targetMap;
            Log.d("Points", String.valueOf(routePoints));
        }
    }

}
