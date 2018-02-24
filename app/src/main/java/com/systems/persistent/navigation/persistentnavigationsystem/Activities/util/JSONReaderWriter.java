package com.systems.persistent.navigation.persistentnavigationsystem.Activities.util;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Tirthankar on 24/2/18.
 */

public class JSONReaderWriter {
    private JSONObject packJSON(String label,String location) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("label", label);
            jsonObject.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void JSONWriter(String label, String location){
        JSONObject jsonObject = this.packJSON(label,location);
        FileWriter fileWriter = null;
        String path = "/sdcard/PSL_NAV/path.json";
        File mediaDir = new File("/sdcard/PSL_NAV/");
        if (!mediaDir.exists()){
            Log.d("Dir","entered");
            mediaDir.mkdir();
        }
        File file = new File(path);
        try {
            fileWriter = new FileWriter(file,true);
            fileWriter.write(jsonObject.toString()+"\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONArray readJSON(String filename) {
        String path = "/sdcard/PSL_NAV/"+filename.trim()+".json";
        File file = new File(path);
        FileReader fr = null;
        BufferedReader br = null;
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                Log.d("Line",line);
                jsonObject=new JSONObject(line);
                jsonArray.put(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  jsonArray;
    }
}
