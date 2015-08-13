package com.xiumeteo.homeostasis.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiumeteo on 8/12/15.
 */
public class DoctorLocationSyncRQ implements Serializable{

    private List<DoctorLocation> data;

    public List<DoctorLocation> getData() {
        return data;
    }

    public void setData(List<DoctorLocation> data) {
        this.data = data;
    }
}
