package com.xiumeteo.homeostasis.locator.listeners;

import com.xiumeteo.homeostasis.locator.dialogs.AddNewDoctorNameDialog;

/**
 * Created by xiumeteo on 8/24/15.
 */ /* The activity that creates an instance of this dialog fragment must
        * implement this interface in order to receive event callbacks.
        * Each method passes the DialogFragment in case the host needs to query it. */
public interface AddNewDoctorNameDialogListener {
    public void onDialogPositiveClick(AddNewDoctorNameDialog dialog);
}
