package com.forcetower.uefs.vm.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.db_service.entity.Account;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.service.ServiceAccountRepository;
import com.forcetower.uefs.util.AbsentLiveData;
import com.forcetower.uefs.util.ImageUtils;

import javax.inject.Inject;

/**
 * Created by João Paulo on 30/04/2018.
 */
public class AccountViewModel extends ViewModel {
    private final ServiceAccountRepository repository;
    private LiveData<Resource<Account>> createAccountSilent;
    private MediatorLiveData<Resource<AccessToken>> loginSilent;
    private LiveData<Resource<AccessToken>> tokenSource;

    @Inject
    public AccountViewModel(ServiceAccountRepository repository) {
        this.repository = repository;

        this.createAccountSilent = AbsentLiveData.create();
        this.loginSilent = new MediatorLiveData<>();
    }

    public LiveData<Resource<AccessToken>> getAccessToken(boolean enforce) {
        if (enforce || tokenSource == null) {
            if (tokenSource != null) loginSilent.removeSource(tokenSource);
            tokenSource = repository.login();
            loginSilent.addSource(tokenSource, dataSnapshot -> loginSilent.postValue(dataSnapshot));
        }
        return loginSilent;
    }

    public LiveData<Resource<Account>> createAccountSilently(String image) {
        return repository.createAccount(image);
    }

    public LiveData<Resource<Account>> getCreateAccountSilent() {
        return createAccountSilent;
    }

    public LiveData<Resource<AccessToken>> getLoginSilent() {
        return loginSilent;
    }

    public LiveData<String> encodeBitmap(Bitmap bitmap) {
        return repository.encodeBitmap(bitmap);
    }
}
