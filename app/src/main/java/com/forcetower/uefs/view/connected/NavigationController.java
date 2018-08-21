package com.forcetower.uefs.view.connected;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.transition.Slide;
import android.view.Gravity;

import com.forcetower.uefs.R;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.fragments.BigTrayFragment;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineClassesFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineMissedClassesFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationFourFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationOneFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationPreviewFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationStartFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationThreeFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationTwoFragment;
import com.forcetower.uefs.view.connected.fragments.EventsFragment;
import com.forcetower.uefs.view.connected.fragments.GoldMonkeyFragment;
import com.forcetower.uefs.view.connected.fragments.OutdatedFragment;
import com.forcetower.uefs.view.connected.fragments.ProfileFragment;
import com.forcetower.uefs.view.connected.fragments.RemindersFragment;
import com.forcetower.uefs.view.connected.fragments.SelectCourseFragment;
import com.forcetower.uefs.view.connected.fragments.SuggestionFragment;
import com.forcetower.uefs.view.connected.fragments.SyncRegistryFragment;
import com.forcetower.uefs.view.connected.fragments.TheAdventureFragment;

import javax.inject.Inject;

import static com.forcetower.uefs.util.SupportUtils.getGravityCompat;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.CALENDAR_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.DISCIPLINES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.FRAGMENT_INTENT_EXTRA;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.GRADES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.MESSAGES_FRAGMENT_SAGRES;
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
        bundle.putString(FRAGMENT_INTENT_EXTRA, MESSAGES_FRAGMENT_SAGRES);
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

    public void navigateToOutdatedVersion() {
        changeFragment(new OutdatedFragment());
    }

    public void navigateToGoldMonkey() {
        changeFragment(new GoldMonkeyFragment(), "gold_monkey", true);
    }

    public void navigateToEvents() {
        changeFragment(new EventsFragment(), "list_events", true);
    }

    public void navigateToCreateEvent(Context ctx) {
        Fragment fragment = new EventCreationStartFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(getGravityCompat(ctx, Gravity.END)));
        }
        changeFragment(fragment, "event_create_zero", true);
    }

    public void navigateToCreateEventOne(Context ctx) {
        Fragment fragment = new EventCreationOneFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(getGravityCompat(ctx, Gravity.END)));
        }
        changeFragment(fragment, "event_create_two", true);
    }

    public void navigateToCreateEventTwo(Context ctx) {
        Fragment fragment = new EventCreationTwoFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(getGravityCompat(ctx, Gravity.END)));
        }
        changeFragment(fragment, "event_create_two", true);
    }

    public void navigateToCreateEventThree(Context ctx) {
        Fragment fragment = new EventCreationThreeFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(getGravityCompat(ctx, Gravity.END)));
        }
        changeFragment(fragment, "event_create_three", true);
    }

    public void navigateToCreateEventFour(Context ctx) {
        Fragment fragment = new EventCreationFourFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(getGravityCompat(ctx, Gravity.END)));
        }
        changeFragment(fragment, "event_create_four", true);
    }

    public void navigateToCreateEventPreview(Context ctx) {
        Fragment fragment = new EventCreationPreviewFragment();
        if (VersionUtils.isLollipop()) {
            fragment.setEnterTransition(new Slide(getGravityCompat(ctx, Gravity.END)));
        }
        changeFragment(fragment, "event_create_preview", true);
    }

    public void backTo(String tag) {
        fragmentManager.popBackStack(tag, 0);
    }

    public void navigateToSelectCourse() {
        Fragment fragment = new SelectCourseFragment();
        changeFragment(fragment, "select_course", true);
    }

    public void navigateToReminders() {
        changeFragment(new RemindersFragment(), "reminders", true);
    }

    public void back() {
        fragmentManager.popBackStack();
    }

    public void navigateToSyncRegistry() {
        changeFragment(new SyncRegistryFragment(), "sync_registry", true);
    }

    public void navigateToMissedClasses(int disciplineId) {
        changeFragment(DisciplineMissedClassesFragment.getFragment(disciplineId), "discipline_missed_classes", true);
    }
}
