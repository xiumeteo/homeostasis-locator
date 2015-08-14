package com.xiumeteo.homeostasis.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiumeteo on 8/12/15.
 */
public class DoctorLocationSyncRQ implements Serializable{

    private List<DoctorLocationEntity> data;

    public List<DoctorLocationEntity> getData() {
        return data;
    }

    public void setData(List<DoctorLocationEntity> data) {
        this.data = data;
    }
}
