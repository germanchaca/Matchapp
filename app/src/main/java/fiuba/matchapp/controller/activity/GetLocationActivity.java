package fiuba.matchapp.controller.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

public abstract class GetLocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        disconnect();

        if (mLastLocation != null) {
            String mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            String mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            if ( (TextUtils.isEmpty(mLatitudeText)) && (TextUtils.isEmpty(mLongitudeText)) ){
                return;
            }
            storeInUserPreferences(mLatitudeText, mLongitudeText);

            Log.d("GetLocatinon: ", mLatitudeText + " " + mLongitudeText );
            //TODO: mandar estos datos al server (si no hay conexión no importa)
        }
        //TODO: el server va a tener que darse cuenta si no le setie nunca el long o latitud e interanmente no lo tenga en cuenta
    }

    private void storeInUserPreferences(String mLatitudeText, String mLongitudeText) {
        User user = MyApplication.getInstance().getPrefManager().getUser();
        if(user==null){
            return;
        }
        user.setLatitude(mLatitudeText);
        user.setLongitude(mLongitudeText);
        MyApplication.getInstance().getPrefManager().storeUser(user);
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
    }

    protected void connect(){
        mGoogleApiClient.connect();
    }
    protected void disconnect(){
        mGoogleApiClient.disconnect();
    }


}
