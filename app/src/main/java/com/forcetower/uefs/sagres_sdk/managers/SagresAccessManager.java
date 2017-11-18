package com.forcetower.uefs.sagres_sdk.managers;

import com.forcetower.uefs.sagres_sdk.caching.SagresAccessCache;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresAccessManager {
    public static final String SHARED_PREFERENCES_NAME = "com.forcetower.SagresAccessManager.SharedPreferences";
    private static SagresAccessManager instance;
    private final SagresAccessCache credentialsCache;
    private SagresAccess currentAccessCredentials;

    private SagresAccessManager() {
        credentialsCache = new SagresAccessCache();
    }

    public static SagresAccessManager getInstance() {
        if (instance == null) {
            synchronized (SagresAccessManager.class) {
                instance = new SagresAccessManager();
            }
        }
        return instance;
    }

    public boolean loadCurrentAccess() {
        SagresAccess credentials = credentialsCache.loadCredentials();
        if (credentials != null) {
            setCurrentCredentials(credentials, false);
            return true;
        }
        return false;
    }

    public void setCurrentCredentials(SagresAccess credentials) {
        setCurrentCredentials(credentials, true);
    }

    private void setCurrentCredentials(SagresAccess credentials, boolean saveToCache) {
        currentAccessCredentials = credentials;
        if (saveToCache) {
            if (currentAccessCredentials != null) {
                credentialsCache.save(currentAccessCredentials);
            } else {
                credentialsCache.clear();
            }
        }
    }

    public SagresAccess getCurrentAccessCredentials() {
        return currentAccessCredentials;
    }


}
