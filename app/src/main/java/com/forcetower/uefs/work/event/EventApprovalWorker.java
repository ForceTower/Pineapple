package com.forcetower.uefs.work.event;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.UNEService;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class EventApprovalWorker extends Job {
    public static final String TAG = Constants.APPROVE_EVENT_WORKER;

    @Inject
    UNEService service;
    @Inject
    ServiceDatabase database;

    public static void createWorker(String uuid) {
        PersistableBundleCompat data = new PersistableBundleCompat();
        data.putString("event_uuid", uuid);

        new JobRequest.Builder(TAG)
                .setBackoffCriteria(5_000L,JobRequest.BackoffPolicy.EXPONENTIAL)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setExtras(data)
                .setExecutionWindow(10_000L, 15_000L)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    public Result onRunJob(@NonNull Params params) {
        ((UEFSApplication)getContext().getApplicationContext()).getAppComponent().inject(this);

        String uuid = params.getExtras().getString("event_uuid", null);
        if (uuid == null) {
            Timber.d("Uuid is null. leaving..");
            return Result.FAILURE;
        }
        Call<ActionResult<Event>> call = service.approveEvent(uuid);
        try {
            Response<ActionResult<Event>> response = call.execute();
            if (response.isSuccessful()) {
                ActionResult<Event> body = response.body();
                if (body != null && body.getData() != null) {
                    database.eventDao().markEventApproved(uuid);
                    return Result.SUCCESS;
                } else {
                    Timber.d("Response data is null " + body);
                    NotificationCreator.createNotificationWithMessage(getContext(), "Approve Event", "Approval failed, response data is null");
                    return Result.FAILURE;
                }
            } else {
                if (response.code() == 403) {
                    NotificationCreator.createNotificationWithMessage(getContext(), "Approve Event", "No permission");
                    return Result.FAILURE;
                } else if (response.code() == 500) {
                    return Result.RESCHEDULE;
                } else {
                    NotificationCreator.createNotificationWithMessage(getContext(), "Approve Event", "Failed with code: " + response.code());
                    return Result.FAILURE;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.RESCHEDULE;
        }
    }
}
