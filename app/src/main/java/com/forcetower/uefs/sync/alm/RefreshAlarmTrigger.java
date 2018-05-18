package com.forcetower.uefs.sync.alm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.util.VersionUtils;

import java.util.Calendar;

import timber.log.Timber;

public class RefreshAlarmTrigger {

    public static void create(Context context) {
        if (context == null) return;
        /*
        if (minutes < 30 && minutes != -1 && !BuildConfig.DEBUG) {
            minutes = 30;
            Timber.d("Reset to 30 min");
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("sync_frequency", "30").apply();
        }
        */
        if (VersionUtils.isOreo()) {
            Timber.d("Doing oreo call");
            doOreo(context, 7);
        } else {
            Timber.d("Doing common call");
            doCommon(context, 7);
        }
    }

    private static void doCommon(Context context, int minutes) {
        Intent intent = new Intent("com.forcetower.uefs.REFRESH");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 2);

        Timber.d("Refreshing N O W");
        Timber.d("Frequency will be set to %d minutes", minutes);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //noinspection ConstantConditions
        alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                (minutes - 5) * 60 * 1000, pendingIntent);
        Timber.d("Common: frequency set");
    }

    private static void doOreo(Context context, int minutes) {
        Intent intent = new Intent(context, RefreshBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 2);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //noinspection ConstantConditions
        alarmManager.cancel(pendingIntent);
        Timber.d("Refreshing N O W");
        Timber.d("Frequency will be set to %d minutes", minutes);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (minutes - 5) * 60* 1000, pendingIntent);
    }

    public static void enableBootComponent(Context context) {
        if (context == null) return;

        NotificationCreator.createNotificationWithDevMessage(context, "Enable boot component!");

        ComponentName receiver = new ComponentName(context, BootOptionsBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void removeAlarm(Context context) {
        Intent intent = new Intent("com.forcetower.uefs.REFRESH");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //noinspection ConstantConditions
        alarmManager.cancel(pendingIntent);
        NotificationCreator.createNotificationWithDevMessage(context, "Alarm Manager Cancel");
    }

    public static void disableBootComponent(Context context) {
        ComponentName receiver = new ComponentName(context, BootOptionsBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        NotificationCreator.createNotificationWithDevMessage(context, "Disable boot component!");
    }
}