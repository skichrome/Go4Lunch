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

    private List<FormattedPlace> placeList;
    private RequestManager glide;

    //=========================================
    // Constructor
    //=========================================

    public RestaurantsAdapter(List<FormattedPlace> mPlaceList, RequestManager mGlide)
    {
        this.placeList = mPlaceList;
        this.glide = mGlide;
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
        holder.updateUI(placeList.get(position), glide);
    }

    @Override
    public int getItemCount()
    {
        return placeList.size();
    }

    public FormattedPlace getClickedPlace(int mPosition)
    {
        return this.placeList.get(mPosition);
    }
}