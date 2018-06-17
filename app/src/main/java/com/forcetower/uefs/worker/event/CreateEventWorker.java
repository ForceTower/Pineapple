package com.forcetower.uefs.worker.event;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.UNEService;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by João Paulo on 17/06/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CreateEventWorker extends JobService {
    @Inject
    AppDatabase database;
    @Inject
    UNEService service;
    @Inject
    AppExecutors executors;

    @Override
    public void onCreate() {
        super.onCreate();
        ((UEFSApplication) getApplication()).lollipopServiceInjector().inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        String sEvent = params.getExtras().getString("event");
        Event event = new Gson().fromJson(sEvent, Event.class);
        Timber.d("Create Event Worker Invoked");
        Timber.d("Recreated event: " + event.getName());

        executors.networkIO().execute(() -> {
            Call<ActionResult<Event>> call = service.createEvent(event);
            try {
                Response<ActionResult<Event>> response = call.execute();
                if (response.isSuccessful()) {
                    ActionResult<Event> body = response.body();
                    if (body != null) {
                        if (body.getData() != null) {
                            Timber.d("Event created on Server Side: " + body.getData().getName());
                            jobFinished(params, false);
                        } else {
                            Timber.d("Body Data is null");
                            jobFinished(params, true);
                        }
                    } else {
                        Timber.d("Response Body is null");
                        jobFinished(params, true);
                    }
                } else {
                    Timber.d("Unsuccessful response");
                    Timber.d(response.errorBody().string());
                    jobFinished(params, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                jobFinished(params, true);
            }
        });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
