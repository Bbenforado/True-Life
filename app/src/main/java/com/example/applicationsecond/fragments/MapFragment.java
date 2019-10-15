package com.example.applicationsecond.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.applicationsecond.R;
import com.example.applicationsecond.api.ProjectHelper;
import com.example.applicationsecond.api.UserHelper;
import com.example.applicationsecond.models.Project;
import com.example.applicationsecond.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.applicationsecond.utils.Utils.formatLocation;
import static com.example.applicationsecond.utils.Utils.getCurrentUser;
import static com.example.applicationsecond.utils.Utils.getUserLocation;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, LocationListener {


    private GoogleMap map;
    private LatLngBounds bounds;

    private static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        &&grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    showMyLocation();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                askPermissionsAndShowMyLocation();
            }
        });

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);

        try {
            map.setMyLocationEnabled(true);
        }
        catch (SecurityException e) {
            e.printStackTrace();
            return;
        }

        map.setOnMarkerClickListener(this::onMarkerClick);
    }

    private void askPermissionsAndShowMyLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                return;
            }
        }
        showMyLocation();
    }

    private void showMyLocation() {
        if (getUserLocation(getContext(), this, getActivity()) == null) {
            Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
        } else {
            double userLat = getUserLocation(getContext(), this, getActivity()).getLatitude();
            double userLng = getUserLocation(getContext(), this, getActivity()).getLongitude();

            LatLng latLng = new LatLng(userLat, userLng);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            float zoomLevel = 16.0f;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

            LatLng latLng1 = createNewLatLngForBounds(userLat, userLng, 1000);
            LatLng latLng2 = createNewLatLngForBounds(userLat, userLng, -1000);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(latLng1);
            builder.include(latLng2);
            bounds = builder.build();

            Marker marker;
            marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("You are here"));
            marker.setTag(-1);
            marker.showInfoWindow();

            getDataToDisplayFollowedProjectsOnTheMap();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getDataToDisplayFollowedProjectsOnTheMap() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);

                    if (user.getProjectsSubscribedId() != null) {
                        List<String> followedProjectsId = new ArrayList<>();
                        followedProjectsId.addAll(user.getProjectsSubscribedId());

                        for (int i = 0; i < followedProjectsId.size(); i++) {
                            ProjectHelper.getProject(followedProjectsId.get(i)).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Project project = task.getResult().toObject(Project.class);

                                        if (project.getLatLng() != null) {
                                            String[] latLong =  project.getLatLng().split(",");
                                            double latitude = Double.parseDouble(latLong[0]);
                                            double longitude = Double.parseDouble(latLong[1]);

                                            LatLng latLng = new LatLng(latitude, longitude);

                                            if (bounds.contains(latLng)) {
                                                showPlaceOnMap(true, project.getId(), latLng);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void showPlaceOnMap(boolean isProject, String id, LatLng latLng) {
        if (latLng != null) {
            CameraUpdateFactory.newLatLng(latLng);
            Marker placeMarker = map.addMarker(new MarkerOptions()
                    .position(latLng));
            placeMarker.setTag(id);
            if (isProject) {
                placeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            } else {
                placeMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            }
        } else {
            Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    private double getNewLat(double latitude, long meters) {
        double earth = 6378.137;  //radius of the earth in kilometer
        double pi = Math.PI;
        double m = (1 / ((2 * pi / 360) * earth)) / 1000;  //1 meter in degree

        return latitude + (meters * m);
    }

    private double getNewLng(double longitude, double latitude, long meters) {
        double earth = 6378.137;  //radius of the earth in kilometer
        double pi = Math.PI;
        double m = (1 / ((2 * pi / 360) * earth)) / 1000;  //1 meter in degree

        return longitude + (meters * m) / Math.cos(latitude * (pi / 180));
    }

    private LatLng createNewLatLngForBounds(double latitude, double longitude, long meters) {
        double newLat = getNewLat(latitude, meters);
        double newLng = getNewLng(longitude, latitude, meters);
        return new LatLng(newLat, newLng);
    }
}
