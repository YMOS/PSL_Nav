package com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tirthankar on 24/2/18.
 */

public class LocationUpdater extends AsyncTask {

    Context context;
    public LocationUpdater(Context context) {
        //callBack = (LocationUpdateCallback) Activity;
        this.context = context;
    }

    public interface LocationUpdateCallback {
        void onLocationUpdate(String route);
    }

    public LocationUpdateCallback callBack;

    @Override
    protected Object doInBackground(Object[] params) {
        String myurl = "http://10.88.246.229:8003/location?group=semicolons98&user=semicolons98";
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(myurl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null)
                result += line;
            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();
            Log.d("Res",result);
            return result;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

       /* String myurl = "http://192.168.1.127:8003/"+params[1];
        if(params[1].toString().equals("location"))*/
        /*String myurl = "http://10.88.246.229:8003/"+params[1];
        Log.d("URL",myurl);
        try{
            URL url = new URL(myurl);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = (String) params[0];
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line="";
            while((line = bufferedReader.readLine())!= null)
                result += line;
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.d("Res",result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return  null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            String res = (String) o;
            JSONObject jsonObject = new JSONObject(res);
            /*res = jsonObject.getString("location");*/
            jsonObject = new JSONObject(String.valueOf(jsonObject.getJSONObject("users")));
            Log.d("Res2", String.valueOf(jsonObject));
            JSONArray jsonArray = jsonObject.getJSONArray("semicolons98");
            Log.d("Res3", String.valueOf(jsonArray));
            jsonObject= jsonArray.getJSONObject(0);
            Log.d("Res4", String.valueOf(jsonObject));
            res = jsonObject.getString("location");
           Log.d("Res6",res);
            callBack.onLocationUpdate(res);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
