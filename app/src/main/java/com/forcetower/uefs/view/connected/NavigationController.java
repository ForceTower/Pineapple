package com.forcetower.uefs.view.connected;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.forcetower.uefs.R;
import com.forcetower.uefs.view.connected.fragments.AutoSyncFragment;
import com.forcetower.uefs.view.connected.fragments.BigTrayFragment;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineClassesFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment;
import com.forcetower.uefs.view.connected.fragments.ProfileFragment;
import com.forcetower.uefs.view.connected.fragments.TheAdventureFragment;
import com.forcetower.uefs.view.suggestion.SuggestionFragment;

import javax.inject.Inject;

import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.CALENDAR_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.DISCIPLINES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.FRAGMENT_INTENT_EXTRA;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.GRADES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.MESSAGES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.SCHEDULE_FRAGMENT;

/**
 * Created by João Paulo on 11/04/2018.
 */
public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(LoggedActivity activity) {
        containerId = R.id.drawer_container;
        fragmentManager = activity.getSupportFragmentManager();
    }

    public void navigateToAutoSync() {
        changeFragment(new AutoSyncFragment());
    }

    public void navigateToSchedule() {
        Fragment content = new ConnectedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_INTENT_EXTRA, SCHEDULE_FRAGMENT);
        content.setArguments(bundle);
        changeFragment(content);
    }

    public void navigateToMessages() {
        Fragment content = new ConnectedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_INTENT_EXTRA, MESSAGES_FRAGMENT);
        content.setArguments(bundle);
        changeFragment(content);
    }

    public void navigateToGrades() {
        Fragment content = new ConnectedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_INTENT_EXTRA, GRADES_FRAGMENT);
        content.setArguments(bundle);
        changeFragment(content);
    }

    public void navigateToDisciplines() {
        Fragment content = new ConnectedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_INTENT_EXTRA, DISCIPLINES_FRAGMENT);
        content.setArguments(bundle);
        changeFragment(content);
    }

    public void navigateToCalendar() {
        Fragment content = new ConnectedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_INTENT_EXTRA, CALENDAR_FRAGMENT);
        content.setArguments(bundle);
        changeFragment(content);
    }

    public void navigateToBigTray() {
        Fragment fragment = new BigTrayFragment();
        changeFragment(fragment);
    }

    public void navigateToProfile() {
        Fragment fragment = new ProfileFragment();
        changeFragment(fragment, "profile", true);
    }

    public void navigateToUNESGame() {
        Fragment fragment = new TheAdventureFragment();
        changeFragment(fragment, "the_adventure", true);
    }

    //This method replaces and don't add to back stack
    //should only be called on start
    public void navigateToMainContent(Bundle bundle) {
        Fragment content = new ConnectedFragment();
        if (bundle != null) content.setArguments(bundle);

        fragmentManager.beginTransaction().replace(containerId, content)
                .commitAllowingStateLoss();
    }

    private void changeFragment(@NonNull Fragment fragment) {
        changeFragment(fragment, null);
    }

    private void changeFragment(@NonNull Fragment fragment, String name, boolean addToStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(containerId, fragment/*, fragment.getClass().getSimpleName()*/)
                .setReorderingAllowed(true);


        if (addToStack) transaction.addToBackStack(name);

        transaction.commitAllowingStateLoss();
    }

    public void changeFragment(@NonNull Fragment fragment, @Nullable String name) {
        changeFragment(fragment, name, false);
    }

    public void navigateToDisciplineDetails(int groupUid, int disciplineUid) {
        changeFragment(DisciplineDetailsFragment.getFragment(groupUid, disciplineUid), "other_arrow_class_details", true);
    }

    public void navigateToDisciplineClasses(int groupId) {
        changeFragment(DisciplineClassesFragment.getFragment(groupId), "other_arrow_class_classes", true);
    }

    public void navigateToSuggestionFragment(String message, String stackTrace) {
        Fragment fragment;
        if (message == null && stackTrace == null)
            fragment = SuggestionFragment.createFragment();
        else
            fragment = SuggestionFragment.createFragment(message, stackTrace);

        changeFragment(fragment, "suggestion", true);
    }
}
