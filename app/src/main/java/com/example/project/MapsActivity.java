package com.example.project;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.project.entities.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new DatabaseHelper(this);
    }

    /**
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //random markers on map, in Romania
        List<User> users = db.getUsers();
        for (User u: users) {
            LatLng latLng = new LatLng(Math.random() * (47 - 44.5 + 1) + 44.5, Math.random() * (27 - 23 + 1) + 23);
            mMap.addMarker(new MarkerOptions().position(latLng).title(u.getFullName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

//        LatLng bucurestiPatricia = new LatLng(44.4306, 26.0519);
//        MarkerOptions marker_in_bucuresti = new MarkerOptions().position(bucurestiPatricia).title("Patricia");
//        marker_in_bucuresti.alpha(0.6f);
//        mMap.addMarker(marker_in_bucuresti);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(bucurestiPatricia));
//
//        LatLng tgMuresDiana = new LatLng(46.5386, 24.5514);
//        MarkerOptions marker_in_tgMures = new MarkerOptions().position(tgMuresDiana).title("Diana");
//        marker_in_tgMures.alpha(0.6f);
//        mMap.addMarker(marker_in_tgMures);

        //move camera to middle of Romania
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.0244, 24.8727), 7.0f));
    }
}