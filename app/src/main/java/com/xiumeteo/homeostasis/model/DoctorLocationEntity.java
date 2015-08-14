package com.xiumeteo.homeostasis.model;

import java.io.Serializable;


/**
 * Created by xiumeteo on 8/13/15.
 */
public class DoctorLocationEntity implements Serializable {

    private String name;
    private String id;
    private String direction;

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    private double[] location;

    public DoctorLocationEntity(DoctorLocation doctorLocation) {
        this.name = doctorLocation.getName();
        this.id = doctorLocation.getId();
        this.direction = doctorLocation.getDirection();
        this.location = new double[]{doctorLocation.getLatitude(), doctorLocation.getLongitude()};
    }

    public DoctorLocationEntity(){}

    public String getName() {
        return name;
    }

    public DoctorLocation translate(){
        DoctorLocation doctorLocation = new DoctorLocation();
        doctorLocation.setLongitude(location[1]);
        doctorLocation.setName(name);
        doctorLocation.setLatitude(location[0]);
        doctorLocation.setDirection(direction);
        doctorLocation.setId(id);
        return doctorLocation;
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

