package com.forcetower.uefs.rep.service;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.Mention;
import com.forcetower.uefs.db_service.entity.Version;
import com.forcetower.uefs.db_service.helper.CreditAndMentions;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.NetworkBoundResource;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by João Paulo on 01/04/2018.
 */
@Singleton
public class ServiceRepository {
    private final UNEService service;
    private final AppExecutors executors;
    private final ServiceDatabase database;

    @Inject
    public ServiceRepository(UNEService service, AppExecutors executors, ServiceDatabase database) {
        this.service = service;
        this.executors = executors;
        this.database = database;
    }

    public LiveData<ApiResponse<Version>> getUNESVersion() {
        return service.getLatestVersion();
    }

    public LiveData<Resource<List<CreditAndMentions>>> getCredits() {
        return new NetworkBoundResource<List<CreditAndMentions>, List<CreditAndMentions>>(executors) {

            @Override
            protected void saveCallResult(@NonNull List<CreditAndMentions> items) {
                database.creditsMentionDao().deleteAll();
                for (CreditAndMentions item : items) {
                    for (Mention mention : item.getParticipants()) {
                        mention.setCreditId(item.getUid());
                    }
                    database.creditsMentionDao().insertFull(item, item.getParticipants());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<CreditAndMentions> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<CreditAndMentions>> loadFromDb() {
                return database.creditsMentionDao().getCreditsWithMentions();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<CreditAndMentions>>> createCall() {
                return service.getCredits();
            }
        }.asLiveData();
    }
}
