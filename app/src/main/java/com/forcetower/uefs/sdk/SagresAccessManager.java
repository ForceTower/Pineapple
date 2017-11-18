package com.forcetower.uefs.sdk;

/**
 * Created by João Paulo on 17/11/2017.
 */

class SagresAccessManager {
    public static final String SHARED_PREFERENCES_NAME = "com.forcetower.SagresAccessManager.SharedPreferences";
    private static SagresAccessManager instance;
    private final SagresAccessCache credentialsCache;
    private SagresAccess currentAccessCredentials;

    private SagresAccessManager() {
        credentialsCache = new SagresAccessCache();
    }

    static SagresAccessManager getInstance() {
        if (instance == null) {
            synchronized (SagresAccessManager.class) {
                instance = new SagresAccessManager();
            }
        }
        return instance;
    }

    boolean loadCurrentAccess() {
        SagresAccess credentials = credentialsCache.loadCredentials();
        if (credentials != null) {
            setCurrentCredentials(credentials, false);
            return true;
        }
        return false;
    }

    void setCurrentCredentials(SagresAccess credentials) {
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

    SagresAccess getCurrentAccessCredentials() {
        return currentAccessCredentials;
    }


}
