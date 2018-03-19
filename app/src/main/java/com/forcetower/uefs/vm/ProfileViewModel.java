package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.ProfileDao;
import com.forcetower.uefs.db.dao.SemesterDao;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 08/03/2018.
 */
public class ProfileViewModel extends ViewModel {
    private final ProfileDao profileDao;
    private final SemesterDao semesterDao;
    private LiveData<Profile> profileLiveData;
    private LiveData<List<Semester>> semestersLiveData;

    @Inject
    public ProfileViewModel(ProfileDao profileDao, SemesterDao semesterDao) {
        this.profileDao = profileDao;
        this.semesterDao = semesterDao;
    }

    public LiveData<Profile> getProfile() {
        if (profileLiveData == null)
            profileLiveData = profileDao.getProfile();
        return  profileLiveData;
    }

    public LiveData<List<Semester>> getSemesters() {
        if (semestersLiveData == null)
            semestersLiveData = semesterDao.getAllSemesters();
        return semestersLiveData;
    }
}
