package com.skichrome.go4lunch.utils.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.skichrome.go4lunch.R;
import com.skichrome.go4lunch.controllers.activities.MainActivity;
import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.skichrome.go4lunch.models.firestore.User;

import static com.skichrome.go4lunch.utils.FireStoreAuthentication.ID_PLACE_INTEREST_CLOUD_FIRESTORE;

public class CloudNotificationService extends FirebaseMessagingService
{
    private final int NOTIFICATION_ID = 78;
    private final String NOTIFICATION_TAG = "Go4Lunch";

    @Override
    public void onMessageReceived(RemoteMessage mRemoteMessage)
    {
        super.onMessageReceived(mRemoteMessage);
        if (mRemoteMessage.getNotification() != null)
        {
            String message = mRemoteMessage.getNotification().getBody();
            updateDatabase();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(RestaurantDetailsActivity.FIREBASE_TOPIC);
            sendVisualNotification(message);
        }
    }

    private void sendVisualNotification(String messageBody)
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.app_name));
        inboxStyle.addLine(messageBody);

        String channelId = "Channel Id";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Content text")
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

    private void updateDatabase()  // Todo change this implementation to activity
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("---- Debug ----", "updateDatabase: " + (firebaseUser == null));
        if (firebaseUser != null)
        {
            UserHelper.getUser(firebaseUser.getUid()).addOnSuccessListener(mDocumentSnapshot ->
            {
                User user = mDocumentSnapshot.toObject(User.class);
                if (user != null && user.getSelectedPlace() != null) PlaceTypeHelper.removeUserIntoPlace(ID_PLACE_INTEREST_CLOUD_FIRESTORE, user.getUid(), user.getSelectedPlace().getId())
                        .addOnSuccessListener(mVoid -> Toast.makeText(getApplicationContext(), "Update Successful !", Toast.LENGTH_SHORT).show());
            });
        }
    }
}