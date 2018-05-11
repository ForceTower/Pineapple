package com.forcetower.uefs.rep.service;

import android.arch.lifecycle.LiveData;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.AccessToken;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by João Paulo on 11/05/2018.
 */
@Singleton
public class AccountRepository {
    private final ServiceDatabase database;
    private final AppExecutors executors;

    @Inject
    public AccountRepository(ServiceDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    public LiveData<AccessToken> getCurrentAccesssToken() {
        return database.accessTokenDao().getAccessToken();
    }
}
