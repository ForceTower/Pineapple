package com.forcetower.uefs.worker.event;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.util.VersionUtils;
import com.google.gson.Gson;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by João Paulo on 17/06/2018.
 */
public class EventScheduler {

    public static void scheduleEventCreation(Event event, FirebaseJobDispatcher dispatcher, Context context) {
        String sEvent = new Gson().toJson(event);
        if (VersionUtils.isLollipop()) {
            scheduleEventCreationLollipop(context, sEvent);
        }
    }

    @RequiresApi(api = LOLLIPOP)
    private static void scheduleEventCreationLollipop(Context context, String event) {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("event", event);

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        JobInfo.Builder builder = new JobInfo.Builder(Constants.CREATE_EVENT_ID, new ComponentName(context, CreateEventWorker.class));
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setExtras(bundle);
        builder.setBackoffCriteria(10000, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        assert scheduler != null;
        scheduler.schedule(builder.build());
    }
}
