package com.skichrome.go4lunch.controllers.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;

import butterknife.OnClick;
import icepick.State;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback
{
    //=========================================
    // Fields
    //=========================================

    public static final int RC_LOCATION_CODE = 412;
    private static final String LOCATION_PERMISSION_REQUEST = Manifest.permission.ACCESS_FINE_LOCATION;

    private GoogleMap gMap;
    @State
    LatLng lastKnownPosition;
    private LocationManager locationManager;

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
    // Floating action btn Method
    //=========================================

    @SuppressLint("MissingPermission")
    @OnClick(R.id.fragment_map_floating_action_btn)
    @AfterPermissionGranted(RC_LOCATION_CODE)
    public void onClickFloatingActionBtn()
    {
        if (locationManager != null)
        {
            // Get last known Location
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);

            // Check with EasyPermissions if tha app have access to the location
            if (!EasyPermissions.hasPermissions(getContext(), LOCATION_PERMISSION_REQUEST))
                EasyPermissions.requestPermissions(this, getString(R.string.map_fragment_easy_permission_location_user_request), RC_LOCATION_CODE, LOCATION_PERMISSION_REQUEST);
            else
            {
                Log.i("EasyPermissions", "askUserToEnableLocationPermission: Location Access granted");

                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button

                this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null)
                {
                    this.lastKnownPosition = new LatLng(location.getLatitude(), location.getLongitude());

                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.lastKnownPosition, 15));
                }
                else
                    Toast.makeText(getContext(), "No location detected, have you enabled location in settings ? If yes, just wait less than a minute before re-try ;)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //=========================================
    // GMap Methods
    //=========================================

    @Override
    public void onMapReady(GoogleMap mGoogleMap)
    {
        this.gMap = mGoogleMap;
        this.gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.gMap.setIndoorEnabled(true);
        this.locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // First LatLng point configuration (this default point is Chamonix in Haute-Savoie)
        if (this.lastKnownPosition == null)
            this.lastKnownPosition = new LatLng(45.9167, 6.8667);

        gMap.addMarker(new MarkerOptions().position(this.lastKnownPosition).title("Last known position"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.lastKnownPosition, 10));
    }

    //=========================================
    // Location Method
    //=========================================

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}