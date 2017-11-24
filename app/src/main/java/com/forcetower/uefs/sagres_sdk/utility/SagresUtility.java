package com.forcetower.uefs.sagres_sdk.utility;

import android.util.Log;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.exception.SagresInfoFetchException;
import com.forcetower.uefs.sagres_sdk.exception.SagresLoginException;
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

    public interface AllInformationFetchWithCacheCallback {
        void onSuccess(SagresProfile profile);
        void onFailure(SagresLoginException e);
        void onLoginSuccess();
    }

    public interface AsyncFetchProfileInformationCallback {
        void onSuccess(SagresProfile profile);
        void onInvalidLogin();
        void onDeveloperError();
        void onFailure(SagresInfoFetchException e);
        void onFailedConnect();
    }

    public static void getInformationFromUserWithCacheAsync(SagresAccess access, AllInformationFetchWithCacheCallback callback) {
        String username = access.getUsername();
        String password = access.getPassword();

        if (!SagresPortalSDK.isSdkInitialized()) {
            if (callback != null) {
                callback.onFailure(new SagresLoginException("Sagres SDK is not initialized"));
            }
            return;
        }

        if (username == null || password == null) {
            if (callback != null) {
                callback.onFailure(new SagresLoginException(true, false, "Fields are null"));
            }
            return;
        }

        try {
            JSONObject loginResponse = SagresConnector.login(username, password);
            if (loginResponse.has("error")) {
                Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Login has error");
                if (callback != null) {
                    callback.onFailure(new SagresLoginException(false, true, "Login has error - Timeout[Prob]"));
                }
                return;
            }

            String html = loginResponse.getString("html");
            html = SagresParser.changeCharset(html);

            boolean connected = SagresParser.connected(html);
            if (!connected) {
                callback.onFailure(new SagresLoginException(true, false, "Invalid Login"));
                return;
            } else {
                if (callback != null) {
                    callback.onLoginSuccess();
                }
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
            callback.onFailure(new SagresLoginException("JSONException in parse - Never should've happened"));
            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "JSONException in login");
        }
    }

    public static void getProfileInformationAsyncWithCallback(final AsyncFetchProfileInformationCallback callback) {
        if (!SagresPortalSDK.isSdkInitialized()) {
            if (callback != null) {
                callback.onFailure(new SagresInfoFetchException("SDK not initialized"));
            }
            return;
        }

        SagresAccess access = SagresAccess.getCurrentAccess();
        if (access == null) {
            if (callback != null) callback.onFailure(new SagresInfoFetchException("Invalid Access"));
            return;
        }

        final String username = access.getUsername();
        final String password = access.getPassword();
        if (username == null || password == null) {
            if (callback != null) callback.onFailure(new SagresInfoFetchException("Fields are null"));
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject loginResponse = SagresConnector.login(username, password);
                    if (loginResponse.has("error")) {
                        Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Login has error - Time out or no network");
                        if (callback != null) {
                            callback.onFailedConnect();
                        }
                        return;
                    }

                    String html = loginResponse.getString("html");
                    html = SagresParser.changeCharset(html);

                    boolean connected = SagresParser.connected(html);
                    if (!connected) {
                        callback.onInvalidLogin();
                        return;
                    }

                    final String studentName                            = SagresParser.getUserName(html);
                    final HashMap<String, List<SagresClassDay>> classes = SagresScheduleParser.getCompleteSchedule(html);
                    final List<SagresMessage> messages                  = SagresMessagesParser.getStartPageMessages(html);

                    if (callback != null) {
                        if (SagresProfile.getCurrentProfile() == null) {
                            SagresProfile.setCurrentProfile(new SagresProfile(studentName, messages, classes));
                        } else {
                            SagresProfile profile = SagresProfile.getCurrentProfile();
                            profile.updateInformation(studentName, messages, classes);
                        }
                        callback.onSuccess(SagresProfile.getCurrentProfile());
                    }

                    Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Finished Fetching Profile");
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onDeveloperError();
                    Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "JSONException in login");
                }
            }
        };

        SagresPortalSDK.getExecutor().execute(runnable);
    }
}
