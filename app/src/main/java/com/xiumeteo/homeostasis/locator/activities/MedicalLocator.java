package com.xiumeteo.homeostasis.locator.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import com.google.android.gms.location.LocationListener;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.xiumeteo.homeostasis.locator.R;
import com.xiumeteo.homeostasis.locator.dialogs.AddNewDoctorNameDialog;
import com.xiumeteo.homeostasis.locator.listeners.OnDeleteDoctorClickListener;
import com.xiumeteo.homeostasis.locator.listeners.OnLaunchDirectionsClickListener;
import com.xiumeteo.homeostasis.locator.sync.SyncLocations;
import com.xiumeteo.homeostasis.model.DoctorLocation;
import com.xiumeteo.homeostasis.model.DoctorLocationEntity;
import com.xiumeteo.homeostasis.model.DoctorLocationSyncRQ;
import com.xiumeteo.homeostasis.model.DoctorLocationSyncRS;
import com.xiumeteo.homeostasis.model.migration.MigrationManager;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class MedicalLocator extends ActionBarActivity implements
        AddNewDoctorNameDialog.AddNewDoctorNameDialogListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private LinearLayout resultsLayout;
    private Realm realm;
    private EditText searchField;
    private String NO_RESULTS_MESSAGE ;
    private GoogleApiClient locationsServiceClient;
    private Location lastLocation;
    private SyncLocations syncLocations;

    private DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {}
    };

    private OnLaunchDirectionsClickListener onLaunchListener;

    private OnDeleteDoctorClickListener onDeleteListener;

    private View.OnClickListener onClickListenerForTextView = new View.OnClickListener() {
        @Override
        public void onClick(View textView) {
            TextView selectedDoctorName = (TextView)textView;

            final String doctorName = selectedDoctorName.getText().toString();
            onLaunchListener.setDoctorName(doctorName);
            onDeleteListener.setDoctorName(doctorName);

            AlertDialog.Builder builder = new AlertDialog.Builder(MedicalLocator.this);
            builder.setPositiveButton(R.string.get_directions, onLaunchListener);
            builder.setNegativeButton(R.string.delete, onDeleteListener);
            builder.setNeutralButton(R.string.cancel, onCancel);
            builder.setTitle(doctorName);

            builder.create().show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_locator);
        buildGoogleApiClient();
        locationsServiceClient.connect();
        onLaunchListener = new OnLaunchDirectionsClickListener(this);
        onDeleteListener = new OnDeleteDoctorClickListener(this);

        realm = Realm.getDefaultInstance();

        resultsLayout = (LinearLayout) this.findViewById(R.id.searchResults);
        searchField = (EditText) findViewById(R.id.searchDoctorName);
        NO_RESULTS_MESSAGE = getString(R.string.no_results);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchForDoctors();
            }
        });


        RealmResults<DoctorLocation> doctorLocations = realm.where(DoctorLocation.class).findAll();

        renderDoctorLocations(doctorLocations);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://peaceful-falls-4143.herokuapp.com/")
                .build();
        
        syncLocations = restAdapter.create(SyncLocations.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                syncDoctorsLocations();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(locationsServiceClient.isConnected()){
            startLocationRequest();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                locationsServiceClient, this);
    }



    protected synchronized void buildGoogleApiClient() {
        locationsServiceClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void renderDoctorLocations(AbstractList<DoctorLocation> doctorLocations) {
        resultsLayout.removeAllViews();

        if (doctorLocations.isEmpty()) {
            resultsLayout.addView(attachMessageToResults(NO_RESULTS_MESSAGE));
        } else {
            for (DoctorLocation location : doctorLocations) {

                TextView doctorName = attachMessageToResults(location.getName());
                doctorName.setTextAppearance(this, R.style.TextAppearance_AppCompat_Medium);
                doctorName.setOnClickListener(onClickListenerForTextView);
                System.out.println(location.getName());
                resultsLayout.addView(doctorName);

            }
        }
    }

    private TextView attachMessageToResults(String content) {
        TextView doctorName = new TextView(this);
        doctorName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        doctorName.setText(content);
        doctorName.setTextAppearance(this, R.style.TextAppearance_AppCompat_Medium);
        return doctorName;
    }


    public void searchForDoctors() {
        String nameToSearch = searchField.getText().toString();

        AbstractList<DoctorLocation> doctorLocationsByName;

        if (nameToSearch.trim().isEmpty()) {
            doctorLocationsByName = realm.where(DoctorLocation.class).findAll();
        } else {
            doctorLocationsByName = realm.where(DoctorLocation.class)
                    .contains("name", nameToSearch, false)
                    .findAll();
        }


        renderDoctorLocations(doctorLocationsByName);
    }

    public void openSaveNewDoctor(View view) {
        DialogFragment newFragment = new AddNewDoctorNameDialog();
        newFragment.show(getFragmentManager(), "doctorName");
    }

    @Override
    public void onDialogPositiveClick(AddNewDoctorNameDialog dialog) {
        Editable doctorName = dialog.getDoctorName().getText();

        DoctorLocation location = new DoctorLocation();
        location.setName(doctorName.toString());
        if(lastLocation != null){
            location.setLatitude(lastLocation.getLatitude());
            location.setLongitude(lastLocation.getLongitude());
        }else{
            //makeText(getApplicationContext(), R.string.location_not_available, LENGTH_SHORT).show();
            //return;
        }

        TextView doctorNameView = attachMessageToResults(location.getName());

        resultsLayout.addView(doctorNameView);

        realm.beginTransaction();
        realm.copyToRealm(location);
        realm.commitTransaction();

        System.out.println(location.getName()+" lat:"+location.getLatitude()+
                " lon:"+location.getLongitude());

        Snackbar.make(findViewById(android.R.id.content), doctorName + getString(R.string.saved), Snackbar.LENGTH_SHORT).show();
        searchField.setText("");
        searchForDoctors();

    }

    @Override
    public void onConnected(Bundle bundle) {
        Snackbar.make(findViewById(android.R.id.content), "ACQUIRING LOCATION...", Snackbar.LENGTH_SHORT).show();
        startLocationRequest();
    }

    private void startLocationRequest() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationsServiceClient, createLocationRequest(), this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.err.println(connectionResult.getErrorCode());
//        Snackbar.make(findViewById(android.R.id.content), connectionResult.toString(), Snackbar.LENGTH_LONG).show();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            lastLocation = location;
            Snackbar.make(findViewById(android.R.id.content), "LOCATION ACQUIRED", Snackbar.LENGTH_LONG);
        }
    }

    public void syncDoctorsLocations(){
        RealmResults<DoctorLocation> doctorLocationsToSync = realm.where(DoctorLocation.class)
                .equalTo("id", "")
                .findAll();

        if(doctorLocationsToSync.isEmpty()){
            Snackbar.make(findViewById(android.R.id.content), "Nada que sincronizar", Snackbar.LENGTH_SHORT).show();
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

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(locationsSynced);
                realm.commitTransaction();

                Snackbar.make(MedicalLocator.this.findViewById(android.R.id.content), "Las ubicaciones han sido sincronizadas", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar.make(MedicalLocator.this.findViewById(android.R.id.content), "Algo fue anduvo mal, intenta más tarde", Snackbar.LENGTH_SHORT).show();
            }
        });



    }

}
