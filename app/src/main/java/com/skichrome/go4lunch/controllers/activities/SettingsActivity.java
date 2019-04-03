package com.skichrome.go4lunch.controllers.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.SettingFragment;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.FireStoreAuthentication;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Enable user to modify some options, delete his account and see his account informations.
 */
public class SettingsActivity extends BaseActivity
{
    /**
     * App toolbar
     */
    @BindView(R.id.activity_setting_toolbar_include) Toolbar mToolbar;
    /**
     * Profile image of the user.
     */
    @BindView(R.id.activity_settings_profile_img) ImageView mImageViewProfile;
    /**
     * Name of user.
     */
    @BindView(R.id.activity_settings_name) TextView mTextViewName;
    /**
     * Email of the user
     */
    @BindView(R.id.activity_settings_email) TextView mTextViewEmail;
    /**
     * Enable account deletion
     */
    @BindView(R.id.activity_settings_delete_account_btn) Button mBtn;

    /**
     * @see BaseActivity#getActivityLayout()
     */
    @Override
    protected int getActivityLayout() { return R.layout.activity_settings; }

    /**
     * <h1>MainActivity initialisation</h1>
     * @see BaseActivity#configureActivity()
     */
    @Override
    protected void configureActivity()
    {
        this.configureToolbar();
        this.updateUserInfo();
    }

    /**
     * <h1>MainActivity initialisation</h1>
     * @see BaseActivity#updateActivity()
     */
    @Override protected void updateActivity()
    {
        SettingFragment mSettingFragment = SettingFragment.newInstance(getCurrentUser().getUid());
        getFragmentManager().beginTransaction().replace(R.id.activity_settings_fragment_container, mSettingFragment).commit();
    }

    /**
     * Update fields on screen with user information
     */
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

    /**
     * Toolbar initialisation.
     */
    private void configureToolbar()
    {
        mToolbar.setTitle(R.string.toolbar_title_settings);
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Show an AlertDialog when the user click on the button, to confirm it's choice.
     * If he choose yes, call {@link #deleteAccountFromFireBase()}
     */
    @OnClick(R.id.activity_settings_delete_account_btn)
    public void onClickDeleteAccountBtn()
    {
        new AlertDialog.Builder(this)
                .setMessage(R.string.alert_dialog_confirmation_msg)
                .setPositiveButton(R.string.alert_dialog_yes, (dialog, which) -> deleteAccountFromFireBase())
                .setNegativeButton(R.string.alert_dialog_no, null)
                .show();
    }

    /**
     * Ask to firebase to delete account.
     * @see FireStoreAuthentication#deleteAccount(Activity, FirebaseUser)
     */
    private void deleteAccountFromFireBase()
    {
        FireStoreAuthentication.deleteAccount(this, getCurrentUser());
    }
}