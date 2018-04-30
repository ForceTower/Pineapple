package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.AccessDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.rep.sgrs.LoginRepository;
import com.forcetower.uefs.rep.helper.Resource;

import javax.inject.Inject;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class LoginViewModel extends ViewModel {
    private final LoginRepository repository;
    private final AccessDao accessDao;

    private LiveData<Resource<Integer>> login;
    private MediatorLiveData<Resource<Integer>> loginMediator;

    private boolean activityStarted = false;

    @Inject
    public LoginViewModel(LoginRepository repository, AccessDao accessDao) {
        this.repository = repository;
        this.accessDao = accessDao;
        this.loginMediator = new MediatorLiveData<>();
    }

    public LiveData<Resource<Integer>> getLogin(String username, String password) {
        if (login != null) loginMediator.removeSource(login);
        login = repository.login(username, password);
        loginMediator.addSource(login, resource -> loginMediator.setValue(resource));
        return loginMediator;
    }

    public LiveData<Access> getAccess() {
        return accessDao.getAccess();
    }

    public LiveData<Resource<Integer>> getLogin() {
        return loginMediator;
    }

    public boolean isActivityStarted() {
        return activityStarted;
    }

    public void setActivityStarted(boolean activityStarted) {
        this.activityStarted = activityStarted;
    }

    public void deleteDatabase() {
        repository.deleteDatabase();
    }
}
