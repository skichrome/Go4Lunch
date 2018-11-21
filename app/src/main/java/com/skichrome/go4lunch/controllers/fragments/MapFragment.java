package com.skichrome.go4lunch.controllers.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.firebase.PlaceTypeHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

import static com.skichrome.go4lunch.utils.FireStoreAuthentication.ID_PLACE_INTEREST_CLOUD_FIRESTORE;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    //=========================================
    // Callback interface
    //=========================================

    public interface MapFragmentListeners
    {
        void fragmentNeedUpdateCallback();
    }

    //=========================================
    // Fields
    //=========================================

    private GoogleMap gMap;
    private WeakReference<MapFragmentListeners> callback;
    private Map<Marker, FormattedPlace> markers;

    //=========================================
    // New Instance method
    //=========================================

    public static MapFragment newInstance() { return new MapFragment(); }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected void configureFragment()
    {
        this.configureMapApi();
        this.callback = new WeakReference<> ((MapFragmentListeners) getActivity());
    }

    @Override
    protected int getFragmentLayout() { return R.layout.fragment_map; }

    //=========================================
    // Floating action btn Method
    //=========================================

    @OnClick(R.id.fragment_map_floating_action_btn)
    public void onClickFloatingActionBtn()
    {
        this.callback.get().fragmentNeedUpdateCallback();
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
    }

    public void updateMarkerOnMap(ArrayList<FormattedPlace> mPlaces)
    {
        this.markers = new HashMap<>();

        if (mPlaces != null && mPlaces.size() != 0)
        {
            gMap.clear();
            for (FormattedPlace place : mPlaces)
                PlaceTypeHelper.getNumberOfWorkmates(ID_PLACE_INTEREST_CLOUD_FIRESTORE, place.getId())
                        .addOnSuccessListener(mQueryDocumentSnapshots -> addMarkerToMap(mQueryDocumentSnapshots.size() != 0, place));
        } else
            Toast.makeText(getContext(), R.string.toast_frag_no_restaurant_detected, Toast.LENGTH_SHORT).show();
    }

    private void addMarkerToMap(boolean someoneIsJoining, FormattedPlace mPlace)
    {
        LatLng location = new LatLng(mPlace.getLocationLatitude(), mPlace.getLocationLongitude());
        Marker marker = gMap.addMarker(new MarkerOptions().position(location).title(mPlace.getName()).icon(BitmapDescriptorFactory.fromResource(someoneIsJoining ? R.drawable.lunch_marker_someone_here : R.drawable.lunch_marker_nobody)));
        this.markers.put(marker, mPlace);
    }

    @Override
    public boolean onMarkerClick(Marker mMarker)
    {
        FormattedPlace restaurantDetails = markers.get(mMarker);
        Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
        intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, restaurantDetails);
        startActivity(intent);
        return true;
    }
}