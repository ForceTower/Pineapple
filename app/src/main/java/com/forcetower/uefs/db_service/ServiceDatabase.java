package com.forcetower.uefs.db_service;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.forcetower.uefs.db_service.dao.AccessTokenDao;
import com.forcetower.uefs.db_service.dao.AccountDao;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.db_service.entity.Account;

/**
 * Created by João Paulo on 30/04/2018.
 */
@Database(entities = {
        AccessToken.class,
        Account.class
}, version = 1)
public abstract class ServiceDatabase extends RoomDatabase {
    public abstract AccessTokenDao accessTokenDao();
    public abstract AccountDao accountDao();
}