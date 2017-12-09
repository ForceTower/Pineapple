package com.forcetower.uefs.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.LoginActivity;
import com.forcetower.uefs.sagres_sdk.domain.GradeInfo;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 20/11/2017.
 */

public class NotificationCreator {
    private static int count = 0;
    public static final int GENERATED_GRADE = 0;
    public static final int CHANGED_GRADE = 1;
    public static final int ADDED_GRADE = 2;
    public static final int SOMETHING_DIFFERENT_GRADE = 4;

    public static void createNewMessageNotification(Context context, SagresMessage message) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        /*boolean showNotification = preferences.getBoolean("show_message_notification", true);
        if (!showNotification) {
            Log.i(APP_TAG, "Messages notifications are disabled, message will be omitted");
            return;
        }*/

        Log.i(APP_TAG, "New message received. Creating notification...");

        //Sets the icon
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_temp_launcher_round);
        //Show a big text - Allow user to see a great part of the message
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle().bigText(message.getMessage());

        //Setup to open app when select the notification [Improve this to go strait to the message]
        Intent resultIntent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Constants.MESSAGES_CHANNEL)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_temp_launcher_round)
                .setLargeIcon(largeIcon)
                .setContentTitle(message.getClassName())
                .setContentText(message.getMessage())
                .setStyle(bigText)
                .setContentIntent(resultPendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        if (preferences.getBoolean("notifications_new_message_vibrate", true)) {
            notificationBuilder.setVibrate(new long[]{150, 300, 150, 300});
        }

        Uri ringtone = Uri.parse(preferences.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound"));
        notificationBuilder.setSound(ringtone);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            /*if (Utils.isOreo()) {
                NotificationChannel messagesChannel = new NotificationChannel(Constants.MESSAGES_CHANNEL, context.getString(R.string.title_messages), NotificationManager.IMPORTANCE_DEFAULT);
                //Causes UI Crash !! DO NOT USE THIS
                notificationManager.createNotificationChannel(messagesChannel);
            }*/
            notificationManager.notify(message.hashCode(), notificationBuilder.build());
        } else {
            Log.e(Constants.APP_TAG, "Alarm manager failed, it is null");
        }
    }

    public static void createNewGradeNotification(Context context, GradeInfo grade, SagresGrade sagresGrade, int code) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean showNotification = preferences.getBoolean("show_message_notification", true);
        if (!showNotification) {
            Log.i(APP_TAG, "Grades notifications are disabled, notification will be omitted");
            return;
        }

        Log.i(APP_TAG, "Grade alteration, generation notification");

        //Sets the icon
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_temp_launcher_round);
        //Setup to open app when select the notification [Improve this to go strait to the message]
        Intent resultIntent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.new_grade_notification, grade.getEvaluationName(), sagresGrade.getClassName()));

        try {
            String gra = grade.getGrade().replace(",", ".");
            double val = Double.parseDouble(gra);
            if (val >= 7) {
                bigText = new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.new_grade_notification_great, grade.getEvaluationName(), sagresGrade.getClassName()));
            }
        } catch (NumberFormatException ignored) {}

        //Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Constants.MESSAGES_CHANNEL)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_temp_launcher_round)
                .setLargeIcon(largeIcon)
                .setStyle(bigText)
                .setContentTitle(context.getString(R.string.grade_posted))
                .setContentText(context.getString(R.string.new_grade_notification, grade.getEvaluationName(), sagresGrade.getClassName()))
                .setContentIntent(resultPendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        if (preferences.getBoolean("notifications_new_message_vibrate", true)) {
            notificationBuilder.setVibrate(new long[]{150, 300, 150, 300});
        }

        Uri ringtone = Uri.parse(preferences.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound"));
        notificationBuilder.setSound(ringtone);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            /*if (Utils.isOreo()) {
                NotificationChannel messagesChannel = new NotificationChannel(Constants.MESSAGES_CHANNEL, context.getString(R.string.title_messages), NotificationManager.IMPORTANCE_DEFAULT);
                //Causes UI Crash !! DO NOT USE THIS
                notificationManager.createNotificationChannel(messagesChannel);
            }*/
            notificationManager.notify(grade.hashCode() + count++, notificationBuilder.build());
        } else {
            Log.e(Constants.APP_TAG, "Alarm manager failed, it is null");
        }
    }
}
