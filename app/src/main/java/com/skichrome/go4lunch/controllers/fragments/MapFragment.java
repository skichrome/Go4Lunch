package com.skichrome.go4lunch.controllers.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
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
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    public interface MapFragmentListeners
    {
        void getResultOnClickFloatingActionBtn();
    }

    //=========================================
    // Fields
    //=========================================

    private GoogleMap gMap;
    private HashMap<String, FormattedPlace> placesHashMap;

    private FusedLocationProviderClient mLocationClient;
    private WeakReference<MapFragmentListeners> markerCallback;

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
        this.markerCallback = new WeakReference<>((MapFragmentListeners) getActivity());
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
                if (location != null)
                {
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    markerCallback.get().getResultOnClickFloatingActionBtn();
                }
                else
                    Toast.makeText(getContext(), R.string.toast_frag_no_location, Toast.LENGTH_SHORT).show();
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
        this.placesHashMap = new HashMap<>();
        this.placesHashMap.putAll(mPlaces);

        if (placesHashMap != null && placesHashMap.size() != 0)
        {
            gMap.clear();

            for (Map.Entry<String, FormattedPlace> place : placesHashMap.entrySet())
            {
                FormattedPlace placeValue = place.getValue();
                LatLng location = new LatLng(placeValue.getLocationLatitude(), placeValue.getLocationLongitude());
                gMap.addMarker(new MarkerOptions().position(location).title(placeValue.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.lunch_marker_nobody)));
            }
        }
        else
            Toast.makeText(getContext(), R.string.toast_frag_no_restaurant_detected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker mMarker)
    {
        FormattedPlace restaurantDetails = placesHashMap.get(mMarker.getTitle());

        Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
        intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, restaurantDetails);
        startActivity(intent);

        return true;
    }
}