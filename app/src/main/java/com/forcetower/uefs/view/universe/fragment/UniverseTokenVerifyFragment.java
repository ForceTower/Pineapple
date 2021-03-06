package com.forcetower.uefs.view.universe.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentUniverseAccessVerifBinding;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.universe.UniverseNavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.universe.UAccountViewModel;

import java.util.Collections;

import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UniverseTokenVerifyFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    UniverseNavigationController navigation;

    private FragmentUniverseAccessVerifBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_universe_access_verif, container, false);
        return binding.getRoot();
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
                    new Pair<>(getString(R.string.transition_logo), binding.ivLogo)
            ));
        } else {
            Timber.d("Has Access Token. Continue to Universe");
            navigation.navigateToCompleted(Collections.singletonList(
                    new Pair<>(getString(R.string.transition_logo), binding.ivLogo)
            ), requireContext());
        }
    }
}
