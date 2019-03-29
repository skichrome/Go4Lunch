package com.skichrome.go4lunch.controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.SettingFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.FireStoreAuthentication;
import com.skichrome.go4lunch.utils.ItemClickSupportOnRecyclerView;
import com.skichrome.go4lunch.utils.NotificationWorker;
import com.skichrome.go4lunch.utils.firebase.PlaceHelper;
import com.skichrome.go4lunch.utils.firebase.UserHelper;
import com.skichrome.go4lunch.views.WorkmatesAdapter;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.OnClick;

public class RestaurantDetailsActivity extends BaseActivity implements  FireStoreAuthentication.GetUserListener
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.activity_details_resto_name) TextView mTextViewName;
    @BindView(R.id.activity_details_resto_adress) TextView mTextViewAddress;
    @BindView(R.id.activity_details_resto_picture) ImageView mImageViewPicture;
    @BindView(R.id.activity_details_resto_rate_1_star) ImageView mImageViewRate1;
    @BindView(R.id.activity_details_resto_rate_2_stars) ImageView mImageViewRate2;
    @BindView(R.id.activity_details_resto_rate_3_stars) ImageView mImageViewRate3;
    @BindView(R.id.activity_details_resto_floating_btn) FloatingActionButton mFloatingActionButton;
    @BindView(R.id.activity_details_resto_revycler_view_container) RecyclerView mRecyclerView;

    private WorkmatesAdapter mAdapter;
    private FormattedPlace mRestaurantDetails;

    public static final String ACTIVITY_DETAILS_CODE = "ACTIVITY_DETAILS_INTENT_CODE";
//    public static final String FIREBASE_TOPIC = "restaurant_selected";
    private static final String GLIDE_BASE_GOOGLE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";

    //=========================================
    // Superclass Methods
    //=========================================

    @Override protected int getActivityLayout() { return R.layout.activity_details_restaurant; }
    @Override
    protected void configureActivity()
    {
        this.getDataFromBundle();
        this.updateUIElements();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
    }
    @Override protected void updateActivity() { }

    //=========================================
    // Configuration Methods
    //=========================================

    private void getDataFromBundle()
    {
        try
        {
            this.mRestaurantDetails = (FormattedPlace) getIntent().getSerializableExtra(ACTIVITY_DETAILS_CODE);
        }
        catch (ClassCastException mE)
        {
            Log.e("RestoDetailsActivity", "getDataFromBundle: ClassCastException", mE);
        }
    }

    private void updateUIElements()
    {
        this.mTextViewName.setText(mRestaurantDetails.getName());
        this.mTextViewAddress.setText(mRestaurantDetails.getAddress());

        if (mRestaurantDetails.getPhotoReference() != null)
        {
            String photoUrl = GLIDE_BASE_GOOGLE_URL + mRestaurantDetails.getPhotoReference() + "&key=" + getString(R.string.google_place_api_key);
            Glide.with(this).load(photoUrl).into(mImageViewPicture);
        }
        int rate = (int) Math.round(mRestaurantDetails.getRating()*3/5);
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
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(success ->
                {
                    User userLogged = success.toObject(User.class);
                    if (userLogged.getSelectedPlace() == null || !userLogged.getSelectedPlace().getId().equals(mRestaurantDetails.getId()))
                        mFloatingActionButton.setImageResource(R.drawable.baseline_add_white_24dp);
                    else
                        mFloatingActionButton.setImageResource(R.drawable.baseline_check_circle_outline_white_24dp);
                });
    }

    private void configureRecyclerView ()
    {
        String[] text = {getString(R.string.view_holder_is_joining)};

        this.mAdapter = new WorkmatesAdapter(generateOptionsForAdapter(UserHelper.getUsersInterestedByPlaceQuery(mRestaurantDetails.getId())),
                Glide.with(this),
                mRestaurantDetails.getId(),
                text);
        this.mRecyclerView.setAdapter(mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter (Query query)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void configureOnClickRecyclerView ()
    {
        ItemClickSupportOnRecyclerView.addTo(mRecyclerView, R.id.fragment_workmates_recycler_view).setOnItemClickListener((recyclerView, position, v) -> launchActivity(position));
    }

    private void launchActivity ( int position)
    {
        FireStoreAuthentication.getUserPlace(this, this, mAdapter.getItem(position));
    }

    //=========================================
    // OnClick Methods
    //=========================================

    @OnClick(R.id.activity_details_resto_floating_btn)
    public void onClickFloatingActionBtn ()
    {
        if (isCurrentUserLogged())
        {
            UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(success ->
            {
                User loggedUser = success.toObject(User.class);
                if (loggedUser.getSelectedPlace() == null || !loggedUser.getSelectedPlace().getId().equals(this.mRestaurantDetails.getId()))
                {
                    this.mFloatingActionButton.setImageResource(R.drawable.baseline_check_circle_outline_white_24dp);
                    FireStoreAuthentication.updateChosenRestaurant(this, getCurrentUser(), mRestaurantDetails);
//                    FirebaseMessaging.getInstance().subscribeToTopic(FIREBASE_TOPIC);
                }
                else
                {
                    this.mFloatingActionButton.setImageResource(R.drawable.baseline_add_white_24dp);
                    FireStoreAuthentication.updateChosenRestaurant(this, getCurrentUser(), null);
                }
                configureNotificationWorker(loggedUser.getSelectedPlace() == null);
            });
        }
    }

    @OnClick({R.id.activity_details_restaurant_container_call, R.id.activity_details_restaurant_container_rate, R.id.activity_details_restaurant_container_website})
    public void onClickConstraintLayout (ConstraintLayout constraintLayout)
    {
        Intent intent;
        switch (Integer.valueOf(constraintLayout.getTag().toString()))
        {
            case 10:
                if (mRestaurantDetails.getPhoneNumber() == null)
                    Toast.makeText(this, R.string.toast_details_activity_no_phone, Toast.LENGTH_SHORT).show();
                else
                {
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mRestaurantDetails.getPhoneNumber()));
                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivity(intent);
                }
                break;

            case 20:
                this.updateRatingOfRestaurant();
                break;

            case 30:
                if (mRestaurantDetails.getWebsite() == null)
                    Toast.makeText(this, R.string.toast_details_activity_no_website, Toast.LENGTH_SHORT).show();
                else
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRestaurantDetails.getWebsite()));
                    if (intent.resolveActivity(getPackageManager()) != null)
                        startActivity(intent);
                }
                break;

            default:
                break;
        }
    }

    // =======================================
    //                 Methods
    // =======================================

    private void configureNotificationWorker(Boolean isEnabled)
    {
            if (isEnabled)
            {
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setConstraints(constraints)
                        .setInitialDelay(SettingFragment.getDeltaCalendar(Calendar.getInstance(), SettingFragment.configureMidDayCalendar()), TimeUnit.MILLISECONDS)
                        .build();
                WorkManager.getInstance().enqueue(request);
            }
            else
            {
                WorkManager.getInstance().cancelAllWork();
            }

    }

    private void updateRatingOfRestaurant ()
    {
        PlaceHelper.updateRatingOfRestaurant(mRestaurantDetails.getId())
        .addOnCompleteListener(complete -> Log.d("RestaurantDetails", "Update rating of restaurant complete"));
    }

    //=========================================
    // Callback Methods
    //=========================================

    @Override
    public void onSuccess (Intent intent) { startActivity(intent); }
}