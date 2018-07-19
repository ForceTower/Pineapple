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
import com.forcetower.uefs.view.universe.UniverseNavigationController;

import javax.inject.Inject;

/**
 * Created by João Paulo on 14/05/2018.
 */
public class YouAreReadyFragment extends Fragment implements Injectable {
    @Inject
    UniverseNavigationController navigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_universe_connection_ready, container, false);
    }
}
