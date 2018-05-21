package com.skichrome.go4lunch.controllers.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.firebase.UserHelper;
import com.skichrome.go4lunch.views.WorkmatesAdapter;

import butterknife.BindView;

public class WorkmatesFragment extends BaseFragment
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_workmates_recycler_view) RecyclerView recyclerView;
    private WorkmatesAdapter adapterWorkmates;

    //=========================================
    // New Instance method
    //=========================================

    public static WorkmatesFragment newInstance()
    {
        return new WorkmatesFragment();
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_workmates;
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
        this.adapterWorkmates = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getAllUsers()), Glide.with(this));
        this.recyclerView.setAdapter(adapterWorkmates);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //this.adapterWorkmates.getItem();
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query mQuery)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(mQuery, User.class)
                .setLifecycleOwner(this)
                .build();
    }
}