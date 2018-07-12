package com.forcetower.uefs.rep.service;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.db_service.helper.ImGurDataObject;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.NetworkBoundResource;
import com.forcetower.uefs.rep.resources.UploadToImGurResource;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.forcetower.uefs.rep.helper.RequestCreator.makeFormBodyForImGurImageUpload;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestForImGurImageUpload;

/**
 * Created by João Paulo on 15/06/2018.
 */
@Singleton
public class EventRepository {
    private final ServiceDatabase database;
    private final AppDatabase uDatabase;
    private final UNEService service;
    private final AppExecutors executors;
    private final OkHttpClient client;
    private final String imGurAlbum;
    private final String imGurSecret;

    @Inject
    public EventRepository(ServiceDatabase database, AppDatabase uDatabase, UNEService service,
                           AppExecutors executors, OkHttpClient client, Context context) {
        this.database = database;
        this.uDatabase = uDatabase;
        this.service = service;
        this.executors = executors;
        this.client = client;
        this.imGurAlbum = context.getString(R.string.imgur_service_album);
        this.imGurSecret = context.getString(R.string.imgur_service_secret);
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

    public LiveData<Resource<ImGurDataObject>> uploadImageToImGur(Bitmap bitmap, String name) {
        return new UploadToImGurResource(executors, bitmap) {

            @Override
            protected Call createCall(String base64) {
                FormBody.Builder builder = makeFormBodyForImGurImageUpload(base64, imGurAlbum, name);
                Request request = makeRequestForImGurImageUpload(builder.build(), imGurSecret);
                return client.newCall(request);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Event>>> getUnapprovedEvents() {
        return new NetworkBoundResource<List<Event>, List<Event>>(executors) {
            @Override
            protected void saveCallResult(@NonNull List<Event> item) {
                database.eventDao().deleteAndInsertUnapproved(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Event> data) {
                return data == null || data.isEmpty() || data.get(0).isOutdated();
            }

            @NonNull
            @Override
            protected LiveData<List<Event>> loadFromDb() {
                return database.eventDao().getAllUnapprovedEvents();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Event>>> createCall() {
                return service.getUnapprovedEvents();
            }
        }.asLiveData();
    }

    public LiveData<Resource<Event>> getEvent(String uuid) {
        return new NetworkBoundResource<Event, List<Event>>(executors) {
            @Override
            protected void saveCallResult(@NonNull List<Event> item) {
                database.eventDao().deleteAndInsert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Event data) {
                return data == null || data.isOutdated();
            }

            @NonNull
            @Override
            protected LiveData<Event> loadFromDb() {
                return database.eventDao().getEvent(uuid);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Event>>> createCall() {
                return service.getEvents();
            }
        }.asLiveData();
    }
}
