package com.forcetower.uefs.dependency_injection.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.forcetower.uefs.database.AppDatabase;
import com.forcetower.uefs.database.dao.AAccessDao;
import com.forcetower.uefs.database.repository.AccessRepository;
import com.forcetower.uefs.database.repository.CalendarRepository;
import com.forcetower.uefs.database.repository.DisciplineClassItemRepository;
import com.forcetower.uefs.database.repository.DisciplineClassLocationRepository;
import com.forcetower.uefs.database.repository.DisciplineGroupRepository;
import com.forcetower.uefs.database.repository.DisciplineRepository;
import com.forcetower.uefs.database.repository.GradeInfoRepository;
import com.forcetower.uefs.database.repository.GradeRepository;
import com.forcetower.uefs.database.repository.GradeSectionRepository;
import com.forcetower.uefs.database.repository.ScrapRepository;
import com.forcetower.uefs.database.repository.SemesterRepository;
import com.forcetower.uefs.database.repository.TodoItemRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Module
public class RoomModule {
    private final AppDatabase database;

    public RoomModule(Application application) {
        database = Room.databaseBuilder(application, AppDatabase.class, "room_database.db").build();
    }

    @Provides
    @Singleton
    AppDatabase provideDatabase() {
        return database;
    }

    @Provides
    AAccessDao provideAccessDao() {
        return database.aAccessDao();
    }

    @Provides
    @Singleton
    AccessRepository provideAccessRep() {
        return new AccessRepository(database.aAccessDao());
    }

    @Provides
    @Singleton
    CalendarRepository provideCalendarRep() {
        return new CalendarRepository(database.aCalendarItemDao());
    }

    @Provides
    @Singleton
    DisciplineClassItemRepository provideDisciplineClassItemRep() {
        return new DisciplineClassItemRepository(database.aDisciplineClassItemDao());
    }

    @Provides
    @Singleton
    DisciplineClassLocationRepository provideDisciplineClassLocationRep() {
        return new DisciplineClassLocationRepository(database.aDisciplineClassLocationDao());
    }

    @Provides
    @Singleton
    DisciplineGroupRepository provideDisciplineGroupRep() {
        return new DisciplineGroupRepository(database.aDisciplineGroupDao());
    }

    @Provides
    @Singleton
    DisciplineRepository provideDisciplineRep() {
        return new DisciplineRepository(database.aDisciplineDao());
    }

    @Provides
    @Singleton
    GradeInfoRepository provideGradeInfoRep() {
        return new GradeInfoRepository(database.aGradeInfoDao());
    }

    @Provides
    @Singleton
    GradeRepository provideGradeRep() {
        return new GradeRepository(database.aGradeDao());
    }

    @Provides
    @Singleton
    GradeSectionRepository provideGradeSectionRep() {
        return new GradeSectionRepository(database.aGradeSectionDao());
    }

    @Provides
    @Singleton
    ScrapRepository provideScrapRep() {
        return new ScrapRepository(database.aScrapDao());
    }

    @Provides
    @Singleton
    SemesterRepository provideSemesterRep() {
        return new SemesterRepository(database.aSemesterDao());
    }

    @Provides
    @Singleton
    TodoItemRepository provideTodoItemRep() {
        return new TodoItemRepository(database.aTodoItemDao());
    }
}
