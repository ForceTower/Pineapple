package com.forcetower.uefs.ntf;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import com.forcetower.uefs.R;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;

import timber.log.Timber;

import static com.forcetower.uefs.Constants.CHANNEL_EVENTS_GENERAL_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GENERAL_BIG_TRAY;
import static com.forcetower.uefs.Constants.CHANNEL_GENERAL_REMOTE_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GENERAL_WARNINGS_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GRADES_CHANGED_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GRADES_CREATED_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GRADES_POSTED_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GROUP_EVENTS_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GROUP_GENERAL_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GROUP_GRADES_ID;
import static com.forcetower.uefs.Constants.CHANNEL_GROUP_MESSAGES_ID;
import static com.forcetower.uefs.Constants.CHANNEL_MESSAGES_DCE_ID;
import static com.forcetower.uefs.Constants.CHANNEL_MESSAGES_ID;
import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 08/03/2018.
 */
public class NotificationHelper extends ContextWrapper{
    private NotificationManager notificationManager;
    private SharedPreferences preferences;

    public NotificationHelper(Context base) {
        super(base);
    }

    public void createChannels() {
        if (!VersionUtils.isOreo()) return;

        NotificationChannelGroup gradesGroup = new NotificationChannelGroup(CHANNEL_GROUP_GRADES_ID, getString(R.string.channel_group_grades));
        getManager().createNotificationChannelGroup(gradesGroup);

        NotificationChannelGroup messagesGroup = new NotificationChannelGroup(CHANNEL_GROUP_MESSAGES_ID, getString(R.string.channel_group_messages));
        getManager().createNotificationChannelGroup(messagesGroup);

        NotificationChannelGroup generalGroup = new NotificationChannelGroup(CHANNEL_GROUP_GENERAL_ID, getString(R.string.channel_group_general));
        getManager().createNotificationChannelGroup(generalGroup);

        NotificationChannelGroup eventsGroup = new NotificationChannelGroup(CHANNEL_GROUP_EVENTS_ID, getString(R.string.channel_group_events));
        getManager().createNotificationChannelGroup(eventsGroup);

        NotificationChannel messages    = createChannel(CHANNEL_MESSAGES_ID, getString(R.string.channel_messages), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel posted      = createChannel(CHANNEL_GRADES_POSTED_ID, getString(R.string.channel_grades_posted), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel changed     = createChannel(CHANNEL_GRADES_CHANGED_ID, getString(R.string.channel_grades_date_changed), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel created     = createChannel(CHANNEL_GRADES_CREATED_ID, getString(R.string.channel_grades_created), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel warnings    = createChannel(CHANNEL_GENERAL_WARNINGS_ID, getString(R.string.warnings), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel remote      = createChannel(CHANNEL_GENERAL_REMOTE_ID, getString(R.string.remote), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel eventGen    = createChannel(CHANNEL_EVENTS_GENERAL_ID, getString(R.string.channel_events_general), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel dceMsg      = createChannel(CHANNEL_MESSAGES_DCE_ID, getString(R.string.channel_messages_dce), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationChannel bigTray     = createChannel(CHANNEL_GENERAL_BIG_TRAY, getString(R.string.channel_big_tray_quota), NotificationManager.IMPORTANCE_LOW);

        messages.setGroup(CHANNEL_GROUP_MESSAGES_ID);
        posted.setGroup(CHANNEL_GROUP_GRADES_ID);
        changed.setGroup(CHANNEL_GROUP_GRADES_ID);
        created.setGroup(CHANNEL_GROUP_GRADES_ID);
        warnings.setGroup(CHANNEL_GROUP_GENERAL_ID);
        remote.setGroup(CHANNEL_GROUP_GENERAL_ID);
        eventGen.setGroup(CHANNEL_GROUP_EVENTS_ID);
        dceMsg.setGroup(CHANNEL_GROUP_MESSAGES_ID);
        bigTray.setGroup(CHANNEL_GROUP_GENERAL_ID);

        getManager().createNotificationChannel(messages);
        getManager().createNotificationChannel(posted);
        getManager().createNotificationChannel(changed);
        getManager().createNotificationChannel(created);
        getManager().createNotificationChannel(warnings);
        getManager().createNotificationChannel(remote);
        getManager().createNotificationChannel(eventGen);
        getManager().createNotificationChannel(dceMsg);
        getManager().createNotificationChannel(bigTray);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private NotificationChannel createChannel(String channelId, CharSequence name, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setVibrationPattern(new long[]{150, 300, 150, 300});
        return channel;
    }

    private NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private SharedPreferences getSharedPreferences() {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences;
    }

    public static NotificationCompat.Builder notificationBuilder(Context context, String groupId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, groupId);
        builder.setAutoCancel(true);

        if (VersionUtils.isLollipop())
            builder.setSmallIcon(R.drawable.ic_unes);
        else {
            builder.setSmallIcon(R.drawable.ic_unes_compat);
            builder.setLargeIcon(getIcon(context, R.drawable.ic_unes));
        }

        return builder;
    }

    public static NotificationCompat.BigTextStyle createBigText(String message) {
        return new NotificationCompat.BigTextStyle().bigText(message);
    }

    public static boolean showNotification(Context context, int id, NotificationCompat.Builder builder) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(id, builder.build());
            return true;
        } else {
            Timber.d("Notification was not shown");
            return false;
        }
    }

    public static PendingIntent getPendingIntent(Context context, Class resultClass, String argument) {
        Intent resultIntent = new Intent(context, resultClass);
        if (validString(argument)) resultIntent.putExtra(ConnectedFragment.FRAGMENT_INTENT_EXTRA, argument);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(resultClass);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void addOptions(Context context, NotificationCompat.Builder builder) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!VersionUtils.isOreo()) {
            if (preferences.getBoolean("notifications_new_message_vibrate", true)) {
                builder.setVibrate(new long[]{150, 300, 150, 300});
            }

            Uri ringtone = Uri.parse(preferences.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound"));
            builder.setSound(ringtone);
        }
    }

    public static Bitmap getIcon(Context context, @DrawableRes int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }
}
