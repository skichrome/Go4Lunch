package com.skichrome.go4lunch.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.FormattedPlace;

import butterknife.BindView;
import butterknife.ButterKnife;

class RVViewHolder extends RecyclerView.ViewHolder
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.fragment_list_item_restaurant_name) TextView textViewName;
    @BindView(R.id.fragment_list_item_restaurant_distance) TextView textViewDistance;
    @BindView(R.id.fragment_list_item_restaurant_address) TextView textViewAddress;
    @BindView(R.id.fragment_list_item_number_of_workmates_in_this_restaurant) TextView textViewNumberOfWorkMates;
    @BindView(R.id.fragment_list_item_restaurant_aperture) TextView textViewAperture;
    @BindView(R.id.fragment_list_item_rate_1_star) ImageView imageViewRate1;
    @BindView(R.id.fragment_list_item_rate_2_stars) ImageView imageViewRate2;
    @BindView(R.id.fragment_list_item_rate_3_stars) ImageView imageViewRate3;
    @BindView(R.id.fragment_list_item_restaurant_image) ImageView imageViewRestaurantImg;

    //=========================================
    // Constructor
    //=========================================

    RVViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    //=========================================
    // UI Methods
    //=========================================

    void updateUI(FormattedPlace mPlace, RequestManager mGlide)
    {
        this.textViewName.setText(mPlace.getName());
        this.textViewDistance.setText(mPlace.getDistance());
        this.textViewAddress.setText(mPlace.getAddress());
        String number = "(" + mPlace.getNumberOfWorkmates() + ")";
        this.textViewNumberOfWorkMates.setText(number);
        this.textViewAperture.setText(mPlace.getAperture());
        this.textViewDistance.setText(mPlace.getDistance() == null ? "-" : mPlace.getDistance());

        if (mPlace.getPhoto() != null)
        {
            imageViewRestaurantImg.setBackgroundResource(0);
            mGlide.load(mPlace.getPhoto()).apply(RequestOptions.centerCropTransform()).into(imageViewRestaurantImg);
        }
        else
            imageViewRestaurantImg.setBackgroundResource(R.drawable.restaurant);
    }
}