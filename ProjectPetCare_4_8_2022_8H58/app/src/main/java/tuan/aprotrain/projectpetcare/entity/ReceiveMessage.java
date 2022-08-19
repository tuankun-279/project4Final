package tuan.aprotrain.projectpetcare.entity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.inappmessaging.model.ImageData;
import com.google.firebase.inappmessaging.model.MessageType;
import com.google.firebase.inappmessaging.model.Text;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tuan.aprotrain.projectpetcare.R;
import tuan.aprotrain.projectpetcare.activities.MainActivity;

public class ReceiveMessage extends FirebaseMessagingService {
    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";
        CharSequence name;


        Intent intentYes = new Intent(getBaseContext(), MainActivity.class);
        intentYes.putExtra("Yes",true);
        intentYes.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntentYes = PendingIntent.getActivity(getBaseContext(),
                0,intentYes,PendingIntent.FLAG_IMMUTABLE);

        Intent intentNo = new Intent(getBaseContext(), MainActivity.class);
        intentNo.putExtra("No",false);
        intentNo.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntentNo = PendingIntent.getActivity(getBaseContext(),
                1,intentNo,PendingIntent.FLAG_IMMUTABLE);

        Intent clickIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder  = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(clickIntent);

        PendingIntent clickPendingIntent = stackBuilder.getPendingIntent(3,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gigapet);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(bitmap)
                .setContentIntent(clickPendingIntent)
                .addAction(R.drawable.gigapet,"Yes",pendingIntentYes)
                .addAction(R.drawable.gigapet,"No",pendingIntentNo)
                .setAutoCancel(true);

//        notification.addAction(R.drawable.ic_launcher_foreground,"Yes",pendingIntentYes);
//        notification.addAction(R.drawable.ic_launcher_foreground,"No",pendingIntentNo);

        NotificationManagerCompat.from(getApplicationContext()).notify(1, notification.build());
        //insert a fake record to Firebase Realtime DB
        super.onMessageReceived(message);

    }


}
