package com.forcetower.uefs.view.connected.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentAutoSyncBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class OutdatedFragment extends Fragment implements Injectable {
    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAutoSyncBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auto_sync, container, false);

        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_outdated_version);
        controller.disableDrawer();

        binding.btnContinue.setOnClickListener(v -> openPlayStore());
        return binding.getRoot();
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
