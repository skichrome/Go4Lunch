package com.skichrome.go4lunch.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.FormattedPlace;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsViewHolder>
{
    //=========================================
    // Fields
    //=========================================

    private List<FormattedPlace> mPlaceList;
    private RequestManager mGlide;

    //=========================================
    // Constructor
    //=========================================

    public RestaurantsAdapter(List<FormattedPlace> placeList, RequestManager glide)
    {
        this.mPlaceList = placeList;
        this.mGlide = glide;
    }

    //=========================================
    // Superclass Methods
    //=========================================

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_list_list_item_recycler_view, parent, false);
        return new RestaurantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder holder, int position)
    {
        holder.updateUI(mPlaceList.get(position), mGlide);
    }

    @Override
    public int getItemCount() { return mPlaceList.size(); }

    public FormattedPlace getClickedPlace(int mPosition) { return this.mPlaceList.get(mPosition); }
}