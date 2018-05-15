package com.skichrome.go4lunch.controllers.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.views.RVAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class ListFragment extends BaseFragment
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_list_recycler_view_container) RecyclerView recyclerView;

    private ArrayList<FormattedPlace> placesList;
    private RVAdapter adapter;
    private static GoogleApiClient googleApiClient;

    //=========================================
    // New Instance method
    //=========================================

    public static ListFragment newInstance(GoogleApiClient mGoogleApiClient)
    {
        googleApiClient = mGoogleApiClient;
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
        this.configureRecyclerView();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureRecyclerView()
    {
        this.placesList = new ArrayList<>();

        this.adapter = new RVAdapter(this.placesList, Glide.with(this));
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void updatePlacesHashMap(HashMap<String, FormattedPlace> mPlaceHashMap)
    {
        this.placesList.addAll(mPlaceHashMap.values());
        this.adapter.notifyDataSetChanged();
    }
}