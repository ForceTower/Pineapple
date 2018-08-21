package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentDisciplineGradesBinding;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.connected.adapters.GradesAdapter;
import com.forcetower.uefs.vm.base.GradesViewModel;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment.INTENT_DISCIPLINE_ID;

/**
 * Created by João Paulo on 09/03/2018.
 */

public class GradesFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private int disciplineId;
    private GradesAdapter gradesAdapter;
    private FragmentDisciplineGradesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discipline_grades, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.grades.rvDisciplineGrades.setLayoutManager(new LinearLayoutManager(getContext()));
        gradesAdapter = new GradesAdapter();
        binding.grades.rvDisciplineGrades.setAdapter(gradesAdapter);
        binding.grades.rvDisciplineGrades.setNestedScrollingEnabled(false);
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
            binding.situation.tvSituation.setText(discipline.getSituation() != null ? discipline.getSituation() : getString(R.string.situation_unknown));

            String mean = value.second.toString();
            if (mean.equalsIgnoreCase("NaN")) {
                binding.mean.tvPartialMeanUn.setText(R.string.a_series_of_question_marks);
                binding.mean.tvPartialMeanSc.setText(R.string.unable_to_calculate_mean);
                UBaseActivity activity = ((UBaseActivity) requireActivity());
                activity.unlockAchievements(getString(R.string.achievement_surely_disney), activity.mPlayGamesInstance);
            } else {
                if (mean.length() > 5) mean = mean.substring(0, 4);

                if (grade.getPartialMeanValue() < 0) {
                    binding.mean.tvPartialMeanUn.setText(R.string.label_current_mean);
                    binding.mean.tvPartialMeanSc.setText(value.first ? mean : "???");
                    binding.mean.pbMean.setProgress(((int) (value.second * 10)));
                } else {
                    binding.mean.tvPartialMeanUn.setText(R.string.label_final_mean);
                    String display = grade.getPartialMeanValue() + "";
                    binding.mean.tvPartialMeanSc.setText(display);
                    binding.mean.pbMean.setProgress(((int) (grade.getPartialMeanValue() * 10)));
                }
            }

            if (grade.getPartialMeanValue() >= 3 && grade.getPartialMeanValue() < 7) {
                binding.mean.tvNeededMeanUn.setText(R.string.label_need_final_mean);
                Double needed = 12.5 - (1.5 * grade.getPartialMeanValue());
                String display = needed.toString();
                if (display.length() > 5) display = display.substring(0, 4);
                binding.mean.tvNeededMeanSc.setText(display);
                binding.mean.tvPartialMeanUn.setText(R.string.label_partial_mean);
            } else {
                binding.mean.rlNecMean.setVisibility(View.GONE);
            }
        } else {
            binding.grades.cardGrades.setVisibility(View.GONE);
        }
    }
}
