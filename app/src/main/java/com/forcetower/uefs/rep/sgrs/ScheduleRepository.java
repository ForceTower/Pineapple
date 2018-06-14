package com.forcetower.uefs.rep.sgrs;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.WorkerThread;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.CalendarEventDao;
import com.forcetower.uefs.db.dao.DisciplineClassLocationDao;
import com.forcetower.uefs.db.dao.DisciplineGroupDao;
import com.forcetower.uefs.db.dao.SemesterDao;
import com.forcetower.uefs.db.entity.CalendarEvent;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.db.entity.Semester;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */
@Singleton
public class ScheduleRepository {
    private final DisciplineClassLocationDao classLocationDao;
    private final DisciplineGroupDao disciplineGroupDao;
    private final CalendarEventDao calendarEventDao;
    private final AppExecutors executors;
    private final SemesterDao semesterDao;

    private MediatorLiveData<List<DisciplineClassLocation>> schedule;

    @Inject
    ScheduleRepository(AppDatabase database, AppExecutors executors) {
        this.classLocationDao = database.disciplineClassLocationDao();
        this.executors = executors;
        this.semesterDao = database.semesterDao();
        this.disciplineGroupDao = database.disciplineGroupDao();
        this.calendarEventDao = database.calendarEventDao();
        schedule = new MediatorLiveData<>();
    }

    public LiveData<List<DisciplineClassLocation>> getSchedule(String semester) {
        if (semester == null) {
            LiveData<List<Semester>> semesterSrc = semesterDao.getAllSemesters();
            schedule.addSource(semesterSrc, semesters -> {
                schedule.removeSource(semesterSrc);
                Semester smt = Semester.getCurrentSemester(semesters);
                getFromDb(smt.getName());
            });
        } else {
            getFromDb(semester);
        }

        return schedule;
    }

    private void getFromDb(String semester) {
        Timber.d("Getting schedule of semester: %s", semester);
        LiveData<List<DisciplineClassLocation>> dbSource = classLocationDao.getClassesFromSemester(semester);
        schedule.addSource(dbSource, disciplineClassLocations -> {
            schedule.removeSource(dbSource);
            schedule.postValue(disciplineClassLocations);
        });
    }

    public DisciplineGroup getDisciplineGroupDirect(int groupId) {
        return disciplineGroupDao.getDisciplineGroupByIdDirect(groupId);
    }

    public LiveData<DisciplineGroup> getDisciplineWithDetailsLoaded() {
        MediatorLiveData<DisciplineGroup> value = new MediatorLiveData<>();
        LiveData<List<Semester>> semesterSrc = semesterDao.getAllSemesters();
        value.addSource(semesterSrc, semesters -> {
            value.removeSource(semesterSrc);
            Semester smt = Semester.getCurrentSemester(semesters);
            if (smt != null) {
                LiveData<DisciplineGroup> groupData = disciplineGroupDao.getLoadedDisciplineFromSemester(smt.getName());
                value.addSource(groupData, group -> {
                    value.removeSource(groupData);
                    value.postValue(group);
                });
            } else {
                value.postValue(null);
            }
        });

        return value;
    }


    public LiveData<List<CalendarEvent>> getExportedCalendar() {
        return calendarEventDao.getExportedCalendar();
    }

    @WorkerThread
    public void deleteCalendarEvent(CalendarEvent event) {
        calendarEventDao.deleteCalendarEvent(event);
    }

    @WorkerThread
    public void insertCalendarEvent(String calendarId, String eventId, String semester) {
        calendarEventDao.insertCalendarEvent(new CalendarEvent(calendarId, eventId, semester));
    }

    public LiveData<DisciplineClassLocation> triggerLocationLoad() {
        MediatorLiveData<DisciplineClassLocation> locationLoaded = new MediatorLiveData<>();
        LiveData<List<Semester>> semesterSrc = semesterDao.getAllSemesters();
        locationLoaded.addSource(semesterSrc, semesters -> {
            locationLoaded.removeSource(semesterSrc);
            Semester smt = Semester.getCurrentSemester(semesters);
            if (smt != null) {
                LiveData<DisciplineClassLocation> locationSrc = classLocationDao.getOneLoadedLocationFromSemester(smt.getName());
                locationLoaded.addSource(locationSrc, location -> {
                    locationLoaded.removeSource(locationSrc);
                    locationLoaded.postValue(location);
                });
            }
        });
        return locationLoaded;
    }
}
