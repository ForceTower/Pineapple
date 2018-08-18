package com.forcetower.uefs.work.sync;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.util.VersionUtils;

import timber.log.Timber;

public class JobSyncUtils {

    public static void setupSagresSync(FirebaseJobDispatcher dispatcher, Context context, int frequency) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int oldFreq = preferences.getInt("sagres_sync_worker_frequency_xl", 45);
        boolean replace = oldFreq != frequency;
        Timber.d("Same frequency sync? %s", replace);

        if (frequency == -1) {
            cancelSyncService(dispatcher, context);
            return;
        }

        boolean result;
        if (VersionUtils.isLollipop()) {
            result = scheduleJobLollipop(context, frequency);
        } else {
            result = scheduleJobOther(dispatcher, frequency);
        }

        if (result) {
            preferences.edit()
                    .putBoolean("sagres_sync_worker_configured_application_xl", true)
                    .putInt("sagres_sync_worker_frequency_xl", frequency)
                    .apply();
            Timber.d("Job scheduled");
        } else {
            Timber.d("Failed to schedule job");
            NotificationCreator.createUserNotificationWithMessage(context, R.string.failed_to_schedule_job);
        }
    }


    public static void cancelSyncService(FirebaseJobDispatcher dispatcher, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (VersionUtils.isLollipop())
            cancelLollipop(context);
        else
            cancelSyncOther(dispatcher);

        Timber.d("Configuration erased");
        preferences.edit().putBoolean("sagres_sync_worker_configured_application_xl", false).apply();
        preferences.edit().putInt("sagres_sync_worker_frequency", -1).apply();
    }

    @TargetApi(21)
    private static boolean scheduleJobLollipop(Context context, int frequency) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder infoBuilder = new JobInfo.Builder(Constants.SAGRES_SYNC_ID, new ComponentName(context, SyncJobScheduler.class));
        infoBuilder.setPeriodic(frequency * 60 * 1000);
        infoBuilder.setPersisted(true);
        assert scheduler != null;
        try {
            scheduler.cancel(Constants.SAGRES_SYNC_ID);
            int result = scheduler.schedule(infoBuilder.build());
            Timber.d("Schedule Lollipop: %s", result == JobScheduler.RESULT_SUCCESS);
            return result == JobScheduler.RESULT_SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            try {
                scheduler.cancelAll();
                int result = scheduler.schedule(infoBuilder.build());
                return result == JobScheduler.RESULT_SUCCESS;
            } catch (Exception ignored) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("scheduler_critical_adr_5", true).apply();
                return false;
            }
        }
    }

    private static boolean scheduleJobOther(FirebaseJobDispatcher dispatcher, int frequency) {
        Job sync = dispatcher.newJobBuilder()
                .setService(SyncJobService.class)
                .addConstraint(Constraint.ON_ANY_NETWORK)
                .setTag(Constants.WORKER_SYNC_SAGRES_NAME)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(5 * 60, frequency * 60))
                .build();

        try {
            dispatcher.mustSchedule(sync);
            Timber.d("Schedule Pre-Lollipop: %s", true);
            return true;
        } catch (Exception e) {
            Timber.e("Scheduling failed");
            e.printStackTrace();
            Timber.d("Schedule Pre-Lollipop: %s", false);
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void cancelLollipop(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        scheduler.cancel(Constants.SAGRES_SYNC_ID);
    }

    private static void cancelSyncOther(FirebaseJobDispatcher dispatcher) {
        dispatcher.cancel(Constants.WORKER_SYNC_SAGRES_NAME);
    }
}
