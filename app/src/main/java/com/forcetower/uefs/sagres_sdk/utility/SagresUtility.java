package com.forcetower.uefs.sagres_sdk.utility;

import android.util.Log;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.parsers.SagresMessagesParser;
import com.forcetower.uefs.sagres_sdk.parsers.SagresParser;
import com.forcetower.uefs.sagres_sdk.parsers.SagresScheduleParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 18/11/2017.
 */
public class SagresUtility {
    public interface InformationFetchWithCacheCallback {
        void onSuccess(SagresProfile profile);
        void onFailure(Exception e);
    }

    public static void getInformationFromUserWithCacheAsync(SagresAccess access, InformationFetchWithCacheCallback callback) {
        String username = access.getUsername();
        String password = access.getPassword();

        if (!SagresPortalSDK.isSdkInitialized()) {
            if (callback != null) {
                callback.onFailure(new Exception("Sagres SDK is not initialized"));
            }
            return;
        }

        if (username == null || password == null) {
            if (callback != null) {
                callback.onFailure(new Exception("Fields are null"));
            }
            return;
        }

        try {
            JSONObject loginResponse = SagresConnector.login(username, password);
            if (loginResponse.has("error")) {
                Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Login has error");
                if (callback != null) {
                    callback.onFailure(new Exception("Login has error - Timeout[Prob]"));
                }
                return;
            }

            String html = loginResponse.getString("html");
            html = SagresParser.changeCharset(html);

            boolean connected = SagresParser.connected(html);
            if (!connected) {
                callback.onFailure(new Exception("Invalid Login"));
                return;
            }

            final String studentName                            = SagresParser.getUserName(html);
            final HashMap<String, List<SagresClassDay>> classes = SagresScheduleParser.getCompleteSchedule(html);
            final List<SagresMessage> messages                  = SagresMessagesParser.getStartPageMessages(html);

            if (callback != null) {
                callback.onSuccess(new SagresProfile(studentName, messages, classes));
            }

            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Finished Fetching Profile");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "JSONException in login");
        }

    }
}
