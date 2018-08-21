package com.forcetower.uefs.view.universe.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
public class UniverseKnowAboutFragment extends Fragment implements Injectable {

    @Inject
    UniverseNavigationController navigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_universe_know_more, container, false);
    }
}
