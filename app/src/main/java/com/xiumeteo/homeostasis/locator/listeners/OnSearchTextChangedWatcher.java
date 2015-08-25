package com.xiumeteo.homeostasis.locator.listeners;

import android.text.Editable;
import android.text.TextWatcher;

import com.xiumeteo.homeostasis.locator.activities.MedicalLocator;

/**
 * Created by xiumeteo on 8/24/15.
 */
public class OnSearchTextChangedWatcher implements TextWatcher {

    private MedicalLocator medicalLocator;

    public OnSearchTextChangedWatcher(MedicalLocator medicalLocator){
        this.medicalLocator = medicalLocator;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        medicalLocator.searchForDoctors();
    }
}
