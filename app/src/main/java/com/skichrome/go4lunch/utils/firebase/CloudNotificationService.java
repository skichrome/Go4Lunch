//package com.skichrome.go4lunch.utils.firebase;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.RingtoneManager;
//import android.os.Build;
//import android.preference.PreferenceManager;
//import android.support.v4.app.NotificationCompat;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.skichrome.go4lunch.R;
//import com.skichrome.go4lunch.controllers.activities.RestaurantDetailsActivity;
//import com.skichrome.go4lunch.controllers.fragments.SettingFragment;
//import com.skichrome.go4lunch.models.FormattedPlace;
//import com.skichrome.go4lunch.models.firestore.User;
//
//import java.util.List;
//
//public class CloudNotificationService extends FirebaseMessagingService
//{
//    private static final int NOTIFICATION_ID = 78;
//    private static final String NOTIFICATION_TAG = "Go4Lunch";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage)
//    {
//        super.onMessageReceived(remoteMessage);
//
//        Context context = this;
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        if (!preferences.getBoolean(SettingFragment.SW_NOTIFICATION_KEY_PREF, false))
//            return;
//
//        if (remoteMessage.getNotification() != null)
//        {
////            FirebaseMessaging.getInstance().unsubscribeFromTopic(RestaurantDetailsActivity.FIREBASE_TOPIC);
////            UserHelper.updateChosenPlace(FirebaseAuth.getInstance().getCurrentUser().getUid(), null);
//
//            String message = remoteMessage.getNotification().getBody();
//            this.getUserRestaurant(message);
//        }
//    }
//
//    public void getUserRestaurant(String message)
//    {
//        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(success ->
//        {
//            User user = success.toObject(User.class);
//            FormattedPlace selectedPlace = user.getSelectedPlace();
//
//            UserHelper.getUsersInterestedByPlace(selectedPlace.getId()).addOnSuccessListener(successList ->
//            {
//                List<User> userList = successList.toObjects(User.class);
//                sendVisualNotification(message, selectedPlace, userList);
//            });
//        });
//    }
//
//    private void sendVisualNotification(String messageBody, FormattedPlace place, List<User> userList)
//    {
//        String channelId = "Channel Id";
//
//        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
//        intent.putExtra(RestaurantDetailsActivity.ACTIVITY_DETAILS_CODE, place);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        inboxStyle.setBigContentTitle(messageBody);
//        inboxStyle.addLine(place.getName());
//        inboxStyle.addLine(place.getAddress());
//
//        for (int i = 0; i < userList.size(); i++)
//        {
//            inboxStyle.addLine(userList.get(i).getUsername());
//            if (i >= 4)
//                break;
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("swiper pour plus d'infos")
//                .setAutoCancel(true)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentIntent(pendingIntent)
//                .setStyle(inboxStyle);
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (manager != null)
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            {
//                CharSequence channelName = getString(R.string.notification_channel_name);
//                int importance = NotificationManager.IMPORTANCE_HIGH;
//                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
//                manager.createNotificationChannel(channel);
//            }
//            manager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
//        }
//    }
//}