package com.xiumeteo.homeostasis.model.migration;

import com.xiumeteo.homeostasis.model.DoctorLocation;

import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.internal.ColumnType;
import io.realm.internal.Table;

/**
 * Created by xiumeteo on 8/12/15.
 */
public class MigrationManager implements RealmMigration{


    @Override
    public long execute(Realm realm, long version) {
        if(version == 0){
            Table doctorLocationTable = realm.getTable(DoctorLocation.class);
            long index = doctorLocationTable.addColumn(ColumnType.STRING, "id");
            for(int i = 0; i < doctorLocationTable.size(); i++){
                doctorLocationTable.setString(i, index, "");
            }
            version++;
        }

        return version;
    }
}
