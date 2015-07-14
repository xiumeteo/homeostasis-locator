package com.xiumeteo.homeostasis.locator.listeners;

import android.text.Editable;
import android.text.TextWatcher;

import com.xiumeteo.homeostasis.locator.activities.MedicalLocator;

/**
 * Created by xiumeteo on 7/11/15.
 */
public class OnSearchTextChanged implements TextWatcher {

    private MedicalLocator context;

    public OnSearchTextChanged(MedicalLocator context) {
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        context.searchForDoctors();
    }
}
