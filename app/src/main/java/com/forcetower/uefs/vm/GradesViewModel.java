package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.AccessDao;
import com.forcetower.uefs.db.dao.SemesterDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.db_service.entity.Version;
import com.forcetower.uefs.rep.GradesRepository;
import com.forcetower.uefs.rep.LoginRepository;
import com.forcetower.uefs.rep.ServiceRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.util.AbsentLiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 07/03/2018.
 */
public class GradesViewModel extends ViewModel {
    private final SemesterDao semesterDao;
    private final GradesRepository repository;
    private final LoginRepository loginRepository;
    private final ServiceRepository serviceRepository;
    private final AccessDao accessDao;

    private LiveData<List<Semester>> allSemesters;
    private LiveData<List<Discipline>> result;
    private LiveData<Resource<Integer>> allGrades;
    private LiveData<Access> access;
    private LiveData<ApiResponse<Version>> versionData;

    private LiveData<Discipline> disciplineGradesData;

    private boolean allGradesCompleted;
    private boolean allGradesRunning;

    @Inject
    GradesViewModel(SemesterDao semesterDao, GradesRepository repository, AccessDao accessDao,
                    LoginRepository loginRep, ServiceRepository serviceRep) {
        this.semesterDao = semesterDao;
        this.accessDao = accessDao;
        this.repository = repository;
        this.loginRepository = loginRep;
        this.serviceRepository = serviceRep;
    }

    public LiveData<List<Semester>> getAllSemesters() {
        if (allSemesters == null) {
            allSemesters = semesterDao.getAllSemesters();
        }
        return allSemesters;
    }

    public LiveData<List<Discipline>> getGrades(String semester) {
        if (result == null) {
            result = repository.getGrades(semester);
        }
        return result;
    }

    public LiveData<Resource<Integer>> getAllSemestersGrade(boolean start) {
        if (start && !allGradesRunning) {
            allGradesRunning = true;
            if (allGrades == null)
                allGrades = repository.getAllGrades();
            return allGrades;
        } else {
            if (allGrades == null)
                return AbsentLiveData.create();
            return allGrades;
        }
    }

    public LiveData<Access> getAccess() {
        if (access == null) access = accessDao.getAccess();
        return access;
    }

    public LiveData<Resource<Integer>> logout() {
        return loginRepository.logout();
    }

    public void deleteAccess() {
        loginRepository.deleteAccess();
    }

    public void clearAllNotifications() {
        repository.clearAllNotifications();
        loginRepository.deleteAllMessagesNotifications();
    }

    public boolean isAllGradesCompleted() {
        return allGradesCompleted;
    }

    public void setAllGradesCompleted(boolean allGradesCompleted) {
        this.allGradesCompleted = allGradesCompleted;
    }

    public boolean isAllGradesRunning() {
        return allGradesRunning;
    }

    public void setAllGradesRunning(boolean allGradesRunning) {
        this.allGradesRunning = allGradesRunning;
    }

    public LiveData<Discipline> getGradesOfDiscipline(int disciplineId) {
        if (disciplineGradesData == null)
            disciplineGradesData = repository.getGradesFromDiscipline(disciplineId);
        return disciplineGradesData;
    }

    public LiveData<List<Discipline>> requestAllDisciplineGrades() {
        return repository.requestAllGrades();
    }

    public LiveData<ApiResponse<Version>> getUNESLatestVersion() {
        if (versionData == null) versionData = serviceRepository.getUNESVersion();
        return versionData;
    }
}
