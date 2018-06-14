package com.forcetower.uefs.view.universe.fragment;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.universe.UniverseNavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.universe.UAccountViewModel;

import java.util.Collections;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UniverseTokenVerifyFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    UniverseNavigationController navigation;

    @BindView(R.id.iv_logo)
    ImageView ivLogo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_universe_access_verif, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewModels();
    }

    private void setupViewModels() {
        UAccountViewModel accountViewModel = ViewModelProviders.of(this, viewModelFactory).get(UAccountViewModel.class);
        accountViewModel.getAccessToken().observe(this, this::onReceiveToken);
    }

    private void onReceiveToken(AccessToken token) {
        if (token == null) {
            Timber.d("No Access Token. Enable Login");
            navigation.navigateToStartPage(Collections.singletonList(
                    new Pair<>(getString(R.string.transition_logo), ivLogo)
            ));
        } else {
            Timber.d("Has Access Token. Continue to Universe");
            navigation.navigateToCompleted(Collections.singletonList(
                    new Pair<>(getString(R.string.transition_logo), ivLogo)
            ), requireContext());
        }
    }
}
