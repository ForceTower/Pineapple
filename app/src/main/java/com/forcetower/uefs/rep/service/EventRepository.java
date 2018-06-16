package com.forcetower.uefs.rep.service;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.NetworkBoundResource;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by João Paulo on 15/06/2018.
 */
@Singleton
public class EventRepository {
    private final ServiceDatabase database;
    private final AppDatabase uDatabase;
    private final UNEService service;
    private final AppExecutors executors;

    @Inject
    public EventRepository(ServiceDatabase database, AppDatabase uDatabase, UNEService service, AppExecutors executors) {
        this.database = database;
        this.uDatabase = uDatabase;
        this.service = service;
        this.executors = executors;
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        return new NetworkBoundResource<List<Event>, List<Event>>(executors) {
            @Override
            protected void saveCallResult(@NonNull List<Event> item) {
                database.eventDao().deleteAndInsert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Event> data) {
                return data == null || data.isEmpty() || data.get(0).isOutdated();
            }

            @NonNull
            @Override
            protected LiveData<List<Event>> loadFromDb() {
                return database.eventDao().getAllEvents();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Event>>> createCall() {
                return service.getEvents();
            }
        }.asLiveData();
    }
}
