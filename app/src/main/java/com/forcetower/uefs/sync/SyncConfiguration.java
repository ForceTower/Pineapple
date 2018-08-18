package com.forcetower.uefs.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.forcetower.uefs.R;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.util.VersionUtils;

import timber.log.Timber;

/**
 * Created by João Paulo on 08/03/2018.
 */
public class SyncConfiguration {
    public static final String PREF_SETUP_COMPLETE = "sync_setup_account_completed";
    public static final String PREF_ACCOUNT_SYNC_FREQUENCY = "sync_frequency";
    public static final int SYNC_FREQUENCY = 60;

    public static void initializeSyncAdapter(@NonNull Context context) {
        boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SETUP_COMPLETE, false);

        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = GenericAccountService.getAccount(context);
        //noinspection ConstantConditions
        if (accountManager.addAccountExplicitly(account, null, null)) {
            Timber.d("Account created");
            startPeriodicSync(context, account, SYNC_FREQUENCY);
        } else {
            String authority = context.getString(R.string.authority);
            if (ContentResolver.getPeriodicSyncs(account, authority).isEmpty()) {
                startPeriodicSync(context, account, 60);
            } else {
                Timber.d("Account not created.... ");
            }
        }

        if (!setupComplete) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true)
                    .apply();
        }
    }

    private static void onAccountCreated(@NonNull Account account, @NonNull Context context) {
        configurePeriodicSync(context, 60, 20);
        syncImmediately(context);
    }

    public static void configurePeriodicSync(@NonNull Context context, long interval, long flex) {
        Account account = GenericAccountService.getAccount(context);
        String authority = context.getString(R.string.authority);
        if (VersionUtils.isKitkat()) {
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(interval, flex)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, interval);
        }
    }

    public static void syncImmediately(@NonNull Context context) {
        Account account = GenericAccountService.getAccount(context);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, context.getString(R.string.authority), bundle);
    }

    public static void stopPeriodicSync(@NonNull Context context) {
        Account account = GenericAccountService.getAccount(context);
        String authority = context.getString(R.string.authority);
        ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);
        ContentResolver.setIsSyncable(account, authority, 0);
        ContentResolver.setSyncAutomatically(account, authority, false);
    }

    public static void startPeriodicSync(@NonNull Context context, Account account, int frequencyMinutes) {
        if (account == null) account = GenericAccountService.getAccount(context);

        String authority = context.getString(R.string.authority);

        ContentResolver.setIsSyncable(account, authority, 1);
        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, 60 * frequencyMinutes);
    }

    public static void setNewPeriodForSync(@NonNull Context context, int frequencyMinutes) {
        stopPeriodicSync(context);
        startPeriodicSync(context, null, frequencyMinutes);
    }
}