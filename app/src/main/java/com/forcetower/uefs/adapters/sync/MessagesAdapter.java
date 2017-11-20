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
import com.forcetower.uefs.providers.SagresContract;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 20/11/2017.
 */

public class MessagesAdapter extends AbstractThreadedSyncAdapter {

    public MessagesAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(Constants.APP_TAG, "Performing Synchronization");
        try {
            final ContentResolver resolver = getContext().getContentResolver();

            SagresProfile profile = SagresProfile.getCurrentProfile();
            List<SagresMessage> messagesBefore = profile.getMessages();

            //TODO change to just fetch the messages
            SagresProfile.fetchProfileForCurrentAccess();

            SagresProfile profileUpdated = SagresProfile.getCurrentProfile();
            List<SagresMessage> messagesAfter = profileUpdated.getMessages();

            ArrayList<ContentProviderOperation> batch = new ArrayList<>();

            for (SagresMessage message : messagesAfter) {
                if (!messagesBefore.contains(message)) {
                    batch.add(ContentProviderOperation.newInsert(SagresContract.Entry.CONTENT_URI).build());
                }
            }

            resolver.applyBatch(SagresContract.CONTENT_AUTHORITY, batch);
            resolver.notifyChange(SagresContract.Entry.CONTENT_URI, null, false);
        } catch (RemoteException e) {
            Log.i(Constants.APP_TAG, "Remote Exception");
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            Log.i(Constants.APP_TAG, "Operation Application Exception");
            e.printStackTrace();
        }
    }
}
