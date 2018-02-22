package com.systems.persistent.navigation.persistentnavigationsystem.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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

import com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom.LocationUpdaterAsync;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.CoordinateModel;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.EndPointsModel;
import com.systems.persistent.navigation.persistentnavigationsystem.R;


public class Navigation extends AppCompatActivity implements View.OnClickListener,
        EditText.OnEditorActionListener, LocationUpdaterAsync.LocationUpdateCallback {

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

        // Initialize views.
        initViews();

        // Set the listeners.
        setListeners();
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


}
