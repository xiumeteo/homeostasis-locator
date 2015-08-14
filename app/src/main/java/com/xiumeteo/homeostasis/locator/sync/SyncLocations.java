package com.xiumeteo.homeostasis.locator.sync;

import com.xiumeteo.homeostasis.model.DoctorLocation;
import com.xiumeteo.homeostasis.model.DoctorLocationSyncRQ;
import com.xiumeteo.homeostasis.model.DoctorLocationSyncRS;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by xiumeteo on 8/9/15.
 */
public interface SyncLocations {

    @POST("/sync")
    void sync(@Body DoctorLocationSyncRQ locations, Callback<DoctorLocationSyncRS> callback);

}
