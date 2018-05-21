package com.skichrome.go4lunch.controllers.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;
import com.skichrome.go4lunch.utils.ActivitiesCallbacks;
import com.skichrome.go4lunch.utils.GetPhotoOnGoogleApiAsyncTask;
import com.skichrome.go4lunch.utils.ItemClickSupportOnRecyclerView;
import com.skichrome.go4lunch.utils.MapMethods;
import com.skichrome.go4lunch.utils.rxjava.GoogleApiStream;
import com.skichrome.go4lunch.views.RestaurantsAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListFragment extends BaseFragment implements ActivitiesCallbacks.AsyncTaskListeners
{
    public interface OnFragmentReadyListener
    {
        void onListFragmentReady();
    }

    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_list_recycler_view_container) RecyclerView recyclerView;
    @BindView(R.id.fragment_list_progress_bar) ProgressBar progressBar;

    private List<FormattedPlace> placesList;
    private ArrayList<FormattedPlace> placesListDetails;
    private RestaurantsAdapter adapter;
    private WeakReference<ActivitiesCallbacks.ShowDetailsListener> callbackRVClick;

    private FusedLocationProviderClient mLocationClient;
    private Disposable disposable;

    //=========================================
    // New Instance method
    //=========================================

    public static ListFragment newInstance()
    {
        return new ListFragment();
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_list;
    }

    @Override
    protected void configureFragment()
    {
        this.progressBar.setVisibility(View.VISIBLE);

        this.mLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        this.configureRecyclerView();

        callbackRVClick = new WeakReference<>((ActivitiesCallbacks.ShowDetailsListener) getActivity());
        this.configureOnClickRV();

        WeakReference<OnFragmentReadyListener> callbackFragmentReady = new WeakReference<>((OnFragmentReadyListener) getActivity());
        callbackFragmentReady.get().onListFragmentReady();
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
                {
                    for (FormattedPlace place : placesList)
                    {
                        Location placeLocation = new Location("placeLocation");
                        placeLocation.setLatitude(place.getLocationLatitude());
                        placeLocation.setLongitude(place.getLocationLongitude());

                        int distance = (int) location.distanceTo(placeLocation);
                        place.setDistance(distance + "m");
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getContext(), R.string.toast_frag_no_location, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureRecyclerView()
    {
        this.placesList = new ArrayList<>();
        this.adapter = new RestaurantsAdapter(placesList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureOnClickRV()
    {
        ItemClickSupportOnRecyclerView.addTo(recyclerView, R.layout.fragment_list_list_item_recycler_view)
                .setOnItemClickListener(new ItemClickSupportOnRecyclerView.OnItemClickListener()
                {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v)
                    {
                        callbackRVClick.get().showRestaurantDetails(adapter.getClickedPlace(position));
                    }
                });
    }

    public void updatePlacesList(ArrayList<FormattedPlace> mPlaces)
    {
        this.placesList.addAll(mPlaces);
        adapter.notifyDataSetChanged();
        this.getLastKnownLocation();

        this.placesListDetails = new ArrayList<>();
        for (FormattedPlace place : placesList)
        {
            this.getPlaceDetails(place);
        }
    }

    //=========================================
    // AsyncTask Methods
    //=========================================

    private void getPlaceDetails(final FormattedPlace mPlace)
    {
        this.disposable = GoogleApiStream.getNearbyPlacesOnGoogleWebApi(getString(R.string.google_place_api_key), mPlace.getId()).subscribeWith(new DisposableObserver<MainGooglePlaceSearch>()
        {
            @Override
            public void onNext(MainGooglePlaceSearch mMainGooglePlaceSearch)
            {
                MapMethods.updatePlaceDetails(mMainGooglePlaceSearch, mPlace);
            }

            @Override
            public void onError(Throwable e)
            {
            }

            @Override
            public void onComplete()
            {
                executeAsyncTask(mPlace);
            }
        });
    }

    private void executeAsyncTask(FormattedPlace mPlace)
    {
        GetPhotoOnGoogleApiAsyncTask asyncTask = new GetPhotoOnGoogleApiAsyncTask(this, mPlace, getString(R.string.google_place_api_key));
        asyncTask.execute();
    }

    @Override
    public void onPreExecute()
    {
    }

    @Override
    public void doInBackground()
    {
    }

    @Override
    public void onPostExecute(FormattedPlace mPlace)
    {
        this.placesListDetails.add(mPlace);
        this.placesList = new ArrayList<>();
        this.placesList.addAll(placesListDetails);
        this.adapter.notifyDataSetChanged();
        this.progressBar.setVisibility(View.INVISIBLE);
    }
}