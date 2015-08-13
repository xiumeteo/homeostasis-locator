package com.xiumeteo.homeostasis.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiumeteo on 8/12/15.
 */
public class DoctorLocationSyncRS implements Serializable{

    private String status;
    private List<DoctorLocation> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DoctorLocation> getData() {
        return data;
    }

    public void setData(List<DoctorLocation> data) {
        this.data = data;
    }
}
