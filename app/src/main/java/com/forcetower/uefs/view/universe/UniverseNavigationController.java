package com.forcetower.uefs.view.universe;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ChangeBoundsTransition;
import com.forcetower.uefs.db_service.entity.Version;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.universe.fragment.UniverseCreateAccountFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseKnowAboutFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseLoginFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseTokenVerifyFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseWelcomeStartFragment;
import com.forcetower.uefs.view.universe.fragment.YouAreReadyFragment;

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
        androidx.fragment.app.Fragment fragment = new UniverseWelcomeStartFragment();
        navigateToFragment(fragment, false, null, null, shared);
    }

    public void navigateToCreateAccount(@Nullable List<Pair<String, View>> shared) {
        Fragment fragment = new UniverseCreateAccountFragment();
        fragment.setAllowEnterTransitionOverlap(false);
        navigateToFragment(fragment, true, "create account", null, shared, false);
    }

    public void navigateToLogin(@Nullable List<Pair<String, View>> shared) {
        Fragment fragment = new UniverseLoginFragment();
        navigateToFragment(fragment, false, "login account", null, shared, false);
    }

    public void navigateToCompleted(@Nullable List<Pair<String, View>> shared, Context context) {
        androidx.fragment.app.Fragment fragment = new YouAreReadyFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, context.getResources().getConfiguration().getLayoutDirection())));
        }
        navigateToFragment(fragment, false, "ready", null, shared, true);
    }

    public void navigateToKnowMore(Context context) {
        Fragment fragment = new UniverseKnowAboutFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, context.getResources().getConfiguration().getLayoutDirection())));
        }
        navigateToFragment(fragment, true, "ready", null, null, false);
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

        androidx.fragment.app.FragmentTransaction transaction = manager.beginTransaction();
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
