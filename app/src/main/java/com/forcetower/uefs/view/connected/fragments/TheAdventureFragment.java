package com.forcetower.uefs.view.connected.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;

import butterknife.ButterKnife;

/**
 * Created by João Paulo on 13/04/2018.
 */
public class TheAdventureFragment extends Fragment implements Injectable {
    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_the_adventure, container, false);

        controller.changeTitle(R.string.unes_the_adventure);
        ButterKnife.bind(this, view);
        return view;
    }
}
