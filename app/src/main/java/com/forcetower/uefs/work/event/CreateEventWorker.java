package com.forcetower.uefs.work.event;

import android.support.annotation.NonNull;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.UNEService;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class CreateEventWorker extends Worker {
    public static void invokeWorker(@NonNull Event event) {
        String string = new Gson().toJson(event);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putString("event", string)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(CreateEventWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(Constants.WORKER_CREATE_EVENT)
                .build();

        WorkManager.getInstance().enqueue(request);
    }

    @Inject
    UNEService service;

    @NonNull
    @Override
    public Result doWork() {
        ((UEFSApplication)getApplicationContext()).getAppComponent().inject(this);
        String sEvent = getInputData().getString("event");
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
                        return Result.RETRY;
                    }
                } else {
                    Timber.d("Response Body is null");
                    return Result.RETRY;
                }
            } else {
                Timber.d("Unsuccessful response");
                Timber.d(response.errorBody().string());
                return Result.RETRY;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.RETRY;
        }
    }
}
