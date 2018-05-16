package com.skichrome.go4lunch.controllers.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    //=========================================
    // Fields
    //=========================================

    private GoogleMap gMap;
    private HashMap<String, FormattedPlace> placesHashMap;

    private FusedLocationProviderClient mLocationClient;
    //private Marker lastMarkerClicked;
    private WeakReference<ActivitiesCallbacks.MarkersChangedListener> markerCallback;

    //=========================================
    // New Instance method
    //=========================================

    public static MapFragment newInstance()
    {
        return new MapFragment();
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected void configureFragment()
    {
        this.markerCallback = new WeakReference<>((ActivitiesCallbacks.MarkersChangedListener) getActivity());
        this.mLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        this.configureMapApi();
    }

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_map;
    }

    //=========================================
    // Floating action btn Method
    //=========================================

    @OnClick(R.id.fragment_map_floating_action_btn)
    @AfterPermissionGranted(RequestCodes.RC_LOCATION_CODE)
    public void onClickFloatingActionBtn()
    {
        this.getLastKnownLocation();
    }

    //=========================================
    // Location Method
    //=========================================

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation()
    {
        mLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                // Got last known location. In some rare situations this can be null.
                if (location != null)
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                else
                    Toast.makeText(getContext(), "No location detected, have you enabled location in settings ?", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //=========================================
    // Methods
    //=========================================

    private void configureMapApi()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_map_view);
        mapFragment.getMapAsync(this);
    }

    //=========================================
    // Callbacks Methods
    //=========================================

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap mGoogleMap)
    {
        markerCallback.get().getMarkerOnMap();

        gMap = mGoogleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.setIndoorEnabled(true);
        gMap.setOnMarkerClickListener(this);

        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button

        this.getLastKnownLocation();
    }

    public void updateMarkerOnMap(HashMap<String, FormattedPlace> mPlaces)
    {
        if (mPlaces.size() != 0)
        {
            this.placesHashMap = mPlaces;
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
            Toast.makeText(getContext(), "No restaurants detected near you ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker mMarker)
    {
        FormattedPlace restaurantDetails = placesHashMap.get(mMarker.getTitle());
        markerCallback.get().displayRestaurantDetailsOnMarkerClick(restaurantDetails);
        return true;
    }
}