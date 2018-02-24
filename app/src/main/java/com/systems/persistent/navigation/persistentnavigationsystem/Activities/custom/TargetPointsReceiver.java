package com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Tirthankar on 24/2/18.
 */

public class TargetPointsReceiver extends AsyncTask {

    Context context;
    JSONArray pointArray;
    String type;
    public TargetPointsReceiver(Context context, JSONArray pointArray, String type){
        this.context = context;
        callback = (TargetPointReceiverAsync) context;
        this.pointArray = pointArray;
        this.type = type;
    }

    public interface TargetPointReceiverAsync{
        void onPointsReceived(Map<String, Route> targetMap, String type);
    }

    public TargetPointReceiverAsync callback;

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Map<String, Route> routeMap = new HashMap<>();
            JSONObject routeObject = null;
            for(int i=0;i<pointArray.length();i++){
                routeObject = pointArray.getJSONObject(i);
                Route route = new Route();
                route.setLabel(routeObject.getString("label"));
                String[] ordinates = routeObject.getString("location").split(",");
                route.set_x(Float.parseFloat(ordinates[0]));
                route.set_y(Float.parseFloat(ordinates[1]));
                routeMap.put(route.getLabel(),route);
                Log.d("Loc", String.valueOf(routeMap));
            }
            return routeMap;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(o!=null){
            Map<String, Route> map = (Map<String, Route>) o;
            Log.d("Loc", String.valueOf(map));
            callback.onPointsReceived(map,type);
        }
    }
}

