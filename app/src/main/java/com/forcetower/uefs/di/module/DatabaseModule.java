package com.forcetower.uefs.di.module;

import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.AccessDao;
import com.forcetower.uefs.db.dao.CalendarItemDao;
import com.forcetower.uefs.db.dao.DisciplineClassItemDao;
import com.forcetower.uefs.db.dao.DisciplineClassLocationDao;
import com.forcetower.uefs.db.dao.DisciplineDao;
import com.forcetower.uefs.db.dao.DisciplineGroupDao;
import com.forcetower.uefs.db.dao.GradeDao;
import com.forcetower.uefs.db.dao.GradeInfoDao;
import com.forcetower.uefs.db.dao.GradeSectionDao;
import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.dao.ProfileDao;
import com.forcetower.uefs.db.dao.SemesterDao;
import com.forcetower.uefs.db.dao.TodoItemDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by João Paulo on 07/03/2018.
 */
@Module
public class DatabaseModule {

    @Singleton
    @Provides
    AccessDao provideAccessDao(AppDatabase database) {
        return database.accessDao();
    }

    @Singleton
    @Provides
    CalendarItemDao provideCalendarItemDao(AppDatabase database) {
        return database.calendarItemDao();
    }

    @Singleton
    @Provides
    DisciplineClassItemDao provideDisciplineClassItemDao(AppDatabase database) {
        return database.disciplineClassItemDao();
    }

    @Singleton
    @Provides
    DisciplineClassLocationDao provideDisciplineLocationDao(AppDatabase database) {
        return database.disciplineClassLocationDao();
    }

    @Provides
    @Singleton
    DisciplineDao provideDisciplineDao(AppDatabase database) {
        return database.disciplineDao();
    }

    @Singleton
    @Provides
    DisciplineGroupDao provideDisciplineGroupDao(AppDatabase database) {
        return database.disciplineGroupDao();
    }

    @Singleton
    @Provides
    GradeDao provideGradeDao(AppDatabase database) {
        return database.gradeDao();
    }

    @Singleton
    @Provides
    GradeInfoDao provideGradeInfoDao(AppDatabase database) {
        return database.gradeInfoDao();
    }

    @Singleton
    @Provides
    GradeSectionDao provideGradeSectionDao(AppDatabase database) {
        return database.gradeSectionDao();
    }

    @Provides
    @Singleton
    MessageDao provideMessageDao(AppDatabase database) {
        return database.messageDao();
    }

    @Singleton
    @Provides
    ProfileDao provideProfileDao(AppDatabase database) {
        return database.profileDao();
    }

    @Singleton
    @Provides
    SemesterDao provideSemesterDao(AppDatabase database) {
        return database.semesterDao();
    }

    @Singleton
    @Provides
    TodoItemDao provideTodoItemDao(AppDatabase database) {
        return database.todoItemDao();
    }

}
