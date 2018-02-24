package com.systems.persistent.navigation.persistentnavigationsystem.Activities.models;

/**
 * Created by Tirthankar on 24/2/18.
 */

public class Route {
    String label;
    float _x;
    float _y;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float get_x() {
        return _x;
    }

    public void set_x(float _x) {
        this._x = _x;
    }

    public float get_y() {
        return _y;
    }

    public void set_y(float _y) {
        this._y = _y;
    }


    @Override
    public String toString() {
        return "Route{" +
                "label='" + label + '\'' +
                ", _x=" + _x +
                ", _y=" + _y +
                '}';
    }
}
