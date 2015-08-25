package com.xiumeteo.homeostasis.locator.services.sync;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.xiumeteo.homeostasis.locator.R;
import com.xiumeteo.homeostasis.locator.services.dao.LocationsDao;
import com.xiumeteo.homeostasis.model.DoctorLocation;
import com.xiumeteo.homeostasis.model.DoctorLocationEntity;
import com.xiumeteo.homeostasis.model.DoctorLocationSyncRQ;
import com.xiumeteo.homeostasis.model.DoctorLocationSyncRS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xiumeteo on 8/24/15.
 */
public class SyncManager {

    private final View view;
    private final LocationsDao locationsDao;
    private final SyncLocations syncLocations;

    public SyncManager(View view, LocationsDao locationsDao, SyncLocations syncLocations) {
        this.view = view;
        this.locationsDao = locationsDao;
        this.syncLocations = syncLocations;
    }

    public void syncDoctorsLocations(){
        final ProgressDialog progressDialog = new ProgressDialog(view.getContext(),
                R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setTitle(R.string.syncing);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Collection<DoctorLocation> doctorLocationsToSync = locationsDao.getAllWithoutId();

        if(doctorLocationsToSync.isEmpty()){
            progressDialog.dismiss();
            Snackbar.make(view, "Nada que sincronizar", Snackbar.LENGTH_SHORT).show();
            return;

        }

        List<DoctorLocationEntity> doctorLocationList = new ArrayList<>();
        for (DoctorLocation doctorLocation: doctorLocationsToSync){
            doctorLocationList.add(new DoctorLocationEntity(doctorLocation));
        }

        DoctorLocationSyncRQ doctorLocationSyncRQ = new DoctorLocationSyncRQ();
        doctorLocationSyncRQ.setData(doctorLocationList);

        syncLocations.sync(doctorLocationSyncRQ, new Callback<DoctorLocationSyncRS>() {

            @Override
            public void success(DoctorLocationSyncRS syncedObjects, Response response) {
                List<DoctorLocation> locationsSynced = new ArrayList<>();
                for (DoctorLocationEntity doctorLocationEntity : syncedObjects.getData()) {
                    locationsSynced.add(doctorLocationEntity.translate());
                }

                locationsDao.save(locationsSynced);
                progressDialog.dismiss();
                Snackbar.make(view, "Las ubicaciones han sido sincronizadas", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Snackbar.make(view, "Algo fue anduvo mal, intenta más tarde", Snackbar.LENGTH_SHORT).show();
            }
        });



    }
}
