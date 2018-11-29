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

    @BindView(R.id.fragment_list_recycler_view_container) RecyclerView recyclerView;
    @BindView(R.id.fragment_list_progress_bar) ProgressBar progressBar;
    @BindView(R.id.list_fragment_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private List<FormattedPlace> placesList;
    private RestaurantsAdapter adapter;

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
        this.progressBar.setVisibility(View.VISIBLE);
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
        this.placesList = new ArrayList<>();
        this.adapter = new RestaurantsAdapter(placesList, Glide.with(this));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(adapter);
    }

    private void configureOnClickRV()
    {
        ItemClickSupportOnRecyclerView.addTo(this.recyclerView, R.layout.fragment_list_list_item_recycler_view)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
                    intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, this.adapter.getClickedPlace(position));
                    startActivity(intent);
                });
    }

    private void configureSwipeRefreshLayout() { this.swipeRefreshLayout.setOnRefreshListener(this::updatePlacesList); }

    //=========================================
    // Update Methods
    //=========================================

    public void updatePlacesList()
    {
        this.placesList.clear();
        PlaceHelper.getAllPlaces().addOnSuccessListener(success ->
        {
            for (DocumentSnapshot snap : success)
            {
                FormattedPlace place = snap.toObject(FormattedPlace.class);
                this.placesList.add(place);
            }
            this.adapter.notifyDataSetChanged();
            this.progressBar.setVisibility(View.INVISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);
        }).addOnFailureListener(throwable -> Log.e("ListFragment : ", "An error occurred when downloading all places.", throwable));
    }
}