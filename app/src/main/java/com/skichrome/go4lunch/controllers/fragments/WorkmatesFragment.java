package com.skichrome.go4lunch.controllers.fragments;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseFragment;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.FireStoreAuthentication;
import com.skichrome.go4lunch.utils.ItemClickSupportOnRecyclerView;
import com.skichrome.go4lunch.utils.firebase.UserHelper;
import com.skichrome.go4lunch.views.WorkmatesAdapter;

import butterknife.BindView;

public class WorkmatesFragment extends BaseFragment implements FireStoreAuthentication.GetUserListener
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_workmates_recycler_view)
    RecyclerView recyclerView;
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
        this.configureOnClickRecyclerView();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void configureRecyclerView()
    {
        String[] text = {getString(R.string.view_holder_is_eating), getString(R.string.view_holder_not_eating)};

        this.adapterWorkmates = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getAllUsers()), Glide.with(this), null, text);
        this.recyclerView.setAdapter(adapterWorkmates);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureOnClickRecyclerView()
    {
        ItemClickSupportOnRecyclerView.addTo(recyclerView, R.id.fragment_workmates_recycler_view).setOnItemClickListener(new ItemClickSupportOnRecyclerView.OnItemClickListener()
        {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v)
            {
                launchActivity(position);
            }
        });
    }

    private void launchActivity(int position)
    {
        FireStoreAuthentication.getUserPlace(this, getActivity(), adapterWorkmates.getItem(position));
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query mQuery)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(mQuery, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onSuccess(Intent mIntent)
    {
        startActivity(mIntent);
    }
}