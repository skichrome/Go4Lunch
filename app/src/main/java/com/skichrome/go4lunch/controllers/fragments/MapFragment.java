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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.ActivitiesCallbacks;
import com.skichrome.go4lunch.utils.RequestCodes;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    //=========================================
    // Fields
    //=========================================

    static WeakReference<Context> contextWeakReference;
    private static GoogleMap gMap;
    private LatLng lastKnownLocation;
    private static HashMap<String, FormattedPlace> placesHashMap;
    private Marker lastMarkerClicked;
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

        if (RequestCodes.isLocationPermissionState())
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

    public static void updateMarkerOnMap(HashMap<String, FormattedPlace> mPlaces)
    {
        if (mPlaces.size() != 0)
        {
            placesHashMap = mPlaces;
            gMap.clear();

            for (Map.Entry<String, FormattedPlace> place : mPlaces.entrySet())
            {
                FormattedPlace placeValue = place.getValue();
                LatLng location = new LatLng(placeValue.getLocationLatitude(), placeValue.getLocationLongitude());
                gMap.addMarker(new MarkerOptions().position(location).title(placeValue.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.lunch_marker_nobody)));
            }
            Log.e("MARKER METHOD", "updateMarkerOnMap: size of markers list : " + mPlaces.size());
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
        if (!EasyPermissions.hasPermissions(contextWeakReference.get(), RequestCodes.LOCATION_PERMISSION_REQUEST))
        {
            EasyPermissions.requestPermissions((Activity) contextWeakReference.get(), contextWeakReference.get().getString(R.string.map_fragment_easy_permission_location_user_request), RequestCodes.RC_LOCATION_CODE, RequestCodes.LOCATION_PERMISSION_REQUEST);
        }
        else
        {
            RequestCodes.setLocationPermissionState();
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
    @AfterPermissionGranted(RequestCodes.RC_LOCATION_CODE)
    public void onClickFloatingActionBtn()
    {
        if (RequestCodes.isLocationPermissionState())
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
        gMap.setOnMarkerClickListener(this);

        askUserToGrandPermission();

        if (RequestCodes.isLocationPermissionState())
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

    @Override
    public boolean onMarkerClick(Marker mMarker)
    {
        // Allow user to see on which marked he has clicked before display restaurant details
        if (mMarker.equals(lastMarkerClicked))
        {
            FormattedPlace restaurantDetails = placesHashMap.get(mMarker.getTitle());
            markerCallback.displayRestaurantDetailsOnMarkerClick(restaurantDetails);
        }
        else
        {
            mMarker.showInfoWindow();
            lastMarkerClicked = mMarker;
        }
        return true;
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