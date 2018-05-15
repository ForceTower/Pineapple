package com.forcetower.uefs.view.connected.fragments;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class OutdatedFragment extends androidx.fragment.app.Fragment implements Injectable {
    public static final String PREF_AUTO_SYNC_SHOWN = "auto_sync_shown";

    @BindView(R.id.btn_continue)
    Button btnContinue;

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

        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_outdated_version);
        controller.disableDrawer();

        btnContinue.setOnClickListener(v -> openPlayStore());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void openPlayStore() {
        String packageName = requireContext().getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
