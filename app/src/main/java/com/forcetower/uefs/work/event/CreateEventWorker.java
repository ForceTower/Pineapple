package com.forcetower.uefs.work.event;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.UNEService;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class CreateEventWorker extends Job {
    public static final String TAG = Constants.WORKER_CREATE_EVENT;

    public static void invokeWorker(@NonNull Event event) {
        String string = new Gson().toJson(event);

        PersistableBundleCompat data = new PersistableBundleCompat();
        data.putString("event", string);

        new JobRequest.Builder(TAG)
                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setExecutionWindow(10_000L, 15_000L)
                .setExtras(data)
                .build()
                .schedule();
    }

    @Inject
    UNEService service;

    @NonNull
    @Override
    public Result onRunJob(@NonNull Params params) {
        ((UEFSApplication)getContext().getApplicationContext()).getAppComponent().inject(this);
        String sEvent = params.getExtras().getString("event", null);
        if (!validString(sEvent)) return Result.FAILURE;

        Event event;
        try {
            event = new Gson().fromJson(sEvent, Event.class);
        } catch (Exception e) {
            return Result.FAILURE;
        }

        Timber.d("Create Event Worker Invoked");
        Timber.d("Recreated event: " + event.getName());

        Call<ActionResult<Event>> call = service.createEvent(event);
        try {
            Response<ActionResult<Event>> response = call.execute();
            if (response.isSuccessful()) {
                ActionResult<Event> body = response.body();
                if (body != null) {
                    if (body.getData() != null) {
                        Timber.d("Event created on Server Side: " + body.getData().getName());
                        return Result.SUCCESS;
                    } else {
                        Timber.d("Body Data is null");
                        return Result.RESCHEDULE;
                    }
                } else {
                    Timber.d("Response Body is null");
                    return Result.RESCHEDULE;
                }
            } else {
                Timber.d("Unsuccessful response");
                Timber.d(response.errorBody().string());
                return Result.RESCHEDULE;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.RESCHEDULE;
        }
    }
}
