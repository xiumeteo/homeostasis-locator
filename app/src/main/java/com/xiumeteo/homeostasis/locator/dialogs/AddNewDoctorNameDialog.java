package com.xiumeteo.homeostasis.locator.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.xiumeteo.homeostasis.locator.R;
import com.xiumeteo.homeostasis.locator.listeners.AddNewDoctorNameDialogListener;

/**
 * Created by xiumeteo on 7/9/15.
 */
public class AddNewDoctorNameDialog extends DialogFragment {

    private EditText doctorName;

    public EditText getDoctorName() {
        return doctorName;
    }

    private AddNewDoctorNameDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (AddNewDoctorNameDialogListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddNewDoctorNameDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View inflate = inflater.inflate(R.layout.add_new_doctor_name_layout, null);
        doctorName = (EditText) inflate.findViewById(R.id.newDoctorName);
        builder.setView(inflate)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(AddNewDoctorNameDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddNewDoctorNameDialog.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();

        doctorName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
        doctorName.requestFocus();
        return dialog;
    }
}
