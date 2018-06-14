package com.forcetower.uefs.vm.universe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.db_service.entity.Account;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.service.AccountRepository;

import javax.inject.Inject;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UAccountViewModel extends ViewModel {
    private final AccountRepository accountRepository;
    private LiveData<AccessToken> accessTokenSrc;
    private LiveData<Resource<Account>> createAccountSrc;
    private final MediatorLiveData<Resource<AccessToken>> loginNoCredentialSrc;
    private final MediatorLiveData<Resource<AccessToken>> loginCredentialsSrc;

    @Inject
    public UAccountViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        loginCredentialsSrc = new MediatorLiveData<>();
        loginNoCredentialSrc = new MediatorLiveData<>();
    }

    public LiveData<AccessToken> getAccessToken() {
        if (accessTokenSrc == null) accessTokenSrc = accountRepository.getCurrentAccessToken();
        return accessTokenSrc;
    }

    public LiveData<Resource<Account>> getCreateAccountSrc() {
        if (createAccountSrc == null) createAccountSrc = accountRepository.createAccount();
        return createAccountSrc;
    }

    public LiveData<Resource<AccessToken>> login(boolean start) {
        if (!start) return loginNoCredentialSrc;

        LiveData<Resource<AccessToken>> loginSrc = accountRepository.login();
        loginNoCredentialSrc.addSource(loginSrc, tokenResource -> {
            if (tokenResource == null) return;

            if (tokenResource.status == Status.ERROR || tokenResource.status == Status.SUCCESS) {
                loginNoCredentialSrc.removeSource(loginSrc);
            }

            loginNoCredentialSrc.setValue(tokenResource);
        });
        return loginNoCredentialSrc;
    }

    public LiveData<Resource<AccessToken>> getLoginWith() {
        return loginCredentialsSrc;
    }

    public void loginWith(String username, String password) {
        LiveData<Resource<AccessToken>> loginSrc = accountRepository.login(username, password);
        loginCredentialsSrc.addSource(loginSrc, tokenResource -> {
            if (tokenResource == null) return;

            if (tokenResource.status == Status.ERROR || tokenResource.status == Status.SUCCESS) {
                loginCredentialsSrc.removeSource(loginSrc);
            }

            loginCredentialsSrc.setValue(tokenResource);
        });
    }

    public LiveData<String> setUserToken() {
        return accountRepository.setUserToken();
    }

    public LiveData<String> setUserBetaInformation(String version) {
        return accountRepository.setUserBetaInformation(version);
    }
}
