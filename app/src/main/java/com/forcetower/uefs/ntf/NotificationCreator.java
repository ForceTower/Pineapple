package com.forcetower.uefs.ntf;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db_service.entity.Version;
import com.forcetower.uefs.ru.RUData;
import com.forcetower.uefs.svc.BigTrayService;
import com.forcetower.uefs.svc.UNESFirebaseMessagingService;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.LoggedActivity;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;
import com.forcetower.uefs.view.event.EventDetailsActivity;
import com.forcetower.uefs.view.login.MainActivity;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import timber.log.Timber;

import static com.forcetower.uefs.ntf.NotificationHelper.addOptions;
import static com.forcetower.uefs.ntf.NotificationHelper.createBigText;
import static com.forcetower.uefs.ntf.NotificationHelper.getPendingIntent;
import static com.forcetower.uefs.ntf.NotificationHelper.notificationBuilder;
import static com.forcetower.uefs.ntf.NotificationHelper.showNotification;
import static com.forcetower.uefs.util.WordUtils.validString;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.GRADES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.MESSAGES_FRAGMENT_SAGRES;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.MESSAGES_FRAGMENT_UNES;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class NotificationCreator {

    public static boolean createMessageNotification(@NonNull Context context, @NonNull Message message) {
        Timber.d("Create notification for message...");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notify = preferences.getBoolean("show_message_notification", true);
        //if user opted for not showing notifications and he's not a oreo user (since oreo handles it differently)
        if (!notify && !VersionUtils.isOreo()) {
            Timber.d("Skipped due to preferences");
            return true;
        }

        PendingIntent pendingIntent = getPendingIntent(context, LoggedActivity.class, MESSAGES_FRAGMENT_SAGRES);
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

        PendingIntent pendingIntent = getPendingIntent(context, LoggedActivity.class, GRADES_FRAGMENT);
        NotificationCompat.Builder builder;
        String message;
        boolean notify;

        if (type == 1) {
            builder = notificationBuilder(context, Constants.CHANNEL_GRADES_POSTED_ID)
                    .setContentTitle(context.getString(R.string.new_grade_posted));
            message = context.getString(R.string.grade_posted_notification, info.getEvaluationName(), info.getClassName());
            notify = preferences.getBoolean("show_grades_posted_notification", true);
            try {
                String sGrade = info.getGrade();
                sGrade = sGrade.replaceAll(",", ".");
                double grade = Double.parseDouble(sGrade);
                if (grade >= 7) message = context.getString(R.string.new_grade_notification_great, info.getEvaluationName(), info.getClassName());
            } catch (Exception ignored) {}
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

    public static boolean postMessageNotification(Context context, @StringRes int messageId) {
        PendingIntent pendingIntent = getPendingIntent(context, MainActivity.class, "Login");
        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_WARNINGS_ID)
                .setContentTitle(context.getString(R.string.new_version_notification));

        String message = context.getString(messageId);
        builder.setContentText(message)
                .setStyle(createBigText(message))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        addOptions(context, builder);

        return showNotification(context, message.hashCode(), builder);
    }

    public static void createNotConnectedNotification(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("show_not_connected_notification", true)) {
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
            preferences.edit().putBoolean("show_not_connected_notification", false).apply();
            Timber.d("Preference set");
        }
    }

    public static void createNewVersionNotification(@NonNull Context context, @NonNull Version version) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("UPDATE_NOTIFICATION_" + version.getCode(), false)) return;

        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_WARNINGS_ID)
                .setContentTitle(context.getString(R.string.new_unes_version_available, version.getName()));

        PendingIntent pendingIntent = getPendingIntent(context, MainActivity.class, version.getDownload());
        String message = context.getString(R.string.get_unes_new_version);

        String[] stuff = version.getDetails().split("_;_");
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
            preferences.edit().putBoolean("UPDATE_NOTIFICATION_" + version.getCode(), true).apply();
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
                .setStyle(createBigText(message))
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        addOptions(context, builder);
        showNotification(context, message.hashCode(), builder);
    }

    public static void createNotificationWithMessage(Context context, String title, String message) {
        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_WARNINGS_ID)
                .setContentTitle(title);

        builder.setContentText(message)
                .setStyle(createBigText(message))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        showNotification(context, message.hashCode(), builder);
    }

    public static void createUserNotificationWithMessage(Context context, @StringRes int messageId) {
        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_WARNINGS_ID)
                .setContentTitle(context.getString(R.string.title_warning));

        String message = context.getString(messageId);
        builder.setContentText(message)
                .setStyle(createBigText(message))
                .setColor(ContextCompat.getColor(context, R.color.red));
        addOptions(context, builder);
        showNotification(context, message.hashCode(), builder);
    }

    public static void createEventNotification(Context context, String title, String text, String image, String uuid) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("show_events_notification", false) && !VersionUtils.isOreo()) {
            Timber.d("Setting says this is disabled");
            return;
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent resultIntent = new Intent(context, EventDetailsActivity.class);
        resultIntent.putExtra(EventDetailsActivity.INTENT_UUID, uuid);
        stackBuilder.addParentStack(EventDetailsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_EVENTS_GENERAL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.color_event));

        try {
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(Picasso.with(context).load(image).get()));
        } catch (IOException e) {
            e.printStackTrace();
            Timber.d("Image couldn't be loaded");
        }

        addOptions(context, builder);
        showNotification(context, text.hashCode(), builder);
    }

    public static void createServiceNotification(Context context, String title, String text, String image) {
        PendingIntent pendingIntent = getPendingIntent(context, LoggedActivity.class, MESSAGES_FRAGMENT_UNES);

        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_WARNINGS_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.color_system_notification));

        if (image != null) {
            try {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(Picasso.with(context).load(image).get()));
            } catch (IOException e) {
                e.printStackTrace();
                Timber.d("Image couldn't be loaded");
            }
        } else {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        }

        addOptions(context, builder);
        showNotification(context, text.hashCode(), builder);
    }

    public static void createDCENotification(Context context, String title, String text, String image) {
        PendingIntent pendingIntent = getPendingIntent(context, LoggedActivity.class, MESSAGES_FRAGMENT_UNES);

        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_MESSAGES_DCE_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.color_dce_notification));

        if (image != null) {
            try {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(Picasso.with(context).load(image).get()));
            } catch (IOException e) {
                e.printStackTrace();
                Timber.d("Image couldn't be loaded");
            }
        } else {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        }

        addOptions(context, builder);
        showNotification(context, text.hashCode(), builder);
    }

    public static Notification showBigTrayNotification(Context context, @Nullable RUData data, PendingIntent pending) {
        NotificationCompat.Builder builder = notificationBuilder(context, Constants.CHANNEL_GENERAL_BIG_TRAY)
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentTitle(context.getString(R.string.label_big_tray))
                .setPriority(NotificationManagerCompat.IMPORTANCE_LOW)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentIntent(createBigTrayIntent(context))
                .addAction(R.drawable.ic_close_black_24dp, context.getString(R.string.ru_close_notification), pending);

        String content;
        if (data == null) {
            content = context.getString(R.string.ru_loading);
        } else if (data.isAberto()) {
            int quota = 0;
            try { quota = Integer.parseInt(data.getCotas()); } catch (Throwable ignored){}

            if (quota > 0) {
                content = context.getString(R.string.ru_quota_remaining, quota);
            } else {
                content = context.getString(R.string.ru_quota_exceeded);
            }
        } else if (data.isError()) {
            content = context.getString(R.string.ru_load_failed);
        } else if (!data.isAberto()) {
            content = context.getString(R.string.ru_closed);
        } else {
            content = context.getString(R.string.ru_load_failed);
        }

        builder.setContentText(content);
        return builder.build();
    }

    private static PendingIntent createBigTrayIntent(Context ctx) {
        Intent intent = new Intent(ctx, LoggedActivity.class);
        intent.putExtra(ConnectedFragment.FRAGMENT_INTENT_EXTRA, "BIG_TRAY_DIRECTION");

        return TaskStackBuilder.create(ctx)
                .addParentStack(LoggedActivity.class)
                .addNextIntent(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
