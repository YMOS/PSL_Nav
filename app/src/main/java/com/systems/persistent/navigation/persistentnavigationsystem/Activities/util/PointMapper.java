package com.systems.persistent.navigation.persistentnavigationsystem.Activities.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tirthankar on 24/2/18.
 */

public class PointMapper{
    Map<String,List<String>> pointMapList = new HashMap<>();
    List<String> list=null;
    String key = null;
    public Map<String,List<String>> getPointMapping(){
        key = "IN,C31";
        list = new ArrayList<>();
        list.add("IN");
        list.add("P1");
        list.add("P2");
        list.add("C31");
        pointMapList.put(key,list);

        /*key = "IN,C31";
        list = new ArrayList<>();
        list.add("IN");
        list.add("C31");
        list.add("C30");
        list.add("C29");
        pointMapList.put(key,list);*/

        key="IN,MERCURY";
        list = new ArrayList<>();
        list.add("IN");
        list.add("P1");
        list.add("P5");
        list.add("P6");
        list.add("P8");
        list.add("MERCURY");
        pointMapList.put(key,list);

        return pointMapList;
    }

    public List<String> getDirections(String source,String destination){
        Map<String,List<String>> map = this.getPointMapping();
        Log.d("Map", String.valueOf(map));
        String key = source.toUpperCase()+","+destination.toUpperCase();
        Log.d("Map",key);
        Log.d("Map", String.valueOf(map.get(key)));
        return map.get(key);
    }
}