package com.forcetower.uefs.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.view.login.LoginActivity;

/**
 * Created by João Paulo on 11/11/2017.
 */

public class NotificationWake extends BroadcastReceiver {

    public static void generateClassNotification(Context context, String name, String modulo, String room) {
        NotificationCompat.BigTextStyle textStyle = new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.class_notification_display, name, room, modulo));

        Intent resultIntent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Constants.CLASSES_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.class_about_to_start))
                .setContentText(context.getString(R.string.class_notification_display, name, room, modulo))
                .setAutoCancel(true)
                .setStyle(textStyle)
                .setContentIntent(resultPendingIntent)
                .setColorized(true)
                .setVibrate(new long[]{150, 300, 150, 300})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(Constants.CLASSES_ID, notificationBuilder.build());
        } else {
            Log.e(Constants.APP_TAG, "Alarm manager failed, it is null");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.APP_TAG, "Notify class starting");

        String name = intent.getStringExtra("name");
        String room = intent.getStringExtra("room");
        String modulo = intent.getStringExtra("modulo");
        generateClassNotification(context, name, modulo, room);
    }
}
