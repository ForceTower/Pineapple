package com.forcetower.uefs.view.connected;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.fragments.AllSemestersGradeFragment;
import com.forcetower.uefs.view.connected.fragments.BigTrayFragment;
import com.forcetower.uefs.view.connected.fragments.CalendarFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplinesFragment;
import com.forcetower.uefs.view.connected.fragments.MessagesFragment;
import com.forcetower.uefs.view.connected.fragments.NewScheduleFragment;
import com.forcetower.uefs.view.connected.fragments.ProfileFragment;
import com.forcetower.uefs.view.connected.fragments.ScheduleFragment;
import com.forcetower.uefs.view.logged.ActivityController;
import com.forcetower.uefs.view.suggestion.SuggestionActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import timber.log.Timber;

public class ConnectedFragment extends Fragment implements Injectable, MainContentController {
    public static final String FRAGMENT_INTENT_EXTRA = "notification_intent_extra";
    public static final String SCHEDULE_FRAGMENT = "ScheduleFragment";
    public static final String MESSAGES_FRAGMENT = "MessagesFragment";
    public static final String GRADES_FRAGMENT = "GradesFragment";
    public static final String DISCIPLINES_FRAGMENT = "DisciplinesFragment";
    public static final String CALENDAR_FRAGMENT = "CalendarFragment";
    public static final String BIG_TRAY_FRAGMENT = "BigTrayFragment";

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.view_root)
    ViewGroup viewRoot;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentManager fragmentManager;
    private SharedPreferences preferences;
    private ActivityController controller;

    @IdRes
    private int containerId;
    private boolean showingTab;

    private boolean newScheduleLayout = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            controller = (ActivityController) context;
        } catch (ClassCastException e) {
            Timber.d("Activity %s must implement ActivityController", context.getClass().getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected, container, false);
        ButterKnife.bind(this, view);

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        newScheduleLayout = preferences.getBoolean("new_schedule_layout", false);
        newScheduleLayout = preferences.getBoolean("new_schedule_user_ready", false) && newScheduleLayout;
        Timber.d("New schedule (onCreate) %s", newScheduleLayout);

        fragmentManager = getChildFragmentManager();
        containerId = R.id.container;

        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationOptionSelected);

        if (savedInstanceState == null) {
            preferences.edit().putBoolean("show_not_connected_notification", false).apply();

            if (getArguments() != null) {
                String value = getArguments().getString(FRAGMENT_INTENT_EXTRA);
                if (value == null) {
                    navigateToSchedule();
                } else {
                    Timber.d("Action asks for: %s", value);
                    if (value.equalsIgnoreCase(MESSAGES_FRAGMENT)) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_messages);
                    } else if (value.equalsIgnoreCase(SCHEDULE_FRAGMENT)) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    } else if (value.equalsIgnoreCase(GRADES_FRAGMENT)) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_grades);
                    } else if (value.equalsIgnoreCase(DISCIPLINES_FRAGMENT)) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_disciplines);
                    } else if (value.equalsIgnoreCase(CALENDAR_FRAGMENT)) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_calendar);
                    } else {
                        navigateToSchedule();
                    }
                }
            } else {
                navigateToSchedule();
            }
        } else {
            showingTab = savedInstanceState.getBoolean("tab_showing", false);
            setTabShowing(showingTab);
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        newScheduleLayout = preferences.getBoolean("new_schedule_layout", false);
        newScheduleLayout = preferences.getBoolean("new_schedule_user_ready", false) && newScheduleLayout;
    }

    @MainThread
    private boolean onNavigationOptionSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        controller.selectItemFromNavigation(id);
        if      (id == R.id.navigation_home)        navigateToSchedule();
        else if (id == R.id.navigation_grades)      navigateToGrades();
        else if (id == R.id.navigation_messages)    navigateToMessages();
        else if (id == R.id.navigation_disciplines) navigateToDisciplines();
        else if (id == R.id.navigation_calendar)    navigateToCalendar();

        return true;
    }

    @Override
    public void navigateToSchedule() {
        changeTitle(R.string.title_schedule);
        setTabShowing(false);
        Timber.d("Show new schedule? %s", newScheduleLayout);
        if (newScheduleLayout) {
            changeFragment(new NewScheduleFragment());
            showNewScheduleWarning();
        }
        else {
            changeFragment(new ScheduleFragment());
        }
    }

    @Override
    public void navigateToMessages() {
        changeTitle(R.string.title_messages);
        setTabShowing(false);
        changeFragment(new MessagesFragment());
    }

    @Override
    public void navigateToGrades() {
        changeTitle(R.string.title_grades);
        setTabShowing(true);
        changeFragment(new AllSemestersGradeFragment());
    }

    @Override
    public void navigateToDisciplines() {
        changeTitle(R.string.title_disciplines);
        setTabShowing(false);
        changeFragment(new DisciplinesFragment());
    }

    @Override
    public void navigateToCalendar() {
        changeTitle(R.string.title_calendar);
        setTabShowing(false);
        changeFragment(new CalendarFragment());
    }

    @MainThread
    private void changeFragment(@NonNull Fragment fragment) {
        Fragment current = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (current != null) fragment = current;

        fragmentManager.beginTransaction()
                .replace(containerId, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    private void changeTitle(@StringRes int idRes) {
        controller.changeTitle(idRes);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("tab_showing", showingTab);
        super.onSaveInstanceState(outState);
    }

    private void setTabShowing(boolean b) {
        if (!b) controller.getTabLayout().setVisibility(View.GONE);
        else AnimUtils.fadeIn(requireContext(), controller.getTabLayout());

        showingTab = b;
    }

    @Override
    public void showNewScheduleError(Exception e) {
        newScheduleLayout = false;

        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putBoolean("new_schedule_layout", false)
                .apply();

        navigateToSchedule();

        Snackbar snackbar = Snackbar.make(viewRoot, getString(R.string.new_schedule_errors), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.send_error, v -> {
            SuggestionActivity.startActivity(requireContext(), e.getMessage(), e.getStackTrace());
            snackbar.dismiss();
        });
        snackbar.show();
    }

    private void showNewScheduleWarning() {
        if (!preferences.getBoolean("warnings_4.0.0_new_schedule", false)) {
            if (!VersionUtils.isFirstInstall(requireContext()))
                Toast.makeText(requireContext(), R.string.new_schedule_takes_an_update, Toast.LENGTH_LONG).show();
            preferences.edit().putBoolean("warnings_4.0.0_new_schedule", true).apply();
        }
    }
}
