package com.forcetower.uefs.rep.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.db_service.entity.Account;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.NetworkBoundResource;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.util.ImageUtils;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by João Paulo on 30/04/2018.
 */
@Singleton
public class ServiceAccountRepository {
    private final UNEService service;
    private final AppExecutors executors;
    private final ServiceDatabase sDatabase;
    private final String clientSecret;

    @Inject
    public ServiceAccountRepository(UNEService service, ServiceDatabase sDatabase, AppExecutors executors, Context context) {
        this.service = service;
        this.sDatabase = sDatabase;
        this.executors = executors;
        this.clientSecret = context.getString(R.string.unes_service_client_secret);
    }

    public LiveData<Resource<AccessToken>> login(String username, String password) {
        return new NetworkBoundResource<AccessToken, AccessToken>(executors) {

            @Override
            protected void saveCallResult(@NonNull AccessToken item) {
                long time = System.currentTimeMillis()/1000;
                item.setCreatedAt(time);
                sDatabase.accessTokenDao().insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable AccessToken data) {
                return data == null || !data.isValid() || data.isExpired();
            }

            @NonNull
            @Override
            protected LiveData<AccessToken> loadFromDb() {
                return sDatabase.accessTokenDao().getAccessToken();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<AccessToken>> createCall() {
                String hash = Hashing.sha256().hashString(password, Charset.forName("UTF-8")).toString();
                return service.login("password", username, hash, 2, clientSecret, "*");
            }
        }.asLiveData();
    }


    public LiveData<Resource<Account>> createAccount(String name, String username, String password, String image) {
        return new NetworkBoundResource<Account, ActionResult<Account>>(executors) {

            @Override
            protected void saveCallResult(@NonNull ActionResult<Account> item) {
                if (item.getData() == null) {
                    Timber.e("Error, item.getData() is null");
                    return;
                }

                item.getData().setInsertedAt(System.currentTimeMillis()/1000);
                sDatabase.accountDao().insert(item.getData());
            }

            @Override
            protected boolean shouldFetch(@Nullable Account data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Account> loadFromDb() {
                return sDatabase.accountDao().getAccount(username);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ActionResult<Account>>> createCall() {
                String hash = Hashing.sha256().hashString(password, Charset.forName("UTF-8")).toString();
                return service.createAccount(name, username, hash, image);
            }
        }.asLiveData();
    }
}
