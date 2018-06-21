package com.forcetower.uefs.db_service;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.forcetower.uefs.db_service.dao.AboutFieldDao;
import com.forcetower.uefs.db_service.dao.AccessTokenDao;
import com.forcetower.uefs.db_service.dao.AccountDao;
import com.forcetower.uefs.db_service.dao.CourseDao;
import com.forcetower.uefs.db_service.dao.CreditsMentionDao;
import com.forcetower.uefs.db_service.dao.EventDao;
import com.forcetower.uefs.db_service.dao.MentionDao;
import com.forcetower.uefs.db_service.dao.QuestionAnswerDao;
import com.forcetower.uefs.db_service.entity.AboutField;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.db_service.entity.Account;
import com.forcetower.uefs.db_service.entity.Course;
import com.forcetower.uefs.db_service.entity.CreditsMention;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.db_service.entity.Mention;
import com.forcetower.uefs.db_service.entity.QuestionAnswer;

/**
 * Created by João Paulo on 30/04/2018.
 */
@Database(entities = {
        AccessToken.class,
        Account.class,
        CreditsMention.class,
        Mention.class,
        QuestionAnswer.class,
        AboutField.class,
        Event.class,
        Course.class
}, version = 2)
public abstract class ServiceDatabase extends RoomDatabase {
    public abstract AccessTokenDao accessTokenDao();
    public abstract AccountDao accountDao();
    public abstract MentionDao mentionDao();
    public abstract CreditsMentionDao creditsMentionDao();
    public abstract QuestionAnswerDao questionAnswerDao();
    public abstract AboutFieldDao aboutFieldDao();
    public abstract EventDao eventDao();
    public abstract CourseDao courseDao();
}