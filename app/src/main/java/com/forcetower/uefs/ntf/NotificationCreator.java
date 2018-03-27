package com.forcetower.uefs.ntf;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.ConnectedActivity;
import com.forcetower.uefs.view.login.MainActivity;

import timber.log.Timber;

import static com.forcetower.uefs.ntf.NotificationHelper.addOptions;
import static com.forcetower.uefs.ntf.NotificationHelper.createBigText;
import static com.forcetower.uefs.ntf.NotificationHelper.getPendingIntent;
import static com.forcetower.uefs.ntf.NotificationHelper.notificationBuilder;
import static com.forcetower.uefs.ntf.NotificationHelper.showNotification;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class NotificationCreator {
    public static final String MESSAGES_FRAGMENT = "MessagesFragment";
    public static final String GRADES_FRAGMENT = "GradesFragment";

    public static boolean createMessageNotification(@NonNull Context context, @NonNull Message message) {
        Timber.d("Create notification for message...");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notify = preferences.getBoolean("show_message_notification", true);
        //if user opted for not showing notifications and he's not a oreo user (since oreo handles it differently)
        if (!notify && !VersionUtils.isOreo()) {
            Timber.d("Skipped due to preferences");
            return true;
        }

        PendingIntent pendingIntent = getPendingIntent(context, ConnectedActivity.class, MESSAGES_FRAGMENT);
        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_MESSAGES_ID)
                .setContentTitle(message.getClassReceived())
                .setContentText(message.getMessage())
                .setStyle(createBigText(message.getMessage()))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        addOptions(context, builder);

        return showNotification(context, message.getUid(), builder);
    }

    public static boolean createGradeNotification(@NonNull Context context, @NonNull GradeInfo info, int type) {
        Timber.d("Create notification for a grade posted...");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        PendingIntent pendingIntent = getPendingIntent(context, ConnectedActivity.class, GRADES_FRAGMENT);
        NotificationCompat.Builder builder;
        String message;
        boolean notify;

        if (type == 1) {
            builder = notificationBuilder(context, Constants.CHANNEL_GRADES_POSTED_ID)
                    .setContentTitle(context.getString(R.string.new_grade_posted));
            message = context.getString(R.string.grade_posted_notification, info.getEvaluationName(), info.getClassName());
            notify = preferences.getBoolean("show_grades_posted_notification", true);
        }

        else if(type == 2) {
            builder = notificationBuilder(context, Constants.CHANNEL_GRADES_CREATED_ID)
                    .setContentTitle(context.getString(R.string.new_grade_created));
            message = context.getString(R.string.grade_create_notification, info.getEvaluationName(), info.getClassName());
            notify = preferences.getBoolean("show_grades_created_notification", false);
        }

        else  {
            builder = notificationBuilder(context, Constants.CHANNEL_GRADES_CHANGED_ID)
                    .setContentTitle(context.getString(R.string.grade_evaluation_date_change));
            message = context.getString(R.string.grade_change_notification, info.getEvaluationName(), info.getClassName());

            notify = preferences.getBoolean("show_grades_changed_notification", false);
        }

        //if user opted for not showing notifications and he's not a oreo user (since oreo handles it differently)
        if (!notify && !VersionUtils.isOreo()) {
            Timber.d("Skipped due to preferences");
            return true;
        }

        builder.setContentText(message)
                .setStyle(createBigText(message))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        addOptions(context, builder);
        return showNotification(context, info.getUid(), builder);
    }

    public static void createNotConnectedNotification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("show_not_connected_notification", true)) {
            Timber.d("Already shown");
            return;
        }

        PendingIntent pendingIntent = getPendingIntent(context, MainActivity.class, "Login");
        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_WARNINGS_ID)
                .setContentTitle(context.getString(R.string.login));

        String message = context.getString(R.string.you_are_not_connected);
        builder.setContentText(message)
                .setStyle(createBigText(message))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        addOptions(context, builder);

        boolean notify = showNotification(context, message.hashCode(), builder);
        if (notify) {
            preferences.edit().putBoolean("show_not_connected_notification", true).apply();
            Timber.d("Preference set");
        }
    }
}
