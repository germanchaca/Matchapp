package fiuba.matchapp.controller.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import fiuba.matchapp.R;
import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;
import fiuba.matchapp.utils.LocationController;

public abstract class GetLocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;

    private String TAG = GetLocationActivity.class.getSimpleName();
    protected double latitude,longitude;


    @Override
    public void onConnected(Bundle bundle) {
        if(!LocationController.checkPermission(this)) return;

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        locationServiceDisconnect();

        if (mLastLocation != null) {
            latitude= mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            if ((latitude == 0) && ((longitude == 0))) return;

            Log.d("GetLocatinon: ", latitude + " " + longitude );
        }
        //TODO: el server va a tener que darse cuenta si no le setie nunca el long o latitud e interanmente no lo tenga en cuenta
    }

    protected void storeUserLocationUpdate(){
        User user = MyApplication.getInstance().getPrefManager().getUser();
        if ( (latitude != user.getLatitude()) || (longitude!= user.getLongitude()) ){
            user.setLatitude(latitude);
            user.setLongitude(longitude);
            MyApplication.getInstance().getPrefManager().storeUser(user);
        }
    }
    protected void showPermissionDialog() {
        if (!LocationController.checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void initUserLastLocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(GetLocationActivity.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        checkPlayServices();
        showPermissionDialog();
    }

    protected void locationServiceConnect(){
        mGoogleApiClient.connect();
    }
    protected void locationServiceDisconnect(){
        mGoogleApiClient.disconnect();
    }

    protected boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, getResources().getString(R.string.check_google_play_service_error));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_google_play_service_error), Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }


}
