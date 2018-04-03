package com.forcetower.uefs.rep;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.DisciplineClassLocationDao;
import com.forcetower.uefs.db.dao.DisciplineGroupDao;
import com.forcetower.uefs.db.dao.SemesterDao;
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
    private final AppExecutors executors;
    private final SemesterDao semesterDao;

    private MediatorLiveData<List<DisciplineClassLocation>> schedule;

    @Inject
    ScheduleRepository(AppDatabase database, AppExecutors executors) {
        this.classLocationDao = database.disciplineClassLocationDao();
        this.executors = executors;
        this.semesterDao = database.semesterDao();
        this.disciplineGroupDao = database.disciplineGroupDao();
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
            //noinspection ConstantConditions
            if (disciplineClassLocations.isEmpty()) {
                Timber.d("Schedule is empty... Code for retry at every attempt was removed");
                /*
                executors.diskIO().execute(() -> {
                    Access access = accessDao.getAccessDirect();
                    LiveData<Resource<Integer>> loginRes = loginRepository.login(access.getUsername(), access.getPassword());
                    schedule.addSource(loginRes, integerResource -> {
                        //noinspection ConstantConditions
                        if (integerResource.status == Status.SUCCESS) {
                            schedule.removeSource(loginRes);
                            schedule.addSource(dbSource, finalLocations -> schedule.postValue(finalLocations));
                        } else if (integerResource.status == Status.ERROR) {
                            int code = integerResource.code;
                            if (code == 401) {
                                Timber.d("User disconnected");
                                //Disconnects the user because validation failed
                                executors.diskIO().execute(accessDao::deleteAllAccesses);
                            }
                        }
                    });
                });
                */
            }
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
}
