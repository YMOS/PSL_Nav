package com.systems.persistent.navigation.persistentnavigationsystem.Activities.custom;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.CoordinateModel;
import com.systems.persistent.navigation.persistentnavigationsystem.Activities.models.EndPointsModel;



import java.util.HashMap;
import java.util.Map;

public class LocationUpdaterAsync extends AsyncTask<EndPointsModel, Void, CoordinateModel> {

    private final String strTAG = getClass().getSimpleName();
    private CoordinateModel responseModel;

    public interface LocationUpdateCallback {
        void onLocationUpdated(CoordinateModel model);
    }

    private LocationUpdateCallback callBack;

    public LocationUpdaterAsync(AppCompatActivity hActivity) {
        callBack = (LocationUpdateCallback) hActivity;
    }

    @Override
    protected CoordinateModel doInBackground(EndPointsModel... endPointsModels) {

        final Gson requestGson = new GsonBuilder().serializeNulls().create();

        try {

            com.android.volley.Response.Listener<JsonObject> responseListener = new com.android.volley.Response.Listener<JsonObject>() {
                @Override
                public void onResponse(JsonObject response) {

                    //TODO: Check for success/failure

                    // On n/w response:
                    // Simply convert the response parameters to the Serialized model.
                    // Supposing response is the following:
                    // {"x_coordinate": 23.112, "y_coordinate": 11.1141}";
                    LocationUpdaterAsync.this.responseModel = requestGson
                            .fromJson(response, CoordinateModel.class);;
                }
            };

            com.android.volley.Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null && error.getMessage() != null) {
                        Log.e(strTAG, error.getMessage());
                    } else {
                        Log.e(strTAG, "Something went wrong");
                    }
                }
            };

            String payloadStr = requestGson.toJson(endPointsModels[0],
                    EndPointsModel.class);

            //TODO: Add your URL here.
            String serverUrl = "<your_url_here>";

            //TODO: Send API request with above payload here.
            GsonRequest<JsonObject> request = new GsonRequest<>(
                    Request.Method.POST,
                    serverUrl,
                    new TypeToken<JsonObject>(){}.getType(),
                    requestGson,
                    responseListener,
                    errorListener

            );

            Map<String, String> headers = new HashMap<>();
            //TODO: Add all headers here.

            JsonObject payload = new JsonObject();
            //TODO: Add the payload here.

            request.setHeaderParams(headers);
            request.setBodyParams(payload);

        } catch (Exception e) {
            e.printStackTrace();
            //TODO handle the exception(s) here.
        }

        return null;
    }

    @Override
    protected void onPostExecute(CoordinateModel coordinateModel) {
        super.onPostExecute(coordinateModel);

        // Issuing a call back to the activity.
        //TODO: Caution - might be null. Check appropriately!
        callBack.onLocationUpdated(responseModel);
    }
}
