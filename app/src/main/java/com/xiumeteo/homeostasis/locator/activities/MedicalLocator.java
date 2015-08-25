package com.xiumeteo.homeostasis.locator.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.xiumeteo.homeostasis.locator.R;
import com.xiumeteo.homeostasis.locator.dialogs.AddNewDoctorNameDialog;
import com.xiumeteo.homeostasis.locator.listeners.AddNewDoctorNameDialogListener;
import com.xiumeteo.homeostasis.locator.listeners.OnClickDoctorNameListener;
import com.xiumeteo.homeostasis.locator.listeners.OnSearchTextChangedWatcher;
import com.xiumeteo.homeostasis.locator.services.dao.LocationsDao;
import com.xiumeteo.homeostasis.locator.services.sync.SyncLocations;
import com.xiumeteo.homeostasis.locator.services.sync.SyncManager;
import com.xiumeteo.homeostasis.model.DoctorLocation;

import java.util.Collection;

import io.realm.Realm;
import retrofit.RestAdapter;


public class MedicalLocator extends AppCompatActivity implements
        AddNewDoctorNameDialogListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private LinearLayout resultsLayout;
    private EditText searchField;
    private static String NO_RESULTS_MESSAGE;
    private GoogleApiClient locationsServiceClient;
    private Location lastLocation;
    private OnClickDoctorNameListener onClickDoctorNameListener;
    private SyncManager syncManager;
    private LocationsDao locationsDao;
    private View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_locator);
        content = findViewById(android.R.id.content);

        buildGoogleApiClient();
        locationsServiceClient.connect();
        onClickDoctorNameListener = new OnClickDoctorNameListener(this);
        Realm realm = Realm.getDefaultInstance();
        locationsDao = new LocationsDao(realm);

        resultsLayout = (LinearLayout) this.findViewById(R.id.searchResults);
        searchField = (EditText) findViewById(R.id.searchDoctorName);
        NO_RESULTS_MESSAGE = getString(R.string.no_results);
        searchField.addTextChangedListener(new OnSearchTextChangedWatcher(this));

        Collection<DoctorLocation> doctorLocations = locationsDao.getAll();
        renderDoctorLocations(doctorLocations);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://peaceful-falls-4143.herokuapp.com/")
                .build();

        SyncLocations syncLocations = restAdapter.create(SyncLocations.class);
        syncManager = new SyncManager(content, locationsDao, syncLocations);

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
                syncLocations();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void syncLocations() {
        syncManager.syncDoctorsLocations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationsServiceClient.isConnected()) {
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

    public void renderDoctorLocations(Collection<DoctorLocation> doctorLocations) {
        resultsLayout.removeAllViews();

        if (doctorLocations.isEmpty()) {
            resultsLayout.addView(attachMessageToResults(NO_RESULTS_MESSAGE));
        } else {
            for (DoctorLocation location : doctorLocations) {

                TextView doctorName = attachMessageToResults(location.getName());
                doctorName.setTextAppearance(this, R.style.TextAppearance_AppCompat_Button);
                doctorName.setOnClickListener(onClickDoctorNameListener);
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
        Collection<DoctorLocation> doctorLocationsByName = locationsDao.findBy(nameToSearch);
        renderDoctorLocations(doctorLocationsByName);
    }


    @Override
    public void onDialogPositiveClick(AddNewDoctorNameDialog dialog) {
        String doctorName = dialog.getDoctorName().getText().toString();
        if(doctorName.trim().isEmpty()){
            Snackbar.make(content, "Debes poner un nombre", Snackbar.LENGTH_SHORT).show();
            return;
        }
        DoctorLocation location = new DoctorLocation();
        location.setName(doctorName);
        if (lastLocation != null) {
            location.setLatitude(lastLocation.getLatitude());
            location.setLongitude(lastLocation.getLongitude());
        } else {
            Snackbar.make(content, R.string.location_not_available, Snackbar.LENGTH_SHORT).show();
            return;
        }
        TextView doctorNameView = attachMessageToResults(location.getName());
        resultsLayout.addView(doctorNameView);
        locationsDao.save(location);
        System.out.println(location.getName() + " lat:" + location.getLatitude() +
                " lon:" + location.getLongitude());
        Snackbar.make(content, doctorName + getString(R.string.saved), Snackbar.LENGTH_SHORT).show();
        searchField.setText("");
        searchForDoctors();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Snackbar.make(content, "ACQUIRING LOCATION...", Snackbar.LENGTH_SHORT).show();
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
        Snackbar.make(content, connectionResult.toString(), Snackbar.LENGTH_LONG).show();
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            lastLocation = location;
            Snackbar.make(content, "LOCATION ACQUIRED", Snackbar.LENGTH_LONG);
        }
    }

    public void openSaveNewDoctor(View view) {
        DialogFragment newFragment = new AddNewDoctorNameDialog();
        newFragment.show(getFragmentManager(), "doctorName");
    }
}
