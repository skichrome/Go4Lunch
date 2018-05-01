package com.skichrome.go4lunch.controllers.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;

import static android.support.constraint.Constraints.TAG;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback
{
    //=========================================
    // Fields
    //=========================================

    private GoogleMap gMap;

    //=========================================
    // New Instance method
    //=========================================

    public static Fragment newInstance()
    {
        return new MapFragment();
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_map;
    }

    @Override
    protected void configureFragment()
    {
        this.configureMapApi();
    }

    @Override
    protected void updateFragment()
    {
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureMapApi()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_map_view);
        mapFragment.getMapAsync(this);
    }

    //=========================================
    // GMap Methods
    //=========================================

    @Override
    public void onMapReady(GoogleMap mGoogleMap)
    {
        Log.d(TAG, "onMapReady: _______________________________________");
        this.gMap = mGoogleMap;

        LatLng lyon = new LatLng(45.75, 4.85);
        gMap.addMarker(new MarkerOptions().position(lyon).title("Marker in lyon"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(lyon));
    }
}