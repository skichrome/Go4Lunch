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

    @BindView(R.id.fragment_list_item_restaurant_name) TextView mTextViewName;
    @BindView(R.id.fragment_list_item_restaurant_distance) TextView mTextViewDistance;
    @BindView(R.id.fragment_list_item_restaurant_address) TextView mTextViewAddress;
    @BindView(R.id.fragment_list_item_number_of_workmates_in_this_restaurant) TextView mTextViewNumberOfWorkMates;
    @BindView(R.id.fragment_list_item_restaurant_aperture) TextView mTextViewAperture;
    @BindView(R.id.fragment_list_item_rate_1_star) ImageView mImageViewRate1;
    @BindView(R.id.fragment_list_item_rate_2_stars) ImageView mImageViewRate2;
    @BindView(R.id.fragment_list_item_rate_3_stars) ImageView mImageViewRate3;
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

    void updateUI(FormattedPlace place, RequestManager glide)
    {
        this.mTextViewName.setText(place.getName());
        this.mTextViewDistance.setText(place.getDistance());
        this.mTextViewAddress.setText(place.getAddress());
        this.mTextViewAperture.setText(place.getAperture());
        this.mTextViewDistance.setText(place.getDistance() == null ? "-" : place.getDistance());

        PlaceTypeHelper.getNumberOfWorkmates(ID_PLACE_INTEREST_CLOUD_FIRESTORE, place.getId()).addOnSuccessListener(onSuccessListener(ID_WORKMATES));
        PlaceTypeHelper.getNumberOfWorkmates(ID_PLACE_RATED_CLOUD_FIRESTORE, place.getId()).addOnSuccessListener(onSuccessListener(ID_RATE));

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
                    mTextViewNumberOfWorkMates.setText(textToDisplay);
                    break;

                case ID_RATE :
                    int size = mQueryDocumentSnapshots.size(); // Todo update rating
                    switch (size)
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
                    break;

                default:
                    break;
            }
        };
    }
}