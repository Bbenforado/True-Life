package com.example.applicationsecond.utils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.AbstractInputMethodService;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.models.Project;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    //-----------------------------
    //FIREBASE
    //-------------------------
    /**
     * get the current logged user
     * */
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * return if the current user is logged
     * */
    public static Boolean isCurrentUserLogged() {
        return (getCurrentUser() != null);
    }

    //------------------------------------
    //METHODS
    //------------------------------------
    /**
     * add a zero behind the string if it s only one character.
     * */
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

    public static String getParenthesesContent(String str){
        return str.substring(str.indexOf('(')+1,str.indexOf(')'));
    }

    public static String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * return the current date format: dd/MM/yy
     */
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String currentDate = dateFormat.format(calendar.getTime());
        return currentDate;
    }

    /**
     * check if there is a value in edit text, and return the string in lower case if needed
     */
    public static String getEditTextValue(TextInputEditText editText, boolean toLowerCase) {
        if (!TextUtils.isEmpty(editText.getText())) {
            if (toLowerCase) {
                return editText.getText().toString().toLowerCase();
            } else {
                return editText.getText().toString();
            }
        }
        return null;
    }

    /**
     * check if the given string is null, if not set this string to the given edit text
     */
    public static void setValueToEditText(String value, TextInputEditText editText) {
        if (value != null) {
            editText.setText(value);
        }
    }

    public static void setDateOnButton(Button button, String date) {
        button.setText(date);
    }

    public static boolean checkIfStringIsNotEmpty(String value) {
        return !TextUtils.isEmpty(value);
    }

    /**
     * if the string length is >= 10, cut it
     */
    public static String resizeUsername(String username) {
        if (username.length() >= 10) {
            username = username.substring(0, 10) + ".";
        }
        return username;
    }

    public static String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }

    //-----------------------
    //LOCATION
    //------------------------
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
    /**
     * get the location of an address
     * */
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
    /**
     * return LatLng from a string
     * */
    public static LatLng getLatLngOfPlace(String latLng) {
        String[] retrievedLatLng = latLng.split(",");
        double latitude = Double.parseDouble(retrievedLatLng[0]);
        double longitude = Double.parseDouble(retrievedLatLng[1]);
        return new LatLng(latitude, longitude);
    }
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
}
