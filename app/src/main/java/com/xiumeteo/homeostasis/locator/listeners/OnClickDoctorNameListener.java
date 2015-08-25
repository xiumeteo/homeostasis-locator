package com.xiumeteo.homeostasis.locator.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.xiumeteo.homeostasis.locator.R;
import com.xiumeteo.homeostasis.locator.activities.MedicalLocator;

/**
 * Created by xiumeteo on 8/24/15.
 */
public class OnClickDoctorNameListener implements View.OnClickListener {

    private MedicalLocator medicalLocator;
    private OnLaunchDirectionsClickListener onLaunchListener;
    private OnDeleteDoctorClickListener onDeleteListener;
    private DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {}
    };

    public OnClickDoctorNameListener(MedicalLocator medicalLocator){
        this.medicalLocator = medicalLocator;
        onLaunchListener = new OnLaunchDirectionsClickListener(medicalLocator);
        onDeleteListener = new OnDeleteDoctorClickListener(medicalLocator);
    }

    public void onClick(View textView) {
        TextView selectedDoctorName = (TextView) textView;

        final String doctorName = selectedDoctorName.getText().toString();
        onLaunchListener.setDoctorName(doctorName);
        onDeleteListener.setDoctorName(doctorName);

        AlertDialog.Builder builder = new AlertDialog.Builder(medicalLocator);
        builder.setPositiveButton(R.string.get_directions, onLaunchListener);
        builder.setNegativeButton(R.string.delete, onDeleteListener);
        builder.setNeutralButton(R.string.cancel, onCancel);
        builder.setTitle(doctorName);

        builder.create().show();
    }
}
