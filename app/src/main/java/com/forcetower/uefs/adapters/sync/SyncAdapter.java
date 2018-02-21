package com.forcetower.uefs.adapters.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.helpers.NotificationCreator;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.GradeInfo;
import com.forcetower.uefs.sagres_sdk.domain.GradeSection;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.exception.SagresInfoFetchException;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 20/11/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private ContentResolver resolver;
    private boolean messagesWarned = false;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = getContext().getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(APP_TAG, "Performing Synchronization");

        if (!SagresPortalSDK.isSdkInitialized()) {
            Log.i(APP_TAG, "SDK not initialized on service context");
            SagresPortalSDK.setContext(getContext());
            SagresPortalSDK.initializeSdk(getContext(), this::fetchData);
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        if (SagresAccess.getCurrentAccess() == null) {
            return;
        }

        SagresProfile profile = SagresProfile.getCurrentProfile();
        final List<SagresMessage> messagesBefore = (profile == null) ? null : profile.getMessages();
        final List<SagresMessage> actualBefore = new ArrayList<>();
        if (messagesBefore != null) {
            actualBefore.addAll(messagesBefore);
            Log.i(APP_TAG, "Before not null");
        }

        final HashMap<String, SagresGrade> gradesHashMap = (profile == null) ? null : profile.getGrades();
        final HashMap<String, SagresGrade> beforeGrades = new HashMap<>();
        if (gradesHashMap != null) {
            for (Map.Entry<String, SagresGrade> entry : gradesHashMap.entrySet()) {
                beforeGrades.put(entry.getKey(), entry.getValue());
            }
        }

        //Part of new code
        List<SagresMessage> newMessages = SagresProfile.syncFetchMessages();
        if (newMessages == null) {
            Log.d(APP_TAG, "fetchData: epic fail");
        } else {
            messagesNewWay(newMessages);
            messagesWarned = true;
        }

        //SagresProfile.fetchProfileForCurrentAccess();
        SagresProfile.asyncFetchProfileInformationWithCallback(new SagresUtility.AsyncFetchProfileInformationCallback() {
            @Override
            public void onSuccess(SagresProfile profile) {
                successMeasures(actualBefore, beforeGrades);
            }

            @Override
            public void onInvalidLogin() {}

            @Override
            public void onDeveloperError() {}

            @Override
            public void onFailure(SagresInfoFetchException e) {}

            @Override
            public void onFailedConnect() {}

            @Override
            public void onHalfCompleted(int completedSteps) {}
        });

        //NotificationCreator.createNewMessageNotification(getContext(), new SagresMessage("João Paulo", "Hey there,\n\nThis is a test notification to check if everything works out.\n\nThanks!", "20/11/2017", "The Developer"));
    }

    private void messagesNewWay(List<SagresMessage> newMessages) {
        Log.d(APP_TAG, "messagesNewWay: DO YOU KNOW THE WAY?");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SagresProfile profile = SagresProfile.getCurrentProfile();
        List<SagresMessage> messagesBefore = profile.getMessages();

        boolean showMessageNotification = preferences.getBoolean("show_message_notification", true);
        if (showMessageNotification) {
            if (messagesBefore != null) {
                for (SagresMessage message : newMessages) {
                    if (!messagesBefore.contains(message)) {
                        Log.i(APP_TAG, "New message arrived");
                        NotificationCreator.createNewMessageNotification(getContext(), message);
                    } else {
                        Log.i(APP_TAG, "Message already there");
                    }
                }
            } else {
                Log.i(APP_TAG, "Auto Sync Fetch a new profile[Messages], interesting");
            }
        } else {
            Log.i(APP_TAG, "Messages notifications skipped");
        }
    }

    private void successMeasures(List<SagresMessage> messagesBefore, HashMap<String, SagresGrade> grades) {
        SagresProfile profileUpdated = SagresProfile.getCurrentProfile();
        profileUpdated.saveProfileNonStatic();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean showMessageNotification = preferences.getBoolean("show_message_notification", true);
        if (showMessageNotification && !messagesWarned) {
            List<SagresMessage> messagesAfter = profileUpdated.getMessages();
            if (messagesBefore != null) {
                for (SagresMessage message : messagesAfter) {
                    if (!messagesBefore.contains(message)) {
                        Log.i(APP_TAG, "New message arrived");
                        NotificationCreator.createNewMessageNotification(getContext(), message);
                    } else {
                        Log.i(APP_TAG, "Message already there");
                    }
                }
            } else {
                Log.i(APP_TAG, "Auto Sync Fetch a new profile[Messages], interesting");
            }
        } else {
            Log.i(APP_TAG, "Messages notifications skipped");
        }

        //NotificationCreator.createNewMessageNotification(getContext(), new SagresMessage("My_Self", "Testando... 1 2 3\nSanca postou e nao recebi!!!", "11:59", "TEC709"));

        boolean showGradesNotification = preferences.getBoolean("show_grades_notification", true);
        if (showGradesNotification) {
            HashMap<String, SagresGrade> updatedGrades = profileUpdated.getGrades();

//
//            GradeInfo info = new GradeInfo("AVXX - Teste de Notificação", "9,8", "25/11/2017");
//            updatedGrades.get("TEC412").getSections().get(0).addGradeInfo(info);


            if (grades != null) {
                if (updatedGrades == null) {
                    Log.e(APP_TAG, "Updated grades is null... Prob timed out");
                } else {
                    for (String key : updatedGrades.keySet()) {
                        Log.i(APP_TAG, "Current Key: " + key);
                        SagresGrade before = grades.get(key);
                        SagresGrade after = updatedGrades.get(key);

                        if (after == null) {
                            Log.i(APP_TAG, "Lost track of a grade... [" + key + "]");
                            continue;
                        }

                        if (before == null) {
                            //NotificationCreator.createNewGradeMessage(getContext(), after, NotificationCreator.GENERATED_GRADE);
                            Log.i(APP_TAG, "Now it's tracking a new class...??? " + after.getClassCode());
                            continue;
                        }

                        for (GradeSection nSection : after.getSections()) {
                            GradeSection oSection = before.findSection(nSection.getName());

                            if (oSection != null) {
                                for (GradeInfo nInfo : nSection.getGrades()) {
                                    if (!oSection.getGrades().contains(nInfo)) {
                                        NotificationCreator.createNewGradeNotification(getContext(), nInfo, after, NotificationCreator.ADDED_GRADE);
                                    }
                                }
                            } else {
                                Log.i(APP_TAG, "Class " + after.getClassCode() + " has a new section!");
                                for (GradeInfo nInfo : nSection.getGrades()) {
                                    NotificationCreator.createNewGradeNotification(getContext(), nInfo, after, NotificationCreator.ADDED_GRADE);
                                }
                            }
                        }
                    }
                }
            } else {
                Log.i(APP_TAG, "Auto Sync Fetch a new profile[Grades], interesting");
            }
        } else {
            Log.i(APP_TAG, "Grades notifications skipped");
        }
    }
}
