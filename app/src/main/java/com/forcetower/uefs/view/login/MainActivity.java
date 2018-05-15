package com.forcetower.uefs.view.login;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.login.fragment.LoginFormFragment;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.FRAGMENT_INTENT_EXTRA;

public class MainActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<androidx.fragment.app.Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppDatabase database;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_main, savedInstanceState);

        String value = getIntent().getStringExtra(FRAGMENT_INTENT_EXTRA);
        if (value == null) {
            Timber.d("Default open");
        } else if (!value.equalsIgnoreCase("LOGIN")){
            Timber.d("Value: %s", value);
            openPlayStore(value);
        }

        if (savedInstanceState == null) {
            checkoutNotification();
            updateReset();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LoginFormFragment())
                    .commit();
        }
    }

    private void checkoutNotification() {
        String notificationExtra = getIntent().getStringExtra("notification_unes_extra");

        Timber.d("Extras: %s", getIntent().getExtras());
        if (notificationExtra == null) return;

        if (notificationExtra.equalsIgnoreCase("save_message")) {
            saveMessage(getIntent().getStringExtra("notification_message"));
        }
    }

    private void saveMessage(String message) {
        if (message == null) return;

        String author = getIntent().getStringExtra("notification_author");
        if (author == null) author = "João Paulo - ForceTower";

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int mon = calendar.get(Calendar.MONTH) + 1;
        int yea = calendar.get(Calendar.YEAR);
        String received = day + "/" + mon + "/" + yea;
        Message msg = new Message(author, message, received, "Notificações Gerais");
        msg.setNotified(1);
        new Thread(() -> database.messageDao().insertMessages(msg)).start();
    }

    private void openPlayStore(String value) {
        String packageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private void updateReset() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains("update_v_3.0.0.rc1")) {
            Timber.d("Performing full clear");
            preferences.edit().clear().apply();
            ((UEFSApplication)getApplication()).clearApplicationData();
            preferences.edit().putInt("update_v_3.0.0.rc1", 1).apply();
        }

    }

    @Override
    public AndroidInjector<androidx.fragment.app.Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
