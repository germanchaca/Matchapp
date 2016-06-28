package fiuba.matchapp.utils;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import fiuba.matchapp.app.MyApplication;
import fiuba.matchapp.model.User;

/**
 * Created by ger on 31/05/16.
 */
public class AdressUtils {
    public static String getParsedAddress(double latitude, double longitude) throws IOException {
        String parsedAddress = "";

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MyApplication.getInstance().getApplicationContext(), Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if(addresses.size() == 0) return "";
        String country = addresses.get(0).getCountryName();//FALLA por null
        String city = addresses.get(0).getLocality();
        parsedAddress = city + ", " + country;

        return parsedAddress;
    }
}
