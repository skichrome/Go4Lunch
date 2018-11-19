package com.skichrome.go4lunch.controllers.activities;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseActivity;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.FireStoreAuthentication;
import com.skichrome.go4lunch.utils.ItemClickSupportOnRecyclerView;
import com.skichrome.go4lunch.utils.firebase.PlaceTypeHelper;
import com.skichrome.go4lunch.views.WorkmatesAdapter;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

import static com.skichrome.go4lunch.utils.FireStoreAuthentication.ID_PLACE_INTEREST_CLOUD_FIRESTORE;

public class RestaurantDetailsActivity extends BaseActivity implements  FireStoreAuthentication.GetUserListener
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.activity_details_resto_name) TextView textViewName;
    @BindView(R.id.activity_details_resto_adress) TextView textViewAddress;
    @BindView(R.id.activity_details_resto_picture) ImageView imageViewPicture;
    @BindView(R.id.activity_details_resto_rate_1_star) ImageView imageViewRate1;
    @BindView(R.id.activity_details_resto_rate_2_stars) ImageView imageViewRate2;
    @BindView(R.id.activity_details_resto_rate_3_stars) ImageView imageViewRate3;
    @BindView(R.id.activity_details_resto_floating_btn) FloatingActionButton floatingActionButton;
    @BindView(R.id.activity_details_restaurant_container_call) ConstraintLayout constraintLayoutCall;
    @BindView(R.id.activity_details_restaurant_container_rate) ConstraintLayout constraintLayoutRate;
    @BindView(R.id.activity_details_restaurant_container_website) ConstraintLayout constraintLayoutWebsite;
    @BindView(R.id.activity_details_progress_bar) ProgressBar progressBar;
    @BindView(R.id.activity_details_resto_revycler_view_container) RecyclerView recyclerView;

    private WorkmatesAdapter adapter;
    private FormattedPlace restaurantDetails;
    Disposable disposable;

    public static final String ACTIVITY_DETAILS_CODE = "ACTIVITY_DETAILS_INTENT_CODE";
    public static final String FIREBASE_TOPIC = "restaurant_selected";

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected void configureActivity()
    {
        this.progressBar.setVisibility(View.VISIBLE);
        this.getDataFromBundle();
        this.updateUIElements();
        this.getPlaceDetails();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
    }

    @Override protected int getActivityLayout() { return R.layout.activity_details_restaurant; }
    @Override protected void updateActivityWithLocationUpdates(Location location) { }

    @Override
    public void onPause()
    {
        super.onPause();
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
    }

    //=========================================
    // Configuration Methods
    //=========================================

    private void getDataFromBundle()
    {
        try
        {
            this.restaurantDetails = (FormattedPlace) getIntent().getSerializableExtra(ACTIVITY_DETAILS_CODE);
        }
        catch (ClassCastException mE)
        {
            Log.e("RestoDetailsActivity", "getDataFromBundle: ClassCastException", mE);
        }
    }

    private void updateUIElements()
    {
        this.textViewName.setText(restaurantDetails.getName());
        this.textViewAddress.setText(restaurantDetails.getAddress());

        // Todo configure photo url with google api url and photo reference
        String photoUrl = null;

        if (photoUrl != null)
            Glide.with(this).load(photoUrl).into(imageViewPicture);

        // Todo calc the rating of place with rating included in Google api
        int size = 10;
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
    }

        private void configureRecyclerView ()
        {
            String[] text = {getString(R.string.view_holder_is_joining)};

            this.adapter = new WorkmatesAdapter(generateOptionsForAdapter(PlaceTypeHelper.getWorkMatesInPlace(ID_PLACE_INTEREST_CLOUD_FIRESTORE, restaurantDetails.getId())), Glide.with(this), restaurantDetails.getId(), text);
            this.recyclerView.setAdapter(adapter);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        private FirestoreRecyclerOptions<User> generateOptionsForAdapter (Query mQuery)
        {
            return new FirestoreRecyclerOptions.Builder<User>()
                    .setQuery(mQuery, User.class)
                    .setLifecycleOwner(this)
                    .build();
        }

        private void configureOnClickRecyclerView ()
        {
            ItemClickSupportOnRecyclerView.addTo(recyclerView, R.id.fragment_workmates_recycler_view).setOnItemClickListener((recyclerView, position, v) -> launchActivity(position));
        }

        private void launchActivity ( int position)
        {
            FireStoreAuthentication.getUserPlace(this, this, adapter.getItem(position));
        }

        //=========================================
        // OnClick Methods
        //=========================================

        @OnClick(R.id.activity_details_resto_floating_btn)
        public void onClickFloatingActionBtn ()
        {
            if (isCurrentUserLogged())
            {
                FireStoreAuthentication.deleteUserFromPlace(this, getCurrentUser(), restaurantDetails); // Todo check the name of firebase method
                this.floatingActionButton.setImageResource(R.drawable.baseline_check_circle_outline_white_24dp);
                FirebaseMessaging.getInstance().subscribeToTopic(FIREBASE_TOPIC);
            }
        }

        @OnClick({R.id.activity_details_restaurant_container_call, R.id.activity_details_restaurant_container_rate, R.id.activity_details_restaurant_container_website})
        public void onClickConstraintLayout (ConstraintLayout mConstraintLayout)
        {
            Intent intent;
            switch (Integer.valueOf(mConstraintLayout.getTag().toString()))
            {
                case 10:
                    if (restaurantDetails.getPhoneNumber() == null)
                        Toast.makeText(this, R.string.toast_details_activity_no_phone, Toast.LENGTH_SHORT).show();
                    else
                    {
                        intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + restaurantDetails.getPhoneNumber()));
                        if (intent.resolveActivity(getPackageManager()) != null)
                            startActivity(intent);
                    }
                    break;

                case 20:
                    this.updateRatingOfRestaurant();
                    break;

                case 30:
                    if (restaurantDetails.getWebsite() == null)
                        Toast.makeText(this, R.string.toast_details_activity_no_website, Toast.LENGTH_SHORT).show();
                    else
                    {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantDetails.getWebsite()));
                        if (intent.resolveActivity(getPackageManager()) != null)
                            startActivity(intent);
                    }
                    break;

                default:
                    break;
            }
        }

        private void updateRatingOfRestaurant ()
        {
            //todo change the way of rating a restaurant
            FireStoreAuthentication.updateRateRestaurant(this, getCurrentUser(), restaurantDetails, null);
        }

        //=========================================
        // AsyncTask Methods
        //=========================================

        private void getPlaceDetails ()
        {
        }

        @Override
        public void onSuccess (Intent mIntent)
        {
            startActivity(mIntent);
        }
    }