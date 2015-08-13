package com.xiumeteo.homeostasis.locator.listeners;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.xiumeteo.homeostasis.locator.activities.MedicalLocator;
import com.xiumeteo.homeostasis.model.DoctorLocation;

import io.realm.Realm;

import static android.widget.Toast.makeText;

/**
 * Created by xiumeteo on 7/11/15.
 */
public class OnDeleteDoctorClickListener implements OnClickListener {

    private MedicalLocator context;
    private String doctorName;
    private Realm realm;

    public OnDeleteDoctorClickListener(MedicalLocator context){
        this.context = context;
        this.realm = Realm.getDefaultInstance();
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        realm.beginTransaction();
        realm.where(DoctorLocation.class)
                .equalTo("name", doctorName)
                .findFirst()
                .removeFromRealm();
        realm.commitTransaction();

        context.renderDoctorLocations(realm.where(DoctorLocation.class).findAll());
    }
}
