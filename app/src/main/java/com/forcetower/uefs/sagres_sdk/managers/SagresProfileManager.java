package com.forcetower.uefs.sagres_sdk.managers;

import com.forcetower.uefs.sagres_sdk.caching.SagresProfileCache;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresProfileManager {
    private static SagresProfileManager instance;
    private final SagresProfileCache profileCache;
    private SagresProfile currentProfile;

    private SagresProfileManager() {
        profileCache = new SagresProfileCache();
    }

    public static SagresProfileManager getInstance() {
        if (instance == null) {
            synchronized (SagresProfileManager.class) {
                instance = new SagresProfileManager();
            }
        }
        return instance;
    }

    public boolean loadCurrentProfile() {
        SagresProfile profile = profileCache.loadProfile();

        if (profile != null) {
            setCurrentProfile(profile, false);
            return true;
        }
        return false;
    }

    public void setCurrentProfile(SagresProfile profile) {
        setCurrentProfile(profile, true);
    }

    private void setCurrentProfile(SagresProfile profile, boolean saveToCache) {
        this.currentProfile = profile;

        if (saveToCache) {
            if (currentProfile != null) {
                profileCache.save(currentProfile);
            } else {
                profileCache.clear();
            }
        }
    }

    public SagresProfile getCurrentProfile() {
        return currentProfile;
    }
}
