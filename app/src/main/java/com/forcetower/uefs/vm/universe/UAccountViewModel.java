package com.forcetower.uefs.vm.universe;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.rep.service.AccountRepository;

import javax.inject.Inject;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UAccountViewModel extends ViewModel {
    private final AccountRepository accountRepository;
    private LiveData<AccessToken> accessTokenSrc;

    @Inject
    public UAccountViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public LiveData<AccessToken> getAccessToken() {
        if (accessTokenSrc == null) accessTokenSrc = accountRepository.getCurrentAccesssToken();
        return accessTokenSrc;
    }
}
