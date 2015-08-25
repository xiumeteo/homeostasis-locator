package com.xiumeteo.homeostasis.locator.services.dao;

import com.xiumeteo.homeostasis.model.DoctorLocation;

import java.util.AbstractList;
import java.util.Collection;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by xiumeteo on 8/24/15.
 */
public class LocationsDao {

    private Realm realm;

    public LocationsDao(Realm realm){
        this.realm = realm;
    }

    public void save(Collection<DoctorLocation> locations) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(locations);
        realm.commitTransaction();
    }

    public Collection<DoctorLocation> getAll() {
        return realm.where(DoctorLocation.class).findAll();
    }

    public Collection<DoctorLocation> getAllWithoutId() {
        return realm.where(DoctorLocation.class)
                .equalTo("id", "")
                .findAll();
    }

    public void save(DoctorLocation location) {
        realm.beginTransaction();
        realm.copyToRealm(location);
        realm.commitTransaction();
    }

    public Collection<DoctorLocation> findBy(String nameToSearch) {
        Collection<DoctorLocation> doctorLocationsByName;
        if (nameToSearch.trim().isEmpty()) {
            doctorLocationsByName = getAll();
        } else {
            doctorLocationsByName = realm.where(DoctorLocation.class)
                    .contains("name", nameToSearch, false)
                    .findAll();
        }
        return doctorLocationsByName;
    }
}
