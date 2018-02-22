package com.systems.persistent.navigation.persistentnavigationsystem.Activities.models;

import com.google.gson.annotations.SerializedName;

public class EndPointsModel {

    @SerializedName("to")
    private String strTo;

    @SerializedName("from")
    private String strFrom;

    public String getStrTo() {
        return strTo;
    }

    public void setStrTo(String strTo) {
        this.strTo = strTo;
    }

    public String getStrFrom() {
        return strFrom;
    }

    public void setStrFrom(String strFrom) {
        this.strFrom = strFrom;
    }
}
