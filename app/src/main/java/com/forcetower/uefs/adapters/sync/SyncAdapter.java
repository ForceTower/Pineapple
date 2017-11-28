package com.forcetower.uefs.adapters.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 20/11/2017.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private ContentResolver resolver;

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
        Log.i(Constants.APP_TAG, "Performing Synchronization");

        if (!SagresPortalSDK.isSdkInitialized()) {
            Log.i(Constants.APP_TAG, "SDK not initialized on service context");
            SagresPortalSDK.initializeSdk(getContext(), new SagresPortalSDK.SagresSDKInitializationCallback() {
                @Override
                public void onFinishInit() {
                    fetchData();
                }
            });
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
        final HashMap<String, SagresGrade> gradesHashMap = (profile == null) ? null : profile.getGrades();

        //SagresProfile.fetchProfileForCurrentAccess();
        SagresProfile.asyncFetchProfileInformationWithCallback(new SagresUtility.AsyncFetchProfileInformationCallback() {
            @Override
            public void onSuccess(SagresProfile profile) {
                successMeasures(profile, messagesBefore, gradesHashMap);
            }

            @Override
            public void onInvalidLogin() {

            }

            @Override
            public void onDeveloperError() {

            }

            @Override
            public void onFailure(SagresInfoFetchException e) {

            }

            @Override
            public void onFailedConnect() {

            }

            @Override
            public void onHalfCompleted(int completedSteps) {

            }
        });

        //NotificationCreator.createNewMessageNotification(getContext(), new SagresMessage("João Paulo", "Hey there,\n\nThis is a test notification to check if everything works out.\n\nThanks!", "20/11/2017", "The Developer"));
    }

    private void successMeasures(SagresProfile profile, List<SagresMessage> messagesBefore, HashMap<String, SagresGrade> grades) {
        SagresProfile profileUpdated = SagresProfile.getCurrentProfile();
        List<SagresMessage> messagesAfter = profileUpdated.getMessages();

        if (messagesBefore != null) {
            for (SagresMessage message : messagesAfter) {
                if (!messagesBefore.contains(message)) {
                    Log.i(Constants.APP_TAG, "New message arrived");
                    NotificationCreator.createNewMessageNotification(getContext(), message);
                }
            }
        } else {
            Log.i(Constants.APP_TAG, "Auto Sync Fetch a new profile[Messages], interesting");
        }

        HashMap<String, SagresGrade> updatedGrades = profileUpdated.getGrades();

        //GradeInfo info = new GradeInfo("AVXX - Teste de Notificação", "9,8", "25/11/2017");
        //updatedGrades.get("TEC412").getSections().get(0).addGradeInfo(info);

        if (grades != null) {
            for (String key : updatedGrades.keySet()) {
                SagresGrade before = grades.get(key);
                SagresGrade after = updatedGrades.get(key);

                if (after == null) {
                    Log.i(Constants.APP_TAG, "Lost track of a grade... [" + key + "]");
                    continue;
                }

                if (before == null) {
                    //NotificationCreator.createNewGradeMessage(getContext(), after, NotificationCreator.GENERATED_GRADE);
                    Log.i(Constants.APP_TAG, "Now it's tracking a new class...??? " + after.getClassCode());
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
                        Log.i(Constants.APP_TAG, "Class " + after.getClassCode() + " has a new section!");
                        for (GradeInfo nInfo : nSection.getGrades()) {
                            NotificationCreator.createNewGradeNotification(getContext(), nInfo, after, NotificationCreator.ADDED_GRADE);
                        }
                    }
                }

            }
        } else {
            Log.i(Constants.APP_TAG, "Auto Sync Fetch a new profile[Grades], interesting");
        }


    }
}