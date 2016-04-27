package fiuba.matchapp.activity;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

/**
 * Created by german on 4/26/2016.
 */
public abstract class GetLocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LOCATION= 1;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    public void onConnected(Bundle bundle) {
        //Se supone que si ya se tildo como granted no se vuelve a pedir
        ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION,REQUEST_LOCATION);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            String mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            Toast.makeText(getBaseContext(), mLatitudeText + " " + mLongitudeText, Toast.LENGTH_LONG).show();

            User user = MyApplication.getInstance().getPrefManager().getUser();
            user.setLatitude(mLatitudeText);
            user.setLongitude(mLongitudeText);
            MyApplication.getInstance().getPrefManager().storeUser(user);

            Log.e("IntroActivity", mLatitudeText + " " + mLongitudeText );
            //TODO: mandar estos datos al server (si no hay conexión no importa)
        }
        //TODO: el server va a tener que darse cuenta si no le setie nunca el long o latitud e interanmente no lo tenga en cuenta
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
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
