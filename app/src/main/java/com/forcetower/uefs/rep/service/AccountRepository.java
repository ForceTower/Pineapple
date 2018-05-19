package com.forcetower.uefs.rep.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db_service.ServiceDatabase;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.db_service.entity.Account;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.resources.NetworkBoundResource;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.util.BCrypt;
import com.forcetower.uefs.util.ImageUtils;
import com.google.common.hash.Hashing;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by João Paulo on 11/05/2018.
 */
@Singleton
public class AccountRepository {
    private final AppDatabase appDatabase;
    private final ServiceDatabase database;
    private final AppExecutors executors;
    private final UNEService service;
    private final String accountSecret;
    private final String clientSecret;
    private final File cacheDir;

    @Inject
    public AccountRepository(AppDatabase appDatabase, ServiceDatabase database, AppExecutors executors,
                             UNEService service, Context context) {
        this.appDatabase = appDatabase;
        this.database = database;
        this.executors = executors;
        this.service = service;
        this.accountSecret = context.getString(R.string.app_service_account_secret);
        this.clientSecret = context.getString(R.string.app_service_client_secret);
        this.cacheDir = context.getCacheDir();
    }

    public LiveData<AccessToken> getCurrentAccessToken() {
        return database.accessTokenDao().getAccessToken();
    }

    public LiveData<Resource<Account>> createAccount() {
        MediatorLiveData<Resource<Account>> createAccountFinal = new MediatorLiveData<>();
        LiveData<Bitmap> bitmapSrc = ImageUtils.getImageBitmap(new File(cacheDir, "profile_image.jpg"), executors);
        createAccountFinal.addSource(bitmapSrc, bitmap -> {
            createAccountFinal.removeSource(bitmapSrc);
            if (bitmap == null) {
                LiveData<Resource<Account>> createAccount = onCreateAccountNext(null);
                createAccountFinal.addSource(createAccount, createAccountFinal::postValue);
            } else {
                LiveData<String> encoderImageSrc = ImageUtils.encodeImage(bitmap, executors);
                createAccountFinal.addSource(encoderImageSrc, encodedImage -> {
                    LiveData<Resource<Account>> createAccount = onCreateAccountNext(encodedImage);
                    createAccountFinal.addSource(createAccount, createAccountFinal::postValue);
                });
            }
        });

        return createAccountFinal;
    }

    private LiveData<Resource<Account>> onCreateAccountNext(String image) {
        return new NetworkBoundResource<Account, ActionResult<Account>> (executors) {
            private String name;
            private String username;
            private String passwordCrypt;
            private String imageSend;

            @Override
            protected boolean preExecute() {
                Access access = appDatabase.accessDao().getAccessDirect();
                Profile profile = appDatabase.profileDao().getProfileDirect();

                if (access == null) return false;

                name = profile != null ? profile.getName() : "Undefined";
                username = access.getUsername();
                passwordCrypt = Hashing.sha256().hashString(access.getPassword(), Charset.forName("UTF-8")).toString();
                imageSend = image == null ? "no_image" : image;

                return true;
            }

            @Override
            protected void saveCallResult(@NonNull ActionResult<Account> item) {
                if (item.getData() == null) {
                    Timber.e("Error, item.getData() is null");
                    return;
                }

                item.getData().setInsertedAt(System.currentTimeMillis()/1000);
                database.accountDao().insert(item.getData());
            }

            @Override
            protected boolean shouldFetch(@Nullable Account data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Account> loadFromDb() {
                return database.accountDao().getAccount(username);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ActionResult<Account>>> createCall() {
                return service.createAccount(name, username, passwordCrypt, imageSend, accountSecret);
            }
        }.asLiveData();
    }

    public MediatorLiveData<Resource<AccessToken>> login() {
        MediatorLiveData<Resource<AccessToken>> loginFinalSrc = new MediatorLiveData<>();
        LiveData<Access> accessSrc = appDatabase.accessDao().getAccess();
        loginFinalSrc.addSource(accessSrc, access -> {
            if (access == null) {
                loginFinalSrc.postValue(Resource.error("Not connected to sagres", 401, (Throwable)null));
                return;
            }

            String username = access.getUsername();
            String password = access.getPassword();

            LiveData<Resource<AccessToken>> loginSrc = login(username, password);
            loginFinalSrc.addSource(loginSrc, loginFinalSrc::setValue);
        });

        return loginFinalSrc;
    }

    public LiveData<Resource<AccessToken>> login(String username, String password) {
        return new NetworkBoundResource<AccessToken, AccessToken>(executors) {
            private String passwordCrypt;

            @Override
            protected boolean preExecute() {
                this.passwordCrypt = Hashing.sha256().hashString(password, Charset.forName("UTF-8")).toString();
                Timber.d("PW Crypt: %s", passwordCrypt);
                return true;
            }

            @Override
            protected void saveCallResult(@NonNull AccessToken item) {
                item.setCreatedAt(System.currentTimeMillis()/1000);
                database.accessTokenDao().insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable AccessToken data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<AccessToken> loadFromDb() {
                return database.accessTokenDao().getAccessToken();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<AccessToken>> createCall() {
                return service.login("password", username, passwordCrypt, 2, clientSecret, "*");
            }
        }.asLiveData();
    }

    public LiveData<String> setUserToken() {
        MutableLiveData<String> data = new MutableLiveData<>();
        executors.diskIO().execute(() -> {
            try {
                Access a = appDatabase.accessDao().getAccessDirect();
                Profile p = appDatabase.profileDao().getProfileDirect();
                if (a == null) return;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("firebase_tokens");
                String token = FirebaseInstanceId.getInstance().getToken();
                if (token == null) {
                    data.postValue("Null Firebase Token");
                } else {
                    reference.child(a.getUsername()).child("token").setValue(token);
                    reference.child(a.getUsername()).child("device").setValue(Build.MANUFACTURER + " " + Build.MODEL);
                    reference.child(a.getUsername()).child("android").setValue(Build.VERSION.SDK_INT);
                    reference.child(a.getUsername()).child("name").setValue(p != null ? p.getName() : "Null Profile");
                    data.postValue("Completed");
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        });
        return data;
    }

    public LiveData<String> setUserBetaInformation(String version) {
        MutableLiveData<String> data = new MutableLiveData<>();
        executors.diskIO().execute(() -> {
            try {
                Access a = appDatabase.accessDao().getAccessDirect();
                Profile p = appDatabase.profileDao().getProfileDirect();
                if (a == null) return;

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("beta_users");
                String token = FirebaseInstanceId.getInstance().getToken();

                String identifier = a.getUsername().toLowerCase();
                reference.child(identifier).child("version").setValue(version);
                reference.child(identifier).child("token").setValue(token);
                reference.child(identifier).child("name").setValue(p != null ? p.getName() : "invalid");
                reference.child(identifier).child("last_sync_att").setValue(p != null ? p.getLastSyncAttempt() : 0);
                reference.child(identifier).child("device").setValue(Build.MANUFACTURER + " " + Build.MODEL);
                reference.child(identifier).child("android").setValue(Build.VERSION.SDK_INT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return data;
    }
}
