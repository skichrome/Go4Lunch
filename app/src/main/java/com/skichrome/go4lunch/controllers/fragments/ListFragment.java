package com.skichrome.go4lunch.controllers.fragments;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.ItemClickSupportOnRecyclerView;
import com.skichrome.go4lunch.utils.firebase.PlaceHelper;
import com.skichrome.go4lunch.views.RestaurantsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Contain a list of restaurants downloaded in MainActivity on Google Places API.
 */
public class ListFragment extends BaseFragment
{
    //=========================================
    // Fields
    //=========================================

    /**
     * RecyclerView for restaurants list.
     */
    @BindView(R.id.fragment_list_recycler_view_container) RecyclerView mRecyclerView;
    /**
     * ProgressBar, tells to user that the app is loading the restaurants list.
     */
    @BindView(R.id.fragment_list_progress_bar) ProgressBar mProgressBar;
    /**
     * Needed to trigger restaurants list update.
     */
    @BindView(R.id.list_fragment_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The list of restaurants to be displayer in the recyclerView
     */
    private List<FormattedPlace> mPlacesList;
    /**
     * Adapter for the recyclerView
     */
    private RestaurantsAdapter mAdapter;

    //=========================================
    // New Instance method
    //=========================================

    /**
     * NewInstance method.
     * @return
     *      New instance of this fragment.
     */
    public static ListFragment newInstance() { return new ListFragment(); }

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * @see BaseFragment#getFragmentLayout()
     */
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_list; }

    /**
     * @see BaseFragment#configureFragment()
     */
    @Override
    protected void configureFragment()
    {
        this.mProgressBar.setVisibility(View.VISIBLE);
        this.configureRecyclerView();
        this.configureOnClickRV();
        this.configureSwipeRefreshLayout();
        this.updatePlacesList();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    /**
     * Configure the recyclerView.
     */
    private void configureRecyclerView()
    {
        this.mPlacesList = new ArrayList<>();
        this.mAdapter = new RestaurantsAdapter(mPlacesList, Glide.with(this), getString(R.string.google_place_api_key));
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Configure recyclerView click callback> Launch {@link RestaurantDetailsActivity} when user click on a restaurant.
     */
    private void configureOnClickRV()
    {
        ItemClickSupportOnRecyclerView.addTo(this.mRecyclerView, R.layout.fragment_list_list_item_recycler_view)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
                    intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, this.mAdapter.getClickedPlace(position));
                    startActivity(intent);
                });
    }

    /**
     * Update restaurant list when user refresh the layout.
     */
    private void configureSwipeRefreshLayout() { this.mSwipeRefreshLayout.setOnRefreshListener(this::updatePlacesList); }

    //=========================================
    // Update Methods
    //=========================================

    /**
     * Update the list of restaurant with restaurants stored on Cloud Firestore Database.
     */
    public void updatePlacesList()
    {
        this.mPlacesList.clear();
        PlaceHelper.getAllPlaces().addOnSuccessListener(success ->
        {
            for (DocumentSnapshot snap : success)
            {
                FormattedPlace place = snap.toObject(FormattedPlace.class);
                this.mPlacesList.add(place);
            }
            this.mAdapter.notifyDataSetChanged();
            this.mProgressBar.setVisibility(View.INVISIBLE);
            this.mSwipeRefreshLayout.setRefreshing(false);
        }).addOnFailureListener(throwable -> Log.e("ListFragment : ", "An error occurred when downloading all places.", throwable));
    }

    /**
     * The list is updated with the result fetched on PlaceAutocomplete API.
     * @param formattedPlace
     *      The restaurant to put in the list.
     */
    public void updateListForAutocomplete(FormattedPlace formattedPlace)
    {
        this.mPlacesList.clear();
        this.mPlacesList.add(formattedPlace);
        this.mAdapter.notifyDataSetChanged();
    }
}