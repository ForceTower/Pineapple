package com.forcetower.uefs.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 17/11/2017.
 */

class SagresProfileCache {
    private static final String CACHED_PROFILE_KEY = "com.forcetower.SagresProfileManager.CachedProfile";
    private static final String SHARED_PREFERENCES_NAME = "com.forcetower.SagresAccessManager.SharedPreferences";

    private final SharedPreferences sharedPreferences;

    SagresProfileCache() {
        sharedPreferences = SagresPortalSDK.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    SagresProfile loadProfile() {
        String jsonString = sharedPreferences.getString(CACHED_PROFILE_KEY, null);
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                return SagresProfile.fromJSONObject(jsonObject);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    void save(SagresProfile profile) {
        /*TODO JSONObject jsonObject = profile.toJSONObject();
        if (jsonObject != null) {
            sharedPreferences
                    .edit()
                    .putString(CACHED_PROFILE_KEY, jsonObject.toString())
                    .apply();
        }*/
    }

    void clear() {
        sharedPreferences.edit().remove(CACHED_PROFILE_KEY).apply();
    }
}
