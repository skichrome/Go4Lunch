package com.skichrome.go4lunch.controllers.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.firebase.PlaceHelper;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

/**
 * This Fragment is used to display a mapView with map api, it will display some restaurants around the user mainly
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    //=========================================
    // Callback interface
    //=========================================

    /**
     * Callback interface, when user request qn update.
     */
    public interface MapFragmentListeners { void fragmentNeedUpdateCallback(); }

    //=========================================
    // Fields
    //=========================================

    /**
     * Default LatLng for the app.
     */
    private static final LatLng LATLNG_PARIS = new LatLng(48.841810, 2.325310);

    /**
     * Google Map instance.
     */
    private GoogleMap mGMap;
    /**
     * Callback to update the restaurant list when user refresh layout.
     */
    private WeakReference<MapFragmentListeners> mCallback;
    /**
     * List of places, identified by a marker key.
     */
    private Map<Marker, FormattedPlace> mMarkers;
    /**
     * True if the update origin id from place autocomplete, false if origin is from http request update.
     */
    private boolean mOrigin = false;

    //=========================================
    // New Instance method
    //=========================================

    /**
     * NewInstance method.
     * @return
     *      New instance of this fragment.
     */
    public static MapFragment newInstance() { return new MapFragment(); }

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * @see BaseFragment#getFragmentLayout()
     */
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_map; }

    /**
     * @see BaseFragment#configureFragment()
     */
    @Override
    protected void configureFragment()
    {
        this.configureMapApi();
        this.mCallback = new WeakReference<> ((MapFragmentListeners) getActivity());
    }

    //=========================================
    // Floating action btn Method
    //=========================================

    /**
     * Button to update location and restaurant list. Ask MainActivity to update all data by calling callback.
     */
    @OnClick(R.id.fragment_map_floating_action_btn)
    public void onClickFloatingActionBtn() { this.mCallback.get().fragmentNeedUpdateCallback(); }

    //=========================================
    // Methods
    //=========================================

    /**
     * Configure the Map.
     */
    private void configureMapApi()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_map_view);
        mapFragment.getMapAsync(this);
    }

    //=========================================
    // Callbacks Methods
    //=========================================

    /**
     * Google Map Android Callback, called when the map is ready to use.
     * @param googleMap
     *      Instance of Map.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.mGMap = googleMap;
        this.mGMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.mGMap.setIndoorEnabled(true);
        this.mGMap.setOnMarkerClickListener(this);
        this.mGMap.setMyLocationEnabled(true);
        this.mGMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button
        this.mGMap.animateCamera(CameraUpdateFactory.newLatLng(LATLNG_PARIS));
        this.updateMarkerOnMap();
    }

    /**
     * Update the location on the map, don't do anything if the origin is from place autocomplete
     * @param location
     *      The location used to update map view position.
     */
    public void updateLocation(Location location)
    {
        if (this.mGMap == null || this.mOrigin) return;
        LatLng lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lastKnownLatLng)
                .zoom(19)
                .tilt(30)
                .build();
        this.mGMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Update restaurant on the map with the result of Place Autocomplete.
     * @param place
     *      The fetched place.
     */
    public void updateMapForAutocomplete(FormattedPlace place)
    {
        if (mGMap == null) return;
        mGMap.clear();
        this.mMarkers = new HashMap<>();
        this.addMarkerToMap(false, place);
        LatLng placeLatLng = new LatLng(place.getLocationLatitude(), place.getLocationLongitude());
        this.mGMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 19.0f));
        this.mOrigin = true;
    }

    /**
     * Get all places stored in Cloud Firestore Database.
     */
    public void updateMarkerOnMap()
    {
        if (mGMap == null) return;
        mGMap.clear();
        this.mMarkers = new HashMap<>();
        PlaceHelper.getAllPlaces().addOnSuccessListener(success ->
        {
            for (DocumentSnapshot snap : success)
            {
                FormattedPlace place = snap.toObject(FormattedPlace.class);
                UserHelper.getUsersInterestedByPlace(place.getId())
                        .addOnSuccessListener(queryDocumentSnapshots ->
                                addMarkerToMap(!queryDocumentSnapshots.getDocuments().isEmpty(), place))
                .addOnFailureListener(throwable -> Log.e("MapFragment", "updateMarkerOnMap: ", throwable));
            }
        }).addOnFailureListener(throwable -> Log.e("MapFragment : ", "An error occurred when downloading all places.", throwable));
        this.mOrigin = false;
    }

    /**
     * Add a marker to the map and to the restaurant list. Change the color of the marker if on workmate
     * go into the restaurant.
     * @param someoneIsJoining
     *      True if there is almost one person that go into this restaurant, else false.
     * @param place
     *      The Place that will be displayed and stored in the list.
     */
    private void addMarkerToMap(boolean someoneIsJoining, FormattedPlace place)
    {
        LatLng location = new LatLng(place.getLocationLatitude(), place.getLocationLongitude());
        Marker marker = this.mGMap.addMarker(new MarkerOptions()
                .position(location)
                .title(place.getName())
                .icon(BitmapDescriptorFactory.fromResource(someoneIsJoining ? R.drawable.lunch_marker_someone_here : R.drawable.lunch_marker_nobody)));
        this.mMarkers.put(marker, place);
    }

    /**
     * Google Map Android API callback, when user click on a marker, {@link RestaurantDetailsActivity} is launched
     * with corresponding restaurant in bundle.
     * @param marker
     *      The marker clicked by the user.
     */
    @Override
    public boolean onMarkerClick(Marker marker)
    {
        FormattedPlace restaurantDetails = this.mMarkers.get(marker);
        Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
        intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, restaurantDetails);
        startActivity(intent);
        return true;
    }
}