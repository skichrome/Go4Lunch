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

public class ListFragment extends BaseFragment
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_list_recycler_view_container) RecyclerView mRecyclerView;
    @BindView(R.id.fragment_list_progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.list_fragment_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    private List<FormattedPlace> mPlacesList;
    private RestaurantsAdapter mAdapter;

    //=========================================
    // New Instance method
    //=========================================

    public static ListFragment newInstance() { return new ListFragment(); }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getFragmentLayout() { return R.layout.fragment_list; }

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

    private void configureRecyclerView()
    {
        this.mPlacesList = new ArrayList<>();
        this.mAdapter = new RestaurantsAdapter(mPlacesList, Glide.with(this), getString(R.string.google_place_api_key));
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mAdapter);
    }

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

    private void configureSwipeRefreshLayout() { this.mSwipeRefreshLayout.setOnRefreshListener(this::updatePlacesList); }

    //=========================================
    // Update Methods
    //=========================================

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

    public void updateListForAutocomplete(FormattedPlace formattedPlace)
    {
        this.mPlacesList.clear();
        this.mPlacesList.add(formattedPlace);
        this.mAdapter.notifyDataSetChanged();
    }
}