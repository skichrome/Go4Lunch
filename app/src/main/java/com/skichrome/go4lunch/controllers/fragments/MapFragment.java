package com.skichrome.go4lunch.controllers.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;
import com.skichrome.go4lunch.controllers.ActivitiesCallbacks;
import com.skichrome.go4lunch.models.FormattedPlace;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.skichrome.go4lunch.controllers.activities.MainActivity.LOCATION_PERMISSION_REQUEST;
import static com.skichrome.go4lunch.controllers.activities.MainActivity.RC_LOCATION_CODE;
import static com.skichrome.go4lunch.controllers.activities.MainActivity.locationPermissionState;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback
{
    //=========================================
    // Fields
    //=========================================

    static WeakReference<Context> contextWeakReference;
    private static GoogleMap gMap;
    private LatLng lastKnownLocation;
    private ActivitiesCallbacks.MarkersChangedListener markerCallback;

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

    private static void getLastKnownLocation()
    {
        LocationManager locationManager = (LocationManager) contextWeakReference.get().getSystemService(Context.LOCATION_SERVICE);

        // Get last known Location
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        askUserToGrandPermission();

        if (locationPermissionState)
        {
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);

            if (location != null)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            else
                Toast.makeText(contextWeakReference.get(), "No location detected, have you enabled location in settings ? If yes, just wait less than a minute before re-try ;)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void updateFragment()
    {
    }

    private void configureMapApi()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_map_view);
        mapFragment.getMapAsync(this);
    }

    //=========================================
    // Floating action btn Method
    //=========================================

    public static void setContextWeakReference(Context mContext)
    {
        contextWeakReference = new WeakReference<>(mContext);
    }

    //=========================================
    // GMap Methods
    //=========================================

    public static void updateMarkerOnMap(List<FormattedPlace> mPlaces)
    {
        if (mPlaces != null)
        {
            gMap.clear();
            for (FormattedPlace place : mPlaces)
                gMap.addMarker(new MarkerOptions().position(place.getLocation()).title(place.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.lunch_marker_nobody)));
            Log.e("MARKER METHOD", "updateMarkerOnMap: size of markers list" + mPlaces.size());
        }
        else
            Toast.makeText(contextWeakReference.get(), "No restaurants detected near you ...", Toast.LENGTH_SHORT).show();
    }

    //=========================================
    // Methods
    //=========================================

    private static void askUserToGrandPermission()
    {
        // Check with EasyPermissions if tha app have access to the location
        if (!EasyPermissions.hasPermissions(contextWeakReference.get(), LOCATION_PERMISSION_REQUEST))
        {
            EasyPermissions.requestPermissions((Activity) contextWeakReference.get(), contextWeakReference.get().getString(R.string.map_fragment_easy_permission_location_user_request), RC_LOCATION_CODE, LOCATION_PERMISSION_REQUEST);
        }
        else
        {
            locationPermissionState = true;
            Log.i("EasyPerm in fragment", "askUserToEnableLocationPermission: Location Access granted");
        }
    }

    //=========================================
    // Callbacks Methods
    //=========================================

    @Override
    protected void configureFragment()
    {
        this.configureMapApi();
        this.createCallbackToParentActivity();
    }

    @OnClick(R.id.fragment_map_floating_action_btn)
    @AfterPermissionGranted(RC_LOCATION_CODE)
    public void onClickFloatingActionBtn()
    {
        if (locationPermissionState)
        {
            getLastKnownLocation();
            markerCallback.getMarkerOnMap();
        }
        else
            askUserToGrandPermission();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap mGoogleMap)
    {
        gMap = mGoogleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.setIndoorEnabled(true);

        askUserToGrandPermission();

        if (locationPermissionState)
        {
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button
        }

        // First LatLng point configuration (this default point is Chamonix in Haute-Savoie or Paris for second line)
        if (lastKnownLocation == null)
        {
            //this.lastKnownLocation = new LatLng(45.9167, 6.8667);
            this.lastKnownLocation = new LatLng(48.866667, 2.333333);
        }

        this.onClickFloatingActionBtn();
    }


    //=========================================
    // Permission Methods
    //=========================================

    private void createCallbackToParentActivity()
    {
        try
        {
            markerCallback = (ActivitiesCallbacks.MarkersChangedListener) getActivity();
        }
        catch (ClassCastException e)
        {
            Log.e("--- CALLBACK ---", "createCallbackToParentActivity: ", e);
        }
    }
}