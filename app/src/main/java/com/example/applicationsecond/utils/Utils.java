package com.example.applicationsecond.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.models.Project;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;

public class Utils {

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static Boolean isCurrentUserLogged() {
        return (getCurrentUser() != null);
    }

    public static String addZeroToDate(String string) {
        if (string.length() == 1) {
            string = "0" + string;
        }
        return string;
    }

    /**
     * verify if there is a internet connection
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * get the latLng of an address and set it to the address
     * @param
     */

    public static String getLatLngOfProject(Context context, String streetNbr, String streetName, String city,
                                              String postalCode, String country) {
        String finalAddress = streetNbr + " " + streetName + "," +
                city + "," + postalCode + " " + country;

        return Utils.getLocationFromAddress(context, finalAddress);

    }

    public static String getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latLng;
        String latLngOfAddress = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            if (address.size() != 0) {
                Address location = address.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                latLngOfAddress = getParenthesesContent(latLng.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return latLngOfAddress;
    }
    //--------------------------------
    public static String getParenthesesContent(String str){
        return str.substring(str.indexOf('(')+1,str.indexOf(')'));
    }

    public static LatLng getLatLngOfPlace(String latLng) {
        String[] retrievedLatLng = latLng.split(",");
        double latitude = Double.parseDouble(retrievedLatLng[0]);
        double longitude = Double.parseDouble(retrievedLatLng[1]);
        return new LatLng(latitude, longitude);
    }

    //-----------------------
    //LOCATION
    //------------------------
    /**
     * get the current user location
     * @param context
     * @param listener
     * @param activity
     * @return the user Location
     */
    public static Location getUserLocation(Context context, LocationListener listener, Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        boolean enabled = locationManager.isProviderEnabled(bestProvider);
        if (!enabled) {
            Toast.makeText(context, "No location provider", Toast.LENGTH_SHORT).show();
        }

        final long MIN_TIME_BW_UPDATES = 1000;
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
        Location myLocation = null;
        try {
            locationManager.requestLocationUpdates(bestProvider, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);
            //myLocation = locationManager.getLastKnownLocation(bestProvider);
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        return myLocation;
    }

    /**
     * format a Location to a String format (latitude,longitude)
     * @param location location of the user
     * @return
     */
    public static String formatLocation(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

    public static String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
