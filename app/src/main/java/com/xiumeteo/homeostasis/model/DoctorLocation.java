package com.xiumeteo.homeostasis.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xiumeteo on 7/10/15.
 */
public class DoctorLocation extends RealmObject {

    @PrimaryKey
    private String name;
    private String direction;
    private double latitude;
    private double longitude;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
