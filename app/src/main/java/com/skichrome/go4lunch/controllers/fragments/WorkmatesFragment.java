package com.skichrome.go4lunch.controllers.fragments;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

/**
 * Display a list of all workmates with an account on Firebase.
 */
public class WorkmatesFragment extends BaseFragment implements FireStoreAuthentication.GetUserListener
{
    //=========================================
    // Fields
    //=========================================

    /**
     * Workmates RecyclerView.
     */
    @BindView(R.id.fragment_workmates_recycler_view) RecyclerView mRecyclerView;
    /**
     * Adapter for the workmates recyclerView.
     */
    private WorkmatesAdapter mAdapterWorkmates;

    //=========================================
    // New Instance method
    //=========================================

    /**
     * NewInstance method.
     * @return
     *      New instance of this fragment.
     */
    public static WorkmatesFragment newInstance() { return new WorkmatesFragment(); }

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * @see BaseFragment#getFragmentLayout()
     */
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_workmates; }

    /**
     * @see BaseFragment#configureFragment()
     */
    @Override
    protected void configureFragment()
    {
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    /**
     * Configure the RecyclerView with an adapter.
     */
    private void configureRecyclerView()
    {
        String[] text = {getString(R.string.view_holder_is_eating), getString(R.string.view_holder_not_eating)};

        this.mAdapterWorkmates = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getAllUsers()), Glide.with(this), null, text);
        this.mRecyclerView.setAdapter(mAdapterWorkmates);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Configure the callback to get the item clicked by the user on the RecyclerView.
     */
    private void configureOnClickRecyclerView()
    {
        ItemClickSupportOnRecyclerView.addTo(mRecyclerView, R.id.fragment_workmates_recycler_view).setOnItemClickListener((recyclerView, position, v) -> launchActivity(position));
    }

    /**
     * Callback for RecyclerView Listener.
     * @param position
     *      The position in the adapter of the clicked item in recyclerView.
     */
    private void launchActivity(int position)
    {
        FireStoreAuthentication.getUserPlace(this, getActivity(), mAdapterWorkmates.getItem(position));
    }

    /**
     * Generate exploitable options for FirebaseAdapter.
     */
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    /**
     * <h1>Callback of #FireStoreAuthentication class</h1>
     * <p>
     *     Called when user click on a workmate in the recyclerView.
     * </p>
     * @param intent
     *      The intent to launch activity.
     */
    @Override
    public void onSuccess(Intent intent) { startActivity(intent); }
}