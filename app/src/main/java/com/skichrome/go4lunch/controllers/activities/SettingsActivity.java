package com.skichrome.go4lunch.controllers.activities;

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

public class SettingsActivity extends BaseActivity
{
    @BindView(R.id.activity_setting_toolbar_include) Toolbar mToolbar;
    @BindView(R.id.activity_settings_profile_img) ImageView mImageViewProfile;
    @BindView(R.id.activity_settings_name) TextView mTextViewName;
    @BindView(R.id.activity_settings_email) TextView mTextViewEmail;
    @BindView(R.id.activity_settings_delete_account_btn) Button mBtn;

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
}