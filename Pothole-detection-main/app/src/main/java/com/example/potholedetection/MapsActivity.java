package com.example.potholedetection;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.potholedetection.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String lat1,lon1;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // retrieving current coordinates
        Bundle bundle = getIntent().getExtras();
        lat1 = bundle.getString("lat1");
        lon1 = bundle.getString("lon1");

        Log.d("Latitude", lat1);
        Log.d("Longitude", lon1);

        //assigning location coordinates
        LatLng curloc = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lon1));


        //using geocoder for semantic address
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(lat1), Double.parseDouble(lon1), 1);
            mMap.addMarker(new MarkerOptions()
                    .position(curloc)).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(curloc));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}