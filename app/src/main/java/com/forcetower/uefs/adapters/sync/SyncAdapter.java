package com.forcetower.uefs.adapters.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.ArrayList;
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
        //resolver.applyBatch(SagresContract.CONTENT_AUTHORITY, batch);
        //resolver.notifyChange(SagresContract.Entry.CONTENT_URI, null, false);
    }

    private void fetchData() {
        SagresProfile profile = SagresProfile.getCurrentProfile();
        List<SagresMessage> messagesBefore = profile.getMessages();
        //TODO change to just fetch the messages
        SagresProfile.fetchProfileForCurrentAccess();

        SagresProfile profileUpdated = SagresProfile.getCurrentProfile();
        List<SagresMessage> messagesAfter = profileUpdated.getMessages();

        for (SagresMessage message : messagesAfter) {
            if (!messagesBefore.contains(message)) {
                Log.i(Constants.APP_TAG, "New message arrived");
                //batch.add(ContentProviderOperation.newInsert(SagresContract.Entry.CONTENT_URI).build());
            }
        }
    }
}
