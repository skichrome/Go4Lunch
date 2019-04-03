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

/**
 * Settings screen, to modify username and enable / disable notifications.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    /**
     * Id for notification switch in sharedPreferences.
     */
    public static final String SW_NOTIFICATION_KEY_PREF = "switch_preference_1";
    /**
     * Id for username field in sharedPreferences.
     */
    private static final String EDIT_TEXT_KEY_PREF = "edit_text_preference_1";
    /**
     * Id for data in bundle.
     */
    private static final String BUNDLE_STR_ARG = "user_id";

    /**
     * Default empty constructor for fragment.
     */
    public SettingFragment() { }

    /**
     * Create a new instance of this fragment, with a string in bundle that represent the user id logged in.
     * @param userId
     *      The id of the user logged in.
     * @return
     *      New instance of this fragment.
     */
    public static SettingFragment newInstance(String userId)
    {
        SettingFragment frag = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_STR_ARG, userId);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Add preferences fields from xml.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    /**
     * Set listener on sharedPreferences.
     */
    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Disable listener on sharedPreferences.
     */
    @Override
    public void onPause()
    {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     *
     * @param sharedPreferences
     * @param key
     */
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

    /**
     * <h1>SharedPreferences modification callback</h1>
     * <p>
     *     Get the selected place for logged in user, and if not null and if switch is on,
     *     enable WorkManager to show a notification at 12h, else disable WorkManager.
     * </p>
     * @param isEnabled
     *      True if the switch is on true, false in the other case.
     */
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

    /**
     * <h1>Delta to 12h calculation</h1>
     * <p>
     *     <ul>
     *         <li>Morning : The method return the difference between now and 12h of today.</li>
     *         <li>Afternoon : The method return the time between now and 12h of the next day.</li>
     *     </ul>
     * </p>
     * @param calendarNow
     *      Time at execution
     * @param calendarMidDay
     *      time at 12h
     * @return
     *      Long, the difference between two Calendar instances.
     */
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

    /**
     * <h1>Get the calendar instance and set it to 12h</h1>
     * @return
     *      corrected calendar instance to 12h.
     */
    public static Calendar configureMidDayCalendar()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}