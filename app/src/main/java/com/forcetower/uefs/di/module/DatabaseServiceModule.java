package com.forcetower.uefs.di.module;

import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.dao.AccessTokenDao;
import com.forcetower.uefs.db_service.dao.AccountDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by João Paulo on 08/05/2018.
 */
@Module
public class DatabaseServiceModule {

    @Provides
    @Singleton
    AccessTokenDao provideAccessTokenDao(ServiceDatabase database) {
        return database.accessTokenDao();
    }

    @Provides
    @Singleton
    AccountDao provideAccountDao(ServiceDatabase database) {
        return database.accountDao();
    }
}
