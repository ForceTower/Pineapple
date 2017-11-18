package com.forcetower.uefs.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 17/11/2017.
 */

class SagresAccessCache {
    private static final String CACHED_ACCESS_TOKEN_KEY = "com.forcetower.SagresAccessCache.CachedCredentials";
    private final SharedPreferences sharedPreferences;


    SagresAccessCache(SharedPreferences preferences) {
        this.sharedPreferences = preferences;
    }

    SagresAccessCache() {
        this(SagresPortalSDK.getApplicationContext().getSharedPreferences(SagresAccessManager.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE));
    }

    public SagresAccess loadCredentials() {
        SagresAccess access = null;

        if (hasCachedAccess()) {
            access = getCachedAccess();
        }

        return access;
    }

    private boolean hasCachedAccess() {
        return sharedPreferences.contains(CACHED_ACCESS_TOKEN_KEY);
    }

    private SagresAccess getCachedAccess() {
        String jsonString = sharedPreferences.getString(CACHED_ACCESS_TOKEN_KEY, null);
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                return SagresAccess.createFromJSONObject(jsonObject);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    void clear() {
        sharedPreferences.edit().remove(CACHED_ACCESS_TOKEN_KEY).apply();
    }

    public void save(SagresAccess access) {
        /*TODO JSONObject jsonObject = null;
        try {
            jsonObject = access.toJSONObject();
            sharedPreferences.edit().putString(CACHED_ACCESS_TOKEN_KEY, jsonObject.toString())
                    .apply();
        } catch (JSONException e) {
            // Can't recover
        }*/
    }
}
