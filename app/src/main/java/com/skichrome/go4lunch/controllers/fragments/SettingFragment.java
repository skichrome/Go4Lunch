package com.skichrome.go4lunch.controllers.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.NotificationWorker;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    public static final String SW_NOTIFICATION_KEY_PREF = "switch_preference_1";
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
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        String userId = getArguments().getString(BUNDLE_STR_ARG);
        switch (key)
        {
            case EDIT_TEXT_KEY_PREF:
                String modifiedName = sharedPreferences.getString(key, null);
                UserHelper.updateUsername(userId, modifiedName)
                        .addOnSuccessListener(getActivity(), success ->
                                Log.d(getClass().getSimpleName(), "Username successfully updated."))
                        .addOnFailureListener(getActivity(), throwable ->
                                Log.e(getClass().getSimpleName(), "Error when update username in settings ; ", throwable));
                break;
            case SW_NOTIFICATION_KEY_PREF:
                    this.configureNotification(sharedPreferences.getBoolean(SW_NOTIFICATION_KEY_PREF, false));
                break;
            default:
                break;
        }
    }

    private void configureNotification(final Boolean isEnabled)
    {
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnSuccessListener(success ->
                {
                    User user = success.toObject(User.class);
                    if (user.getSelectedPlace() == null)
                        return;

                    if (isEnabled)
                    {
                        Constraints constraints = new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build();

                        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                                .setConstraints(constraints)
                                .setInitialDelay(getDeltaCalendar(Calendar.getInstance(), configureMidDayCalendar()), TimeUnit.MILLISECONDS)
                                .build();
                        WorkManager.getInstance().enqueue(request);
                    }
                    else
                    {
                        WorkManager.getInstance().cancelAllWork();
                    }
                });
    }

    public static long getDeltaCalendar(Calendar calendarNow, Calendar calendarMidDay)
    {
        long delta = calendarMidDay.getTimeInMillis() - calendarNow.getTimeInMillis();
        if (delta < 0)
        {
            calendarMidDay.set(Calendar.DAY_OF_MONTH, calendarMidDay.get(Calendar.DAY_OF_MONTH) + 1);
            delta = calendarMidDay.getTimeInMillis() - calendarNow.getTimeInMillis();
        }
        Log.e("DeltaCalendar", "Time to the next notification in minuts : " + delta / 1000 / 60);
        return delta;
    }

    public static Calendar configureMidDayCalendar()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}