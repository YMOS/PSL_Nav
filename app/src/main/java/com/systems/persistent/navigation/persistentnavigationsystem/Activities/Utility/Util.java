package com.systems.persistent.navigation.persistentnavigationsystem.Activities.Utility;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by ymos on 19/2/18.
 */

public class Util {



        public Intent switchActivity(Context context, Class target)
        {
            Intent intent=new Intent(context,target);
            return intent;
        }

        public void CreateToast(String text,Context context)
        {
            int duration= Toast.LENGTH_SHORT;
            Toast toast=Toast.makeText(context,text,duration);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();

        }


}
