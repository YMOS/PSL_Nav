package com.systems.persistent.navigation.persistentnavigationsystem.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.systems.persistent.navigation.persistentnavigationsystem.R;

public class Navigation extends AppCompatActivity {

    private ImageView loc_mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        loc_mark = (ImageView)findViewById(R.id.loc_mark);
        int i=50;
        float screenWidth = getResources().getDisplayMetrics().widthPixels;

        while(i<600) {
            moveImageView(loc_mark, i, 0, 50000);
            i=i+10;
        }

       /*
        moveImageView(loc_mark,100,0,500);
        */

    }



    public void moveImageView(View view, float toX, float toY, int duration){

        view.animate()
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationX(toX)
                .translationY(toY)
                .setDuration(duration);
    }

}
