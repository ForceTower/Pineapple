package com.forcetower.uefs.view.connected.fragments;

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

/**
 * Created by João Paulo on 01/06/2018.
 */
public class GoldMonkeyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentGoldMonkeyBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gold_monkey, container, false);
        return binding.getRoot();
    }
}
