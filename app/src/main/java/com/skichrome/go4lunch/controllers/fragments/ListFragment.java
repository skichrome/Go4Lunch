package com.skichrome.go4lunch.controllers.fragments;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.ItemClickSupportOnRecyclerView;
import com.skichrome.go4lunch.views.RestaurantsAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ListFragment extends BaseFragment
{
    public interface OnFragmentReadyListener { void onListFragmentReady();}

    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_list_recycler_view_container) RecyclerView recyclerView;
    @BindView(R.id.fragment_list_progress_bar) ProgressBar progressBar;

    private List<FormattedPlace> placesList;
    private RestaurantsAdapter adapter;


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

        this.configureRecyclerView();
        this.configureOnClickRV();

        WeakReference<OnFragmentReadyListener> callbackFragmentReady = new WeakReference<>((OnFragmentReadyListener) getActivity());
        callbackFragmentReady.get().onListFragmentReady();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureRecyclerView()
    {
        placesList = new ArrayList<>();
        adapter = new RestaurantsAdapter(placesList, Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void configureOnClickRV()
    {
        ItemClickSupportOnRecyclerView.addTo(recyclerView, R.layout.fragment_list_list_item_recycler_view)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Intent intent = new Intent(getContext(), RestaurantDetailsActivity.class);
                    intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, adapter.getClickedPlace(position));
                    startActivity(intent);
                });
    }

    public void updatePlacesList(ArrayList<FormattedPlace> mPlaces)
    {
        placesList.clear();
        placesList.addAll(mPlaces);
    }
}