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

/**
 * <h1>Show the details of a place</h1>
 * <p>
 *     This activity show the details of a place, collected from the intent in bundle.
 * </p>
 */
public class RestaurantDetailsActivity extends BaseActivity implements  FireStoreAuthentication.GetUserListener
{
    //=========================================
    // Fields
    //=========================================

    /**
     * The name of the restaurant
     */
    @BindView(R.id.activity_details_resto_name) TextView mTextViewName;
    /**
     * the adress of the restaurant
     */
    @BindView(R.id.activity_details_resto_adress) TextView mTextViewAddress;
    /**
     * The image of the restaurant
     */
    @BindView(R.id.activity_details_resto_picture) ImageView mImageViewPicture;
    /**
     * Rate level one of the restaurant
     */
    @BindView(R.id.activity_details_resto_rate_1_star) ImageView mImageViewRate1;
    /**
     * Rate level two of the restaurant
     */
    @BindView(R.id.activity_details_resto_rate_2_stars) ImageView mImageViewRate2;
    /**
     * Rate level three of the restaurant
     */
    @BindView(R.id.activity_details_resto_rate_3_stars) ImageView mImageViewRate3;
    /**
     * Button used to select or unselect this restaurant.
     */
    @BindView(R.id.activity_details_resto_floating_btn) FloatingActionButton mFloatingActionButton;
    /**
     * Container for the recycler view that will contains the workmates also interested.
     */
    @BindView(R.id.activity_details_resto_revycler_view_container) RecyclerView mRecyclerView;

    /**
     * Adapter for the recycler view.
     */
    private WorkmatesAdapter mAdapter;
    /**
     * The restaurant that we need to display details.
     */
    private FormattedPlace mRestaurantDetails;
    /**
     * Code used to identify the bundle in the intent.
     */
    public static final String ACTIVITY_DETAILS_CODE = "ACTIVITY_DETAILS_INTENT_CODE";
    /**
     * Base url used for Glide.
     */
    private static final String GLIDE_BASE_GOOGLE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
//    public static final String FIREBASE_TOPIC = "restaurant_selected";

    //=========================================
    // Superclass Methods
    //=========================================

    /**
     * @see BaseActivity#getActivityLayout()
     */
    @Override protected int getActivityLayout() { return R.layout.activity_details_restaurant; }

    /**
     * configure this activity
     * @see BaseActivity#configureActivity()
     */
    @Override
    protected void configureActivity()
    {
        this.getDataFromBundle();
        this.updateUIElements();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
    }

    /**
     * @see BaseActivity#updateActivity()
     */
    @Override protected void updateActivity() { }

    //=========================================
    // Configuration Methods
    //=========================================

    /**
     * <h1>Get the Formated place from Bundle</h1>
     * <p>
     *     This method try to get and cast the formatted place in the bundle.
     * </p>
     */
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

    /**
     * <h1>Update UI</h1>
     * <p>
     *     Update all the elements visible on screen, with available data.
     * </p>
     */
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

    /**
     * <h1>RecyclerView configuration</h1>
     * <p>
     *     Configure a recyclerView with a class that extends FirestoreRecyclerAdapter.
     * </p>
     */
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

    /**
     * generate options for firebase adapter
     * @param query
     *      query results from firebase.
     * @return
     *      options for adapter.
     */
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter (Query query)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    /**
     * Configure the callback when a user click on an item.
     */
    private void configureOnClickRecyclerView ()
    {
        ItemClickSupportOnRecyclerView.addTo(mRecyclerView, R.id.fragment_workmates_recycler_view).setOnItemClickListener((recyclerView, position, v) -> launchActivity(position));
    }

    /**
     * Launch an activity with the data collected from the recycler view
     * @param position
     *      the position of the item clicked by the user
     * @see FireStoreAuthentication#getUserPlace
     */
    private void launchActivity ( int position)
    {
        FireStoreAuthentication.getUserPlace(this, this, mAdapter.getItem(position));
    }

    //=========================================
    // OnClick Methods
    //=========================================

    /**
     * <h1>Floating action Button Listener</h1>
     * <p>
     *     Select the current detailed place to the selected place in Firebase if no place is selected,
     *     else it cancel the selected place from Firebase.
     *     Also update the logo in the button.
     * </p>
     */
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

    /**
     * <h1>Share / concact buttons Listeners</h1>
     * <p>
     *     <ul>
     *         <li>Phone : Launch an Intent to the phone app, to call the restaurant, if number is available.</li>
     *         <li>Rate : Update a field in Cloud Firestore database.</li>
     *         <li>Website : Launch an Intent to nthe web navigator, to sse the restaurant's website, if available.</li>
     *     </ul>
     * </p>
     * @param constraintLayout
     *      The view clicked by the user.
     */
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

    /**
     * <h1>WorkManager management</h1>
     * <p>
     *     This method enable or disable the WorkManager. <br/>
     *     If the WorkManager is enabled, a notification will be sended at 12h.
     * </p>
     * @param isEnabled
     *      Boolean, tell to the method if the WorkManager need to be enabled or disabled.
     */
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

    /**
     * <h1>Update rate field on Firebase</h1>
     * <p>
     *     Update the rate field in Cloud Firestore database. Update only the status of the restaurant detailed in this activity.
     * </p>
     */
    private void updateRatingOfRestaurant ()
    {
        PlaceHelper.updateRatingOfRestaurant(mRestaurantDetails.getId())
        .addOnCompleteListener(complete -> Log.d("RestaurantDetails", "Update rating of restaurant complete"));
    }

    //=========================================
    // Callback Methods
    //=========================================

    /**
     * <h1>Callback of #FireStoreAuthentication class</h1>
     * <p>
     *     Called when user click on a workmate in the recyclerView.
     * </p>
     * @param intent
     *      The intent to launch activity.
     */
    @Override
    public void onSuccess (Intent intent) { startActivity(intent); }
}