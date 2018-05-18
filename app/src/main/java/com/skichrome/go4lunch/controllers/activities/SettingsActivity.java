package com.skichrome.go4lunch.controllers.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.base.BaseActivity;
import com.skichrome.go4lunch.controllers.fragments.SettingFragment;
import com.skichrome.go4lunch.utils.FireBaseAuthentication;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity
{
    @BindView(R.id.activity_setting_toolbar_include) Toolbar toolbar;
    @BindView(R.id.activity_setting_progress_bar) ProgressBar progressBar;
    @BindView(R.id.activity_settings_profile_img) ImageView imageViewProfile;
    @BindView(R.id.activity_settings_name) TextView textViewName;
    @BindView(R.id.activity_settings_email) TextView textViewEmail;
    @BindView(R.id.activity_settings_delete_account_btn) Button btn;

    private FireBaseAuthentication fireBaseAuthentication = new FireBaseAuthentication(this);

    @Override
    protected int getActivityLayout()
    {
        return R.layout.activity_settings;
    }

    @Override
    protected void configureActivity()
    {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.configureToolbar();
        this.updateUserInfo();
    }

    private void updateUserInfo()
    {
        if (isCurrentUserLogged())
        {
            FirebaseUser user = getCurrentUser();

            textViewName.setText(user.getDisplayName());
            textViewEmail.setText(user.getEmail());

            if (user.getPhotoUrl() != null)
                Glide.with(this).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);
        }
    }

    @Override
    protected void updateActivityWithPermissionGranted()
    {
        getFragmentManager().beginTransaction().replace(R.id.activity_settings_fragment_container, new SettingFragment()).commit();
    }

    private void configureToolbar()
    {
        toolbar.setTitle(R.string.toolbar_title_settings);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.activity_settings_delete_account_btn)
    public void onClickDeleteAccountBtn()
    {
        new AlertDialog.Builder(this)
                .setMessage(R.string.alert_dialog_confirmation_msg)
                .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        fireBaseAuthentication.deleteAccountFromFirebase();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_no, null)
                .show();
        Log.i("OnClick Method", "onClickDeleteAccountBtn : Button delete clicked !");
    }

    public void disableProgressBar()
    {
        progressBar.setVisibility(View.GONE);
    }
}