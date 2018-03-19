package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.view.control_room.ControlRoomActivity;
import com.forcetower.uefs.view.experimental.good_barrel.GoodBarrelActivity;
import com.forcetower.uefs.vm.ProfileViewModel;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class ProfileFragment extends Fragment implements Injectable {
    @BindView(R.id.tv_std_name)
    TextView tvStdName;
    @BindView(R.id.tv_std_semester)
    TextView tvStdSemester;
    @BindView(R.id.tv_std_semester_hide)
    TextView tvStdSemesterHidden;
    @BindView(R.id.tv_std_score)
    TextView tvStdScore;
    @BindView(R.id.tv_last_update)
    TextView tvLastUpdate;
    @BindView(R.id.tv_last_update_attempt)
    TextView tvLastUpdateAttempt;
    @BindView(R.id.cv_calendar)
    CardView cvCalendar;
    @BindView(R.id.cv_update_control)
    CardView cvUpdateControl;
    @BindView(R.id.cv_good_barrel)
    CardView cvGoodBarrel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private NavigationController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            controller = (NavigationController) context;
        } catch (ClassCastException ignored) {
            Timber.d("Activity %s must implement NavigationController", context.getClass().getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        cvCalendar.setOnClickListener(v -> controller.navigateToCalendar());
        cvUpdateControl.setOnClickListener(v -> goToUpdateControl());
        cvGoodBarrel.setOnClickListener(v -> goToBarrel());

        if (BuildConfig.DEBUG) {
            tvLastUpdateAttempt.setVisibility(View.VISIBLE);
            cvUpdateControl.setVisibility(View.VISIBLE);
            cvGoodBarrel.setVisibility(View.VISIBLE);
        } else {
            tvLastUpdateAttempt.setVisibility(View.GONE);
            cvUpdateControl.setVisibility(View.GONE);
            cvGoodBarrel.setVisibility(View.GONE);
        }

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("show_score", false)) {
            tvStdScore.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ProfileViewModel profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        profileViewModel.getProfile().observe(this, this::onReceiveProfile);
        profileViewModel.getSemesters().observe(this, this::onReceiveSemesters);
    }

    private void onReceiveProfile(Profile profile) {
        if (profile == null) return;

        tvStdName.setText(profile.getName());

        if (profile.getScore() >= 0)
            tvStdScore.setText(getString(R.string.student_score, profile.getScore()));

        Timber.d("Last Sync: %s", profile.getLastSync());
        String date = DateUtils.convertTime(profile.getLastSync());
        String attempt = DateUtils.convertTime(profile.getLastSyncAttempt());
        tvLastUpdate.setText(getString(R.string.last_information_update, date));
        tvLastUpdateAttempt.setText(getString(R.string.last_information_update_attempt, attempt));
    }

    private void onReceiveSemesters(List<Semester> semesters) {
        if (semesters.size() == 0) {
            Timber.d("F R E S H M A N");
            tvStdSemester.setText(R.string.student_freshman);
        } else {
            int currently = 0;
            for (Semester semester : semesters) {
                if (semester.getName().trim().length() == 5)
                    currently++;
            }
            tvStdSemester.setText(getString(R.string.student_semester, currently));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (preferences.getBoolean("show_score", false)) {
            tvStdScore.setVisibility(View.VISIBLE);
        } else {
            tvStdScore.setVisibility(View.INVISIBLE);
        }

        if (preferences.getBoolean("show_current_semester", true)) {
            tvStdSemester.setVisibility(View.VISIBLE);
            tvStdSemesterHidden.setVisibility(View.GONE);
        } else {
            tvStdSemesterHidden.setVisibility(View.VISIBLE);
            tvStdSemester.setVisibility(View.GONE);
        }
    }

    private void goToUpdateControl() {
        ControlRoomActivity.startActivity(getContext());
    }

    private void goToBarrel() {
        GoodBarrelActivity.startActivity(getContext());
    }
}
