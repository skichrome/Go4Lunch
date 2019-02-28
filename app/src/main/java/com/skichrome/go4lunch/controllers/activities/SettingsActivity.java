package com.skichrome.go4lunch.controllers.activities;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.SettingFragment;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.FireStoreAuthentication;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity
{
    @BindView(R.id.activity_setting_toolbar_include) Toolbar mToolbar;
    @BindView(R.id.activity_settings_profile_img) ImageView mImageViewProfile;
    @BindView(R.id.activity_settings_name) TextView mTextViewName;
    @BindView(R.id.activity_settings_email) TextView mTextViewEmail;
    @BindView(R.id.activity_settings_delete_account_btn) Button mBtn;

    @BindView(R.id.btn_notif) Button notifDebugBtn; // Todo debug

    @Override
    protected int getActivityLayout() { return R.layout.activity_settings; }

    @Override
    protected void configureActivity()
    {
        this.configureToolbar();
        this.updateUserInfo();
    }

    @Override protected void updateActivity()
    {
        SettingFragment mSettingFragment = SettingFragment.newInstance(getCurrentUser().getUid());
        getFragmentManager().beginTransaction().replace(R.id.activity_settings_fragment_container, mSettingFragment).commit();
    }

    private void updateUserInfo()
    {
        if (isCurrentUserLogged())
        {
            FirebaseUser user = getCurrentUser();
            UserHelper.getUser(user.getUid()).addOnSuccessListener(success ->
            {
                User databaseUser = success.toObject(User.class);
                mTextViewName.setText(databaseUser.getUsername());
            });
            mTextViewEmail.setText(user.getEmail());
            if (user.getPhotoUrl() != null)
                Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(mImageViewProfile);
        }
    }

    private void configureToolbar()
    {
        mToolbar.setTitle(R.string.toolbar_title_settings);
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.activity_settings_delete_account_btn)
    public void onClickDeleteAccountBtn()
    {
        new AlertDialog.Builder(this)
                .setMessage(R.string.alert_dialog_confirmation_msg)
                .setPositiveButton(R.string.alert_dialog_yes, (dialog, which) -> deleteAccountFromFireBase())
                .setNegativeButton(R.string.alert_dialog_no, null)
                .show();
    }

    private void deleteAccountFromFireBase()
    {
        FireStoreAuthentication.deleteAccount(this, getCurrentUser());
    }

    @OnClick(R.id.btn_notif)    // Todo for debug only
    void onClickNotifDebug()
    {
        Log.e("SettingActivity", "onClickNotifDebug: SUCCESS");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getBoolean(SettingFragment.SW_NOTIFICATION_KEY_PREF, false))
            return;
        getUserRestaurant();
    }

    private void getUserRestaurant() // Todo debug only
    {
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(success ->
        {
            User user = success.toObject(User.class);
            FormattedPlace selectedPlace = user.getSelectedPlace();

            UserHelper.getUsersInterestedByPlace(selectedPlace.getId()).addOnSuccessListener(successList ->
            {
                List<User> userList = successList.toObjects(User.class);

                Log.e("NOTIFICATION", "getUserRestaurant: " + (userList.isEmpty() ?  "Empty List !!" : userList.size()));

                sendVisualNotification("IL est l'ore monseignore", selectedPlace, userList);
            });
        });
    }

    private void sendVisualNotification(String messageBody, FormattedPlace place, List<User> userList) // Todo for debug only
    {
        final int NOTIFICATION_ID = 78;
        final String NOTIFICATION_TAG = "Go4Lunch";

        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
        intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, place);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(messageBody);
        inboxStyle.addLine(place.getName());
        inboxStyle.addLine(place.getAddress());

        for (int i = 0; i < userList.size(); i++)
        {
            inboxStyle.addLine(userList.get(i).getUsername());
            if (i >= 4)
                break;
        }

        String channelId = "Channel Id";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("swiper pour plus d'infos")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                CharSequence channelName = getString(R.string.notification_channel_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                manager.createNotificationChannel(channel);
            }
            manager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
        }
    }
}