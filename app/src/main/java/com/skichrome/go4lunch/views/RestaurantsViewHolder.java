package com.skichrome.go4lunch.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ViewHolder for restaurant in the RecyclerView.
 */
class RestaurantsViewHolder extends RecyclerView.ViewHolder
{
    //=========================================
    // Fields
    //=========================================

    /**
     * The name of the restaurant.
     */
    @BindView(R.id.fragment_list_item_restaurant_name) TextView mTextViewName;
    /**
     * The distance to the restaurant.
     */
    @BindView(R.id.fragment_list_item_restaurant_distance) TextView mTextViewDistance;
    /**
     * The address of the restaurant.
     */
    @BindView(R.id.fragment_list_item_restaurant_address) TextView mTextViewAddress;
    /**
     * The number of workmates that goes into the restaurant.
     */
    @BindView(R.id.fragment_list_item_number_of_workmates_in_this_restaurant) TextView mTextViewNumberOfWorkMates;
    /**
     * The opening hours of the restaurant.
     */
    @BindView(R.id.fragment_list_item_restaurant_aperture) TextView mTextViewAperture;
    /**
     * The first level of restaurant rating.
     */
    @BindView(R.id.fragment_list_item_rate_1_star) ImageView mImageViewRate1;
    /**
     * The second level of restaurant rating.
     */
    @BindView(R.id.fragment_list_item_rate_2_stars) ImageView mImageViewRate2;
    /**
     * The third level of restaurant rating.
     */
    @BindView(R.id.fragment_list_item_rate_3_stars) ImageView mImageViewRate3;
    /**
     * The picture of the restaurant, downloaded from Google APIs.
     */
    @BindView(R.id.fragment_list_item_restaurant_image) ImageView mImageViewRestaurantImg;

    /**
     * Google API key for Glide.
     */
    private String MAP_API_KEY;
    private static final String GLIDE_BASE_GOOGLE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";

    //=========================================
    // Constructor
    //=========================================

    /**
     * Holder fields initialisation.
     * @param googleApiKey
     *      {@link #MAP_API_KEY}
     */
    RestaurantsViewHolder(View itemView, String googleApiKey)
    {
        super(itemView);
        this.MAP_API_KEY = googleApiKey;
        ButterKnife.bind(this, itemView);
    }

    //=========================================
    // UI Methods
    //=========================================

    /**
     * Update all views in the holder.
     * @param place
     *      The place that will update the holder.
     * @param glide
     *      For imageView updates.
     */
    void updateUI(FormattedPlace place, RequestManager glide)
    {
        this.mTextViewName.setText(place.getName());
        this.mTextViewDistance.setText(place.getDistance() + "m");
        this.mTextViewAddress.setText(place.getAddress());
        this.mTextViewAperture.setText(place.getAperture());

        UserHelper.getUsersInterestedByPlaceQuery(place.getId()).get().addOnSuccessListener(success ->
        {
            String textToDisplay = "(" + success.size() + ")";
            mTextViewNumberOfWorkMates.setText(textToDisplay);
        });

        int rate = (int) Math.round(place.getRating()*3/5);
        switch (rate)
        {
            case 0:
                mImageViewRate1.setVisibility(View.INVISIBLE);
                mImageViewRate2.setVisibility(View.INVISIBLE);
                mImageViewRate3.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mImageViewRate1.setVisibility(View.VISIBLE);
                mImageViewRate2.setVisibility(View.INVISIBLE);
                mImageViewRate3.setVisibility(View.INVISIBLE);
                break;

            case 2:
                mImageViewRate1.setVisibility(View.VISIBLE);
                mImageViewRate2.setVisibility(View.VISIBLE);
                mImageViewRate3.setVisibility(View.INVISIBLE);
                break;

            default:
                mImageViewRate1.setVisibility(View.VISIBLE);
                mImageViewRate2.setVisibility(View.VISIBLE);
                mImageViewRate3.setVisibility(View.VISIBLE);
                break;
        }

        if (place.getPhotoReference() != null)
        {
            String photoUrl = GLIDE_BASE_GOOGLE_URL + place.getPhotoReference() + "&key=" + MAP_API_KEY;
            glide.load(photoUrl).into(mImageViewRestaurantImg);
        }
    }
}