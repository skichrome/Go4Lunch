package com.skichrome.go4lunch.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.utils.firebase.PlaceTypeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.skichrome.go4lunch.utils.FireStoreAuthentication.ID_PLACE_INTEREST_CLOUD_FIRESTORE;
import static com.skichrome.go4lunch.utils.FireStoreAuthentication.ID_PLACE_RATED_CLOUD_FIRESTORE;

class RestaurantsViewHolder extends RecyclerView.ViewHolder
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

    private static final int ID_WORKMATES = 100;
    private static final int ID_RATE = 200;

    //=========================================
    // Constructor
    //=========================================

    RestaurantsViewHolder(View itemView)
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
        this.textViewAperture.setText(mPlace.getAperture());
        this.textViewDistance.setText(mPlace.getDistance() == null ? "-" : mPlace.getDistance());

        PlaceTypeHelper.getNumberOfWorkmates(ID_PLACE_INTEREST_CLOUD_FIRESTORE, mPlace.getId()).addOnSuccessListener(onSuccessListener(ID_WORKMATES));
        PlaceTypeHelper.getNumberOfWorkmates(ID_PLACE_RATED_CLOUD_FIRESTORE, mPlace.getId()).addOnSuccessListener(onSuccessListener(ID_RATE));

        // Todo update photo field with Glide and Google Photo API here
    }

    private OnSuccessListener<QuerySnapshot> onSuccessListener(final int mOrigin)
    {
        return mQueryDocumentSnapshots ->
        {
            switch (mOrigin)
            {
                case ID_WORKMATES :
                    String textToDisplay = "(" + mQueryDocumentSnapshots.size() + ")";
                    textViewNumberOfWorkMates.setText(textToDisplay);
                    break;

                case ID_RATE :
                    int size = mQueryDocumentSnapshots.size(); // Todo update rating
                    switch (size)
                    {
                        case 0:
                            imageViewRate1.setVisibility(View.INVISIBLE);
                            imageViewRate2.setVisibility(View.INVISIBLE);
                            imageViewRate3.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            imageViewRate1.setVisibility(View.VISIBLE);
                            imageViewRate2.setVisibility(View.INVISIBLE);
                            imageViewRate3.setVisibility(View.INVISIBLE);
                            break;

                        case 2:
                            imageViewRate1.setVisibility(View.VISIBLE);
                            imageViewRate2.setVisibility(View.VISIBLE);
                            imageViewRate3.setVisibility(View.INVISIBLE);
                            break;

                        default:
                            imageViewRate1.setVisibility(View.VISIBLE);
                            imageViewRate2.setVisibility(View.VISIBLE);
                            imageViewRate3.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;

                default:
                    break;
            }
        };
    }
}