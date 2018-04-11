package com.forcetower.uefs.view.connected.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.logged.ActivityController;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class AutoSyncFragment extends Fragment implements Injectable {
    public static final String PREF_AUTO_SYNC_SKIPPED = "auto_sync_skipped";
    public static final String PREF_AUTO_SYNC_SHOWN = "auto_sync_shown";

    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.btn_enable_sync)
    Button btnEnableSync;

    ActivityController controller;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_sync, container, false);
        ButterKnife.bind(this, view);

        btnContinue.setOnClickListener(v -> {
            controller.getNavigationController().navigateToSchedule();
            setPreference();
        });

        btnEnableSync.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
            requireContext().startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean autoSync = ContentResolver.getMasterSyncAutomatically();
        if (autoSync) controller.getNavigationController().navigateToSchedule();
    }

    private void setPreference() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putBoolean(PREF_AUTO_SYNC_SHOWN, true)
                .apply();
    }
}
