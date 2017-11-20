package com.forcetower.uefs.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.services.GenericAccountService;

/**
 * Created by João Paulo on 20/11/2017.
 */

public class SyncUtils {
    private static final String AUTHORITY = "com.forcetower.uefs.providers";
    private static final int SYNC_FREQUENCY = 60;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static void createSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PrefUtils.get(context, PREF_SETUP_COMPLETE, false);

        Account account = GenericAccountService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        assert accountManager != null;

        if (accountManager.addAccountExplicitly(account, null, null)) {
            startPeriodicSync(account, SYNC_FREQUENCY);
            Log.i(Constants.APP_TAG, "Account set");
            newAccount = true;

        }

        if (newAccount || !setupComplete) {
            triggerRefresh();
            PrefUtils.save(context, PREF_SETUP_COMPLETE, true);
        }
    }

    private static void triggerRefresh() {
        Log.i(Constants.APP_TAG, "Trigger Refresh");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(GenericAccountService.getAccount(), AUTHORITY, bundle);
    }

    public static void stopPeriodicSync() {
        ContentResolver.removePeriodicSync(GenericAccountService.getAccount(), SyncUtils.AUTHORITY, Bundle.EMPTY);
        ContentResolver.setIsSyncable(GenericAccountService.getAccount(), SyncUtils.AUTHORITY, 0);
        ContentResolver.setSyncAutomatically(GenericAccountService.getAccount(), SyncUtils.AUTHORITY, false);
    }

    private static void startPeriodicSync(Account account, int frequencyMinutes) {
        if (account == null)
            account = GenericAccountService.getAccount();

        ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
        ContentResolver.addPeriodicSync(account, AUTHORITY, Bundle.EMPTY, 60 * frequencyMinutes);
        //triggerRefresh();
    }

    public static void setNewPeriodForSync(int frequencyMinutes) {
        stopPeriodicSync();
        startPeriodicSync(null, frequencyMinutes);
    }
}
