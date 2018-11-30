package com.skichrome.go4lunch.controllers.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String EDIT_TEXT_KEY_PREF = "edit_text_preference_1";
    private static final String BUNDLE_STR_ARG = "user_id";

    public SettingFragment() { }

    public static SettingFragment newInstance(String userId)
    {
        SettingFragment frag = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_STR_ARG, userId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        String userId = getArguments().getString(BUNDLE_STR_ARG);
        if (key.equals(EDIT_TEXT_KEY_PREF))
        {
            String modifiedName = sharedPreferences.getString(key, null);
            UserHelper.updateUsername(userId, modifiedName)
                    .addOnSuccessListener(getActivity(), success ->
                            Log.d(getClass().getSimpleName(), "Username successfully updated."))
                    .addOnFailureListener(getActivity(), throwable ->
                    Log.e(getClass().getSimpleName(), "Error when update username in settings ; ", throwable));
        }
    }
}