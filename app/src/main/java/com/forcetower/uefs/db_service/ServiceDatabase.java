package com.forcetower.uefs.db_service;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.forcetower.uefs.db_service.dao.AccessTokenDao;
import com.forcetower.uefs.db_service.dao.AccountDao;
import com.forcetower.uefs.db_service.dao.CreditsMentionDao;
import com.forcetower.uefs.db_service.dao.MentionDao;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.db_service.entity.Account;
import com.forcetower.uefs.db_service.entity.CreditsMention;
import com.forcetower.uefs.db_service.entity.Mention;

/**
 * Created by João Paulo on 30/04/2018.
 */
@Database(entities = {
        AccessToken.class,
        Account.class,
        CreditsMention.class,
        Mention.class
}, version = 2)
public abstract class ServiceDatabase extends RoomDatabase {
    public abstract AccessTokenDao accessTokenDao();
    public abstract AccountDao accountDao();
    public abstract MentionDao mentionDao();
    public abstract CreditsMentionDao creditsMentionDao();
}