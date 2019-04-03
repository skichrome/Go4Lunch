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

/**
 * Adapter for the Restaurant RecyclerView.
 */
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsViewHolder>
{
    //=========================================
    // Fields
    //=========================================

    /**
     * The list of restaurant to display.
     */
    private List<FormattedPlace> mPlaceList;
    /**
     * For ImageView updates.
     */
    private RequestManager mGlide;
    /**
     * Google API key for Glide.
     */
    private String MAP_API_KEY;

    //=========================================
    // Constructor
    //=========================================

    /**
     * Adapter constructor, initialise fields in this class.
     * @param placeList
     *      {@link #mPlaceList}
     * @param glide
     *      {@link #mGlide}
     * @param googleApiKey
     *      {@link #MAP_API_KEY}
     */
    public RestaurantsAdapter(List<FormattedPlace> placeList, RequestManager glide, String googleApiKey)
    {
        this.mPlaceList = placeList;
        this.mGlide = glide;
        this.MAP_API_KEY = googleApiKey;
    }

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * View holder creation, fields initialisation.
     */
    @NonNull @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_list_list_item_recycler_view, parent, false);
        return new RestaurantsViewHolder(view, MAP_API_KEY);
    }

    /**
     * View updates in this method.
     */
    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder holder, int position)
    {
        holder.updateUI(mPlaceList.get(position), mGlide);
    }

    /**
     * Number of items to display.
     * @return
     *      The size of the restaurant list : {@link #mPlaceList}
     */
    @Override
    public int getItemCount() { return mPlaceList.size(); }

    /**
     * For callback when user click on a item in the recyclerView.
     * @param mPosition
     *      The position of the restaurant in the list.
     * @return
     *      The restaurant associated to the position.
     */
    public FormattedPlace getClickedPlace(int mPosition) { return this.mPlaceList.get(mPosition); }
}