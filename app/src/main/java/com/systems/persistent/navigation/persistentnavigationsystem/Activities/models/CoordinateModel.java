package com.systems.persistent.navigation.persistentnavigationsystem.Activities.models;

import com.google.gson.annotations.SerializedName;

public class CoordinateModel {

    @SerializedName("x_coordinate")
    private double xCoordinate;

    @SerializedName("y_coordinate")
    private double yCoordinate;

    public double getX_Coordinate() {
        return xCoordinate;
    }

    public void setX_Coordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getY_Coordinate() {
        return yCoordinate;
    }

    public void setY_Coordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
