package com.forcetower.uefs.sagres_sdk.caching;

import android.util.Log;

import com.forcetower.uefs.content.ObscuredSharedPreferences;
import com.forcetower.uefs.helpers.PrefUtils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresProfileCache {
    private static final String CACHED_PROFILE_KEY = "com.forcetower.SagresProfileManager.CachedProfile";

    private final ObscuredSharedPreferences sharedPreferences;

    public SagresProfileCache() {
        sharedPreferences = PrefUtils.getPrefs(SagresPortalSDK.getApplicationContext());
    }

    public SagresProfile loadProfile() {
        String jsonString = sharedPreferences.getString(CACHED_PROFILE_KEY, null);
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                return SagresProfile.fromJSONObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void save(SagresProfile profile) {
        profile.setClasses(removeDuplicated(profile.getClasses()));
        try {
            JSONObject jsonObject = profile.toJSONObject();
            if (jsonObject != null) {
                sharedPreferences.edit().putString(CACHED_PROFILE_KEY, jsonObject.toString()).apply();
            }
        } catch (JSONException e) {
            Log.e(SagresPortalSDK.SAGRES_SDK_TAG, "Attempt to save Sagres Profile failed");
            e.printStackTrace();
        }
    }

    private HashMap<String, List<SagresClassDay>> removeDuplicated(HashMap<String, List<SagresClassDay>> classes) {
        HashMap<String, List<SagresClassDay>> hash = new HashMap<>();

        for (Map.Entry<String, List<SagresClassDay>> entry : classes.entrySet()) {
            List<SagresClassDay> classesDay = entry.getValue();
            List<SagresClassDay> clean = new ArrayList<>();

            for (SagresClassDay classDay : classesDay) {
                if (!clean.contains(classDay)) {
                    clean.add(classDay);
                }
            }

            hash.put(entry.getKey(), clean);
        }
        return hash;
    }

    public void clear() {
        sharedPreferences.edit().remove(CACHED_PROFILE_KEY).apply();
    }
}
