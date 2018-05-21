package com.skichrome.go4lunch.controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseActivity;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.googleplace.MainGooglePlaceSearch;
import com.skichrome.go4lunch.utils.ActivitiesCallbacks;
import com.skichrome.go4lunch.utils.GetPhotoOnGoogleApiAsyncTask;
import com.skichrome.go4lunch.utils.MapMethods;
import com.skichrome.go4lunch.utils.rxjava.GoogleApiStream;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailsActivity extends BaseActivity implements ActivitiesCallbacks.AsyncTaskListeners
{
    //=========================================
    // Fields
    //=========================================

    @BindView(R.id.activity_details_resto_name) TextView textViewName;
    @BindView(R.id.activity_details_resto_adress) TextView textViewAddress;
    @BindView(R.id.activity_details_resto_picture) ImageView imageViewPicture;
    @BindView(R.id.activity_details_resto_floating_btn) FloatingActionButton floatingActionButton;
    @BindView(R.id.activity_details_restaurant_container_call) ConstraintLayout constraintLayoutCall;
    @BindView(R.id.activity_details_restaurant_container_rate) ConstraintLayout constraintLayoutRate;
    @BindView(R.id.activity_details_restaurant_container_website) ConstraintLayout constraintLayoutWebsite;
    @BindView(R.id.activity_details_progress_bar) ProgressBar progressBar;

    private FormattedPlace restaurantDetails;

    Disposable disposable;
    GetPhotoOnGoogleApiAsyncTask asyncTask;

    public static final String ACTIVITY_DETAILS_CODE = "ACTIVITY_DETAILS_INTENT_CODE";

    //=========================================
    // Superclass Methods
    //=========================================

    @Override
    protected int getActivityLayout()
    {
        return R.layout.activity_details_restaurant;
    }

    @Override
    protected void configureActivity()
    {
        this.progressBar.setVisibility(View.VISIBLE);
        this.getDataFromBundle();
        this.updateUIElements();
        this.getPlaceDetails();
    }

    @Override
    protected void updateActivityWithPermissionGranted()
    {
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

        if (restaurantDetails.getPhoto() != null)
            Glide.with(this).load(restaurantDetails.getPhoto()).into(imageViewPicture);
    }

    //=========================================
    // OnClick Methods
    //=========================================

    @OnClick(R.id.activity_details_resto_floating_btn)
    public void onClickFloatingActionBtn()
    {
        this.floatingActionButton.setImageResource(R.drawable.baseline_check_circle_outline_white_24dp);
    }

    @OnClick({R.id.activity_details_restaurant_container_call, R.id.activity_details_restaurant_container_rate, R.id.activity_details_restaurant_container_website})
    public void onClickConstraintLayout(ConstraintLayout mConstraintLayout)
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
                Toast.makeText(this, "You are trying to rate the restaurant !", Toast.LENGTH_SHORT).show();
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

    //=========================================
    // AsyncTask Methods
    //=========================================

    private void getPlaceDetails()
    {
        this.disposable = GoogleApiStream.getNearbyPlacesOnGoogleWebApi(getString(R.string.google_place_api_key), restaurantDetails.getId()).subscribeWith(new DisposableObserver<MainGooglePlaceSearch>()
        {
            @Override
            public void onNext(MainGooglePlaceSearch mMainGooglePlaceSearch)
            {
                MapMethods.updatePlaceDetails(mMainGooglePlaceSearch, restaurantDetails);
            }

            @Override
            public void onError(Throwable e)
            {
            }

            @Override
            public void onComplete()
            {
                executeAsyncTask(restaurantDetails);
            }
        });
    }

    public void executeAsyncTask(FormattedPlace mPlace)
    {
        asyncTask = new GetPhotoOnGoogleApiAsyncTask(this, mPlace, getString(R.string.google_place_api_key));
        asyncTask.execute();
    }

    @Override
    public void onPreExecute()
    {
    }

    @Override
    public void doInBackground()
    {
    }

    @Override
    public void onPostExecute(FormattedPlace mPlace)
    {
        this.restaurantDetails = mPlace;
        this.updateUIElements();
        this.progressBar.setVisibility(View.INVISIBLE);
    }
}