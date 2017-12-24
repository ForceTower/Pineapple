package com.forcetower.uefs.sagres_sdk.managers;

import android.support.annotation.NonNull;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.exception.SagresLoginException;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;

/**
 * Created by João Paulo on 18/11/2017.
 */
public class SagresLoginManager {
    private static SagresLoginManager instance;

    public static SagresLoginManager getInstance() {
        if (instance == null)
            synchronized (SagresLoginManager.class) {
                if (instance == null)
                    instance = new SagresLoginManager();
            }

        return instance;
    }

    public void login(final String username, final String password, @NonNull final SagresLoginCallback callback) {
        SagresPortalSDK.getExecutor().execute(() -> {
            final SagresAccess access = createAccess(username, password);
            SagresProfile.fetchProfileForSagresAccess(access, new SagresUtility.AllInformationFetchWithCacheCallback() {
                @Override
                public void onSuccess(SagresProfile profile) {
                    SagresProfile.setCurrentProfile(profile);
                    callback.onSuccess();
                }

                @Override
                public void onFailure(SagresLoginException e) {
                    if (e.failedConnection())
                        callback.onFailure(false);
                    else
                        callback.onFailure(true);
                }

                @Override
                public void onLoginSuccess() {
                    SagresAccess.setCurrentAccess(access);
                    callback.onLoginSuccess();
                }
            });
        });
    }

    private SagresAccess createAccess(String username, String password) {
        return new SagresAccess(username, password);
    }

    public interface SagresLoginCallback {
        void onSuccess();

        void onFailure(boolean failedConnection);

        void onLoginSuccess();
    }
}
