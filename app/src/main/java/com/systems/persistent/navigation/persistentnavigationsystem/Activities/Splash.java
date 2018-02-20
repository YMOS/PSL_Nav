package com.systems.persistent.navigation.persistentnavigationsystem.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.systems.persistent.navigation.persistentnavigationsystem.Activities.Utility.Util;
import com.systems.persistent.navigation.persistentnavigationsystem.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        final Handler handler = new Handler();//holding the splash screen for 2 second
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CallConfig_act();
            }

        }, 2000);//Calling the next activity after 2 second
    }

    private void CallConfig_act(){

        startActivity(new Util().switchActivity(this, Navigation.class));
        Splash.this.finish();
    }

}
