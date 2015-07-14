package com.xiumeteo.homeostasis.locator.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.xiumeteo.homeostasis.locator.R;
import com.xiumeteo.homeostasis.model.DoctorLocation;

import io.realm.Realm;

import static android.widget.Toast.makeText;

/**
 * Created by xiumeteo on 7/11/15.
 */
public class OnLaunchDirectionsClickListener implements OnClickListener {


    private Context context;
    private String doctorName;

    public OnLaunchDirectionsClickListener(Context context){
        this.context = context;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        DoctorLocation doctorLocation = Realm.getInstance(context).where(DoctorLocation.class)
                .equalTo("name", doctorName)
                .findFirst();

        if(doctorLocation!=null) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="
                            + doctorLocation.getLatitude() + "," + doctorLocation.getLongitude()));
            context.startActivity(intent);
        }else{
            makeText(context, R.string.something_weird_happened, Toast.LENGTH_LONG);
        }
    }
}
