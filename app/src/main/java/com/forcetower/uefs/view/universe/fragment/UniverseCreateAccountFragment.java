package com.forcetower.uefs.view.universe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;

import butterknife.ButterKnife;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UniverseCreateAccountFragment extends Fragment implements Injectable {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_universe_create_account, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
