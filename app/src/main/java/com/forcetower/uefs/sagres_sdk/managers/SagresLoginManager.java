package com.forcetower.uefs.sagres_sdk.managers;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.exception.SagresLoginException;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;

import org.jetbrains.annotations.NotNull;

/**
 * Created by João Paulo on 18/11/2017.
 */
public class SagresLoginManager {
    public interface SagresLoginCallback {
        void onSuccess();
        void onFailure();
        void onLoginSuccess();
    }
    private static SagresLoginManager instance;

    public static SagresLoginManager getInstance() {
        if (instance == null)
            synchronized (SagresLoginManager.class) {
                if (instance == null)
                    instance = new SagresLoginManager();
            }

        return instance;
    }

    public void login(final String username, final String password, @NotNull final SagresLoginCallback callback) {
        SagresPortalSDK.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final SagresAccess access = createAccess(username, password);
                SagresProfile.fetchProfileForSagresAccess(access, new SagresUtility.InformationFetchWithCacheCallback() {
                    @Override
                    public void onSuccess(SagresProfile profile) {
                        callback.onSuccess();
                        SagresProfile.setCurrentProfile(profile);
                    }

                    @Override
                    public void onFailure(SagresLoginException e) {
                        callback.onFailure();
                    }

                    @Override
                    public void onLoginSuccess() {
                        SagresAccess.setCurrentAccess(access);
                        callback.onLoginSuccess();
                    }
                });
            }
        });
    }

    private SagresAccess createAccess(String username, String password) {
        return new SagresAccess(username, password);
    }
}
