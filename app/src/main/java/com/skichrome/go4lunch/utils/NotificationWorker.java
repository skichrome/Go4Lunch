package com.skichrome.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.models.FormattedPlace;
import com.skichrome.go4lunch.models.firestore.User;
import com.skichrome.go4lunch.utils.firebase.UserHelper;

import java.util.List;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.skichrome.go4lunch.controllers.fragments.SettingFragment.SW_NOTIFICATION_KEY_PREF;

/**
 * <h1>Worker class to launch a notification</h1>
 * <p>
 *     Worker class to create a notification with detailed information about the user selected restaurant.
 * </p>
 */
public class NotificationWorker extends Worker
{
    /**
     * Tag for the notification  manager.
     */
    private static final String NOTIFICATION_TAG = "NOTIFICATION TAG";
    /**
     * Id for the notification manager.
     */
    private static final int NOTIFICATION_ID = 12;

    /**
     * Default constructor, needed to compile but not used here.
     */
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    /**
     * Main method of Worker class, here the background task is launched.<br/>
     * We need to control if the user has enabled notifications before creating the notification.
     * @return
     *      Result status.
     */
    @NonNull @Override
    public Result doWork()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isActivated = preferences.getBoolean(SW_NOTIFICATION_KEY_PREF, false);

        if (!isActivated)
            return Result.failure();

        this.getUserRestaurant();
        return Result.success();
    }

    /**
     * <h1>Download selected restaurant on Cloud Firestore</h1>
     * <p>
     *     Download the restaurant selected by the user, and also all workmates that join with you.
     * </p>
     */
    private void getUserRestaurant()
    {
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnSuccessListener(success ->
                {
                    User user = success.toObject(User.class);
                    FormattedPlace selectedPlace = user.getSelectedPlace();
                    UserHelper.getUsersInterestedByPlace(selectedPlace.getId())
                            .addOnSuccessListener(successList ->
                            {
                                List<User> userList = successList.toObjects(User.class);
                                sendVisualNotification(selectedPlace, userList);
                                updateSelectedPlaceStatus();
                            });
                });
    }

    /**
     * <h1>Create and send a notification to the system</h1>
     * <p>
     *      Create a notification that will launch {@link RestaurantDetailsActivity} on click.
     *      The notification is composed by a subhead (name of the map), a title (that indicates that it is time to go eat),
     *      a field with the address of the restaurant and the rest of the space available is filled with some workmates.
     * </p>
     * @param selectedPlace
     *      The restaurant selected by the user.
     * @param users
     * \        All workmates that will come into this restaurant.
     */
    private void sendVisualNotification(FormattedPlace selectedPlace, List<User> users)
    {
        Context context = getApplicationContext();

        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, selectedPlace);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.notification_big_title));
        inboxStyle.addLine(selectedPlace.getName());
        inboxStyle.addLine(selectedPlace.getAddress());

        for (int i = 0; i < users.size(); i++)
        {
            inboxStyle.addLine(users.get(i).getUsername());
            if (i >= 4)
                break;
        }

        String channelId = "Channel Id";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_swipe_for_more))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.notification_channel_name), importance);
                manager.createNotificationChannel(channel);
            }
            manager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Reset of the selected place on Cloud Firestore database.
     */
    private void updateSelectedPlaceStatus()
    {
        UserHelper.updateChosenPlace(FirebaseAuth.getInstance().getCurrentUser().getUid(), null)
                .addOnSuccessListener(success -> Log.d("Notification service", "Successfully updated user place to null"))
                .addOnFailureListener(throwable -> Log.d("Notification service", "Error when update user place", throwable));
    }
}