package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.connected.adapters.GradesAdapter;
import com.forcetower.uefs.vm.base.GradesViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment.INTENT_DISCIPLINE_ID;

/**
 * Created by João Paulo on 09/03/2018.
 */

public class GradesFragment extends Fragment implements Injectable {
    @BindView(R.id.rv_discipline_grades)
    RecyclerView recyclerView;
    @BindView(R.id.tv_situation)
    TextView tvSituation;
    @BindView(R.id.tv_partial_mean_un)
    TextView tvPartialMeanLabel;
    @BindView(R.id.tv_partial_mean_sc)
    TextView tvPartialMeanValue;
    @BindView(R.id.pb_mean)
    ProgressBar pbMean;
    @BindView(R.id.rl_nec_mean)
    RelativeLayout rlNeededMean;
    @BindView(R.id.tv_needed_mean_un)
    TextView tvNeededMeanLabel;
    @BindView(R.id.tv_needed_mean_sc)
    TextView tvNeededMeanValue;
    @BindView(R.id.card_grades_mean)
    CardView cvGradesMean;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int disciplineId;
    private GradesAdapter gradesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discipline_grades, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gradesAdapter = new GradesAdapter(null, null);
        recyclerView.setAdapter(gradesAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //noinspection ConstantConditions
        disciplineId = getArguments().getInt(INTENT_DISCIPLINE_ID);
        GradesViewModel gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);
        gradesViewModel.getGradesOfDiscipline(disciplineId).observe(this, this::onGradeUpdate);
    }

    private void onGradeUpdate(Discipline discipline) {
        if (discipline == null) {
            Timber.d("Discipline id %d where not found", disciplineId);
            return;
        }

        gradesAdapter.setItems(discipline.getSections(), discipline.getGrade());
        setupView(discipline);
    }

    @MainThread
    private void setupView(Discipline discipline) {
        Grade grade = discipline.getGrade();
        if (grade != null) {
            grade.setSections(discipline.getSections());
            Pair<Boolean, Double> value = grade.getCalculatedPartialMean();
            tvSituation.setText(discipline.getSituation() != null ? discipline.getSituation() : getString(R.string.situation_unknown));

            String mean = value.second.toString();
            if (mean.equalsIgnoreCase("NaN")) {
                tvPartialMeanLabel.setText(R.string.a_series_of_question_marks);
                tvPartialMeanValue.setText(R.string.unable_to_calculate_mean);
                UBaseActivity activity = ((UBaseActivity) requireActivity());
                activity.unlockAchievements(getString(R.string.achievement_surely_disney), activity.mPlayGamesInstance);
            } else {
                if (mean.length() > 5) mean = mean.substring(0, 4);

                if (grade.getPartialMeanValue() < 0) {
                    tvPartialMeanLabel.setText(R.string.label_current_mean);
                    tvPartialMeanValue.setText(value.first ? mean : "???");
                    pbMean.setProgress(((int) (value.second * 10)));
                } else {
                    tvPartialMeanLabel.setText(R.string.label_final_mean);
                    String display = grade.getPartialMeanValue() + "";
                    tvPartialMeanValue.setText(display);
                    pbMean.setProgress(((int) (grade.getPartialMeanValue() * 10)));
                }
            }

            if (grade.getPartialMeanValue() >= 3 && grade.getPartialMeanValue() < 7) {
                tvNeededMeanLabel.setText(R.string.label_need_final_mean);
                Double needed = 12.5 - (1.5 * grade.getPartialMeanValue());
                String display = needed.toString();
                if (display.length() > 5) display = display.substring(0, 4);
                tvNeededMeanValue.setText(display);
                tvPartialMeanLabel.setText(R.string.label_partial_mean);
            } else {
                rlNeededMean.setVisibility(View.GONE);
            }
        } else {
            cvGradesMean.setVisibility(View.GONE);
        }
    }
}
