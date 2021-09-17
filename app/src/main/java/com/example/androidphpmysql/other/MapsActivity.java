package com.example.androidphpmysql.other;

import static com.example.androidphpmysql.R.string.address_not_found;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.androidphpmysql.R;
import com.example.androidphpmysql.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.androidphpmysql.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        String location = com.example.androidphpmysql.statics.Address.getAddress();
        com.example.androidphpmysql.statics.Address.setAddress(null);
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), address_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
        Address address = Objects.requireNonNull(addressList).get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng).title(location));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }
}