package com.forcetower.uefs.view.login;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.util.NetworkUtils;
import com.forcetower.uefs.util.WordUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.login.fragment.LoginFormFragment;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.FRAGMENT_INTENT_EXTRA;

public class MainActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LoginFormFragment())
                    .commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
