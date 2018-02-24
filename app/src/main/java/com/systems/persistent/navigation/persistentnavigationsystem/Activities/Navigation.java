package com.systems.persistent.navigation.persistentnavigationsystem.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom.LocationUpdaterAsync;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.CoordinateModel;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.EndPointsModel;
import com.systems.persistent.navigation.persistentnavigationsystem.R;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.hardware.Sensor.TYPE_STEP_COUNTER;
import static android.hardware.Sensor.TYPE_ORIENTATION;
public class Navigation extends AppCompatActivity implements View.OnClickListener,
        EditText.OnEditorActionListener, LocationUpdaterAsync.LocationUpdateCallback {
    //sensor variables
    private SensorManager mSensorManager;
    private SensorEventListener listener;
    private Socket socket;
    private static final int SERVERPORT = 5000;
    private String SERVER_IP="10.88.230.13";
    private Map<String ,String > sensorData=new HashMap<>();
    private String value="";
    private Timer timer;

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

 //   DrawView drawView;
 //   Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        final Timer timer = new Timer();
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);


        // Initialize views.
        initViews();

        // Set the listeners.
        setListeners();

        //Initialize sensor
        initSensor(mSensorManager);

        //schedule sensor for 500ms
        schedulerSensor(timer);




    }

    public void schedulerSensor(Timer timer){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(socket!=null && socket.isConnected() && !sensorData.isEmpty())
                            try {
                                JSONObject jsonObject = new JSONObject(sensorData);
                                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                                out.println(jsonObject.toString());
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                    }
                }).start();
            }
        },0,500);
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

    private void initSensor(SensorManager mSensorManager){

        if(socket==null || !socket.isConnected())
            new Thread(new Runnable() {
                   @Override
                   public void run() {
                       InetAddress serverAddr = null;
                       try {
                           serverAddr = InetAddress.getByName(SERVER_IP);
                           socket = new Socket(serverAddr, SERVERPORT);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               }).start();
            mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);

    }

    private void startTracking() {
        EndPointsModel model = new EndPointsModel();
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
        }
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

        //sensor data is collected
        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                float dataset[];
                value = "";
                if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    float zOrient, xOrient;
                    zOrient = event.values[0];
                    xOrient = event.values[1];
                    sensorData.put("z_x", Float.toString(zOrient)+" "+Float.toString(xOrient));
                }

                if (sensor.getType() == TYPE_LINEAR_ACCELERATION) {
                    dataset = event.values;
                    for (float data : dataset) {
                        value += data + " ";
                    }
                    sensorData.put("Linear", value);
                }
                /*if (sensor.getType() == TYPE_STEP_COUNTER) {
                    dataset = event.values;
                    for (float data : dataset) {
                        value += data + " ";
                    }
                    sensorData.put("Step Counter", value);
                }*/
            }
        };
    }

    private void initViews() {
        ivLocationSwitch = findViewById(R.id.iv_location_switch);
        ivLocationMarker = findViewById(R.id.iv_location_marker);
        ivFloorPlan=findViewById(R.id.iv_floor_plan);
        edFrom = findViewById(R.id.ed_location_from);
        edTo = findViewById(R.id.ed_location_to);
        tilTo = findViewById(R.id.til_location_to);
        tilFrom = findViewById(R.id.til_location_from);
        rootView = findViewById(R.id.root_view);
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

    @Override
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
    }

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
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(listener);
        timer.cancel();
    }


}
