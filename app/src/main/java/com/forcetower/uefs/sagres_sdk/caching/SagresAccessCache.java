package com.forcetower.uefs.sagres_sdk.caching;

import android.util.Log;

import com.forcetower.uefs.content.ObscuredSharedPreferences;
import com.forcetower.uefs.helpers.PrefUtils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresAccessCache {
    private static final String CACHED_ACCESS_TOKEN_KEY = "com.forcetower.SagresAccessCache.CachedCredentials";
    private final ObscuredSharedPreferences sharedPreferences;


    public SagresAccessCache(ObscuredSharedPreferences preferences) {
        this.sharedPreferences = preferences;
    }

    public SagresAccessCache() {
        this(PrefUtils.getPrefs(SagresPortalSDK.getApplicationContext()));
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

    public void clear() {
        sharedPreferences.edit().remove(CACHED_ACCESS_TOKEN_KEY).apply();
    }

    public void save(SagresAccess access) {
        JSONObject jsonObject;
        try {
            jsonObject = access.toJSONObject();
            sharedPreferences.edit().putString(CACHED_ACCESS_TOKEN_KEY, jsonObject.toString()).apply();
        } catch (JSONException e) {
            Log.e(SagresPortalSDK.SAGRES_SDK_TAG, "Attempt to save Sagres Access has failed");
            e.printStackTrace();
        }
    }
}
