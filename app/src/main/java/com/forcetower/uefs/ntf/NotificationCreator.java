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
import com.forcetower.uefs.service.Version;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.ConnectedActivity;
import com.forcetower.uefs.view.download.DownloadActivity;
import com.forcetower.uefs.view.login.MainActivity;
import com.google.firebase.messaging.RemoteMessage;

import timber.log.Timber;

import static com.forcetower.uefs.ntf.NotificationHelper.addOptions;
import static com.forcetower.uefs.ntf.NotificationHelper.createBigText;
import static com.forcetower.uefs.ntf.NotificationHelper.getPendingIntent;
import static com.forcetower.uefs.ntf.NotificationHelper.notificationBuilder;
import static com.forcetower.uefs.ntf.NotificationHelper.showNotification;
import static com.forcetower.uefs.util.WordUtils.validString;

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

    public static void createNewVersionNotification(@NonNull Context context, @NonNull Version version) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("UPDATE_NOTIFICATION_" + version.getVersionCode(), false)) return;

        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_WARNINGS_ID)
                .setContentTitle(context.getString(R.string.new_unes_version_available, version.getVersionName()));

        PendingIntent pendingIntent = getPendingIntent(context, MainActivity.class, version.getDownloadLink());
        String message = context.getString(R.string.get_unes_new_version);

        String[] stuff = version.getWhatsNew().split("_;_");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stuff.length; i++) {
            String str = stuff[i];
            stringBuilder.append(str);
            if (i != stuff.length - 1) stringBuilder.append("\n");
        }
        String text = stringBuilder.toString();

        builder.setContentText(message)
                .setStyle(createBigText(text))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        addOptions(context, builder);
        boolean notify = showNotification(context, message.hashCode(), builder);
        if (notify) {
            preferences.edit().putBoolean("UPDATE_NOTIFICATION_" + version.getVersionCode(), true).apply();
            Timber.d("Preference for update set");
        }
    }

    public static void createFirebaseSimpleNotification(Context context, RemoteMessage.Notification notification) {
        String message = notification.getBody();
        if (!validString(message)) return;

        Timber.d("Notification is being created");

        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_REMOTE_ID)
                .setContentTitle(notification.getTitle());

        PendingIntent pendingIntent = getPendingIntent(context, MainActivity.class, null);
        builder.setContentText(message)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        addOptions(context, builder);
        showNotification(context, message.hashCode(), builder);
    }
}
