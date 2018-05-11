package com.skichrome.go4lunch.controllers.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    private static ArrayList<FormattedPlace> placesList;
    private static RVAdapter adapter;

    //=========================================
    // New Instance method
    //=========================================

    public static Fragment newInstance()
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
        this.configureRecyclerView();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureRecyclerView()
    {
        placesList = new ArrayList<>();

        adapter = new RVAdapter(placesList, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public static void updatePlacesHashMap(HashMap<String, FormattedPlace> mPlaceHashMap)
    {
        placesList.addAll(mPlaceHashMap.values());
        adapter.notifyDataSetChanged();
    }
}