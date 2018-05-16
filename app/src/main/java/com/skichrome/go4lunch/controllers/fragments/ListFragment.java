package com.skichrome.go4lunch.controllers.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.ActivitiesCallbacks;
import com.skichrome.go4lunch.utils.GetPhotoOnGoogleApiAsyncTask;
import com.skichrome.go4lunch.utils.MapMethods;
import com.skichrome.go4lunch.views.RVAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ListFragment extends BaseFragment implements ActivitiesCallbacks.AsynctaskListeners, ActivitiesCallbacks.RxJavaListeners
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_list_recycler_view_container) RecyclerView recyclerView;
    @BindView(R.id.fragment_list_progress_bar) ProgressBar progressBar;

    private List<FormattedPlace> placesList;
    private ArrayList<FormattedPlace> placesListDetails;
    private RVAdapter adapter;

    private WeakReference<ActivitiesCallbacks.ListFragmentCallback> callback;
    private MapMethods mapMethods = new MapMethods(this);

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
        this.callback = new WeakReference<>((ActivitiesCallbacks.ListFragmentCallback) getActivity());
        this.progressBar.setVisibility(View.VISIBLE);
        this.configureRecyclerView();
        callback.get().updatePlaceList();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureRecyclerView()
    {
        this.placesList = new ArrayList<>();
        this.adapter = new RVAdapter(placesList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void updatePlacesList(ArrayList<FormattedPlace> mPlaces)
    {
        placesList.addAll(mPlaces);
        adapter.notifyDataSetChanged();

        progressBar.setVisibility(View.INVISIBLE);/* TODO Remove this line after debugging */

        /*
        this.placesListDetails = new ArrayList<>();
        for (FormattedPlace place : placesList)
            mapMethods.getPlaceDetails(getString(R.string.google_place_api_key), place);
        */
    }

    //=========================================
    // AsyncTask Methods
    //=========================================

    @Override
    public void onComplete(FormattedPlace mPlace)
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
        //this.placesListDetails.add(mPlace);
        //this.placesList = placesListDetails;
        //this.adapter.notifyDataSetChanged();
        //this.progressBar.setVisibility(View.INVISIBLE);
    }
}