package com.forcetower.uefs.view.universe;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.View;

import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ChangeBoundsTransition;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.universe.fragment.UniverseCreateAccountFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseLoginFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseTokenVerifyFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseWelcomeStartFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 11/05/2018.
 */
@SuppressWarnings("SameParameterValue")
public class UniverseNavigationController {
    private final FragmentManager manager;
    private final int containerId;
    private final ActivityController controller;

    @Inject
    public UniverseNavigationController(UniverseActivity activity) {
        this.manager = activity.getSupportFragmentManager();
        this.containerId = R.id.container;
        this.controller = activity;
    }

    public void initiateNavigation() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, new UniverseTokenVerifyFragment(), UniverseTokenVerifyFragment.class.getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public void navigateToStartPage(@Nullable List<Pair<String, View>> shared) {
        controller.hideToolbar();
        Fragment fragment = new UniverseWelcomeStartFragment();
        navigateToFragment(fragment, false, null, null, shared);
    }

    public void navigateToCreateAccount(@Nullable List<Pair<String, View>> shared) {
        Fragment fragment = new UniverseCreateAccountFragment();
        fragment.setAllowEnterTransitionOverlap(false);
        navigateToFragment(fragment, true, "create account", null, shared, true);
    }

    public void navigateToLogin(@Nullable List<Pair<String, View>> shared) {
        Fragment fragment = new UniverseLoginFragment();
        navigateToFragment(fragment, false, "login account", null, shared, true);
    }

    private void navigateToFragment(Fragment fragment) {
        navigateToFragment(fragment, false, null, null, null);
    }

    private void navigateToFragment(@NonNull Fragment fragment, boolean stack, @Nullable String name,
                                    @Nullable Bundle args, List<Pair<String, View>> shared) {
        navigateToFragment(fragment, stack, name, args, shared, false);
    }

    private void navigateToFragment(@NonNull Fragment fragment, boolean stack, @Nullable String name,
                                         @Nullable Bundle args, @Nullable List<Pair<String, View>> shared, boolean clearTop) {
        if (args != null) fragment.setArguments(args);

        if (clearTop) {
            for (int i = manager.getBackStackEntryCount(); i > 1; i--) {
                manager.popBackStack();
            }
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, fragment, fragment.getClass().getSimpleName());

        if (stack) {
            if (name == null) throw new IllegalArgumentException("name can not be null");
            transaction.addToBackStack(name);
        }
        if (VersionUtils.isLollipop() && shared != null && shared.size() > 0) {
            fragment.setSharedElementEnterTransition(new ChangeBoundsTransition());
            fragment.setSharedElementReturnTransition(new ChangeBoundsTransition());

            for (Pair<String, View> element : shared)
                transaction.addSharedElement(element.second, element.first);
        }

        transaction.commit();
    }
}
