package com.forcetower.uefs.view.connected.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentGoldMonkeyBinding;
import com.forcetower.uefs.view.connected.ActivityController;

/**
 * Created by João Paulo on 01/06/2018.
 */
public class GoldMonkeyFragment extends Fragment {

    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentGoldMonkeyBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gold_monkey, container, false);

        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_alpha_testing);
        return binding.getRoot();
    }
}
