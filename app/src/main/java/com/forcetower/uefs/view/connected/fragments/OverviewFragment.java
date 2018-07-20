package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentDisciplineOverviewBinding;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.vm.base.DisciplinesViewModel;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment.INTENT_DISCIPLINE_GROUP_ID;
import static com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment.INTENT_DISCIPLINE_ID;

/**
 * Created by João Paulo on 09/03/2018.
 */

public class OverviewFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigation;

    private DisciplinesViewModel disciplinesViewModel;
    private Discipline mDiscipline;
    private ActivityController controller;
    private FragmentDisciplineOverviewBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discipline_overview, container, false);
        if (getArguments() != null) {
            int disciplineId = getArguments().getInt(INTENT_DISCIPLINE_ID);
            binding.misses.cardMissedClasses.setOnClickListener(v -> navigation.navigateToMissedClasses(disciplineId));
        }
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disciplinesViewModel = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
        //noinspection ConstantConditions
        int groupId = getArguments().getInt(INTENT_DISCIPLINE_GROUP_ID);
        Timber.d("This is my groupId: %d", groupId);
        disciplinesViewModel.findDisciplineFromGroupId(groupId).observe(this, this::onDisciplineChanged);
        disciplinesViewModel.findDisciplineGroup(groupId).observe(this, this::onDisciplineGroupChanged);
        disciplinesViewModel.fetchDetails(groupId).observe(this, this::onFetchUpdate);

        if (!disciplinesViewModel.isDetailsFinished())
            AnimUtils.fadeIn(getContext(), binding.pbLoading);

        binding.draftCard.setOnClickListener(v -> {
            if (disciplinesViewModel.isDetailsFinished()) {
                Timber.d("Click Accepted");
                disciplinesViewModel.triggerFetchDetails();
                disciplinesViewModel.fetchDetails(groupId);
                AnimUtils.fadeIn(getContext(), binding.pbLoading);
                binding.pbLoading.setIndeterminate(true);
            } else {
                Timber.d("Details were no finished, click rejected");
            }
        });

        binding.prevNext.cardPrevNext.setOnClickListener(v -> controller.navigateToDisciplineClasses(groupId));
    }

    private void onDisciplineGroupChanged(DisciplineGroup group) {
        if (group == null) {
            Timber.d("Group is null... Thinking...");
            return;
        }

        //Achievement
        if (!group.isDraft() && mDiscipline != null) {
            String situation = mDiscipline.getSituation();
            if (situation == null || situation.equalsIgnoreCase("Em Aberto")) {
                Timber.d("Ignored because this semester isn't finished yet");
            } else {
                String[] dates = group.getClassPeriod().split("até");
                if (dates.length == 2) {
                    int monthDifference = DateUtils.getDifference(dates[0], dates[1], 1);
                    if (monthDifference != -1) {
                        UBaseActivity activity = ((UBaseActivity) requireActivity());
                        if (monthDifference <= 6)
                            activity.unlockAchievements(getString(R.string.achievement_6_month_semester), activity.mPlayGamesInstance);
                    }

                    int yearDifference = DateUtils.getDifference(dates[0], dates[1], 2);
                    if (monthDifference != -1) {
                        UBaseActivity activity = ((UBaseActivity) requireActivity());
                        if (yearDifference >= 1)
                            activity.unlockAchievements(getString(R.string.achievement_year_turner_semester), activity.mPlayGamesInstance);
                    }
                }
            }
        }

        if (!group.isDraft()) binding.general.tvClassCredits.setText(getString(R.string.class_details_credits, group.getCredits()));
        binding.general.tvClassPeriod.setText(getString(R.string.class_details_class_period, group.isDraft() ? "???" : group.getClassPeriod()));
        binding.general.tvClassTeacher.setText(getString(R.string.class_details_teacher, group.isDraft() ? "???" : group.getTeacher()));
        binding.general.tvClassDepartment.setText(group.isDraft() ? getString(R.string.class_details_department, "???") : group.getDepartment());
        binding.detailsInfo.tvClassGroup.setText(group.getGroup());

        if (!group.isDraft()) {
            binding.draftCard.setVisibility(View.GONE);
            binding.prevNext.tvShowAllClasses.setVisibility(View.VISIBLE);
            binding.prevNext.cardPrevNext.setClickable(true);
            binding.prevNext.cardPrevNext.setFocusable(true);
        }
        else {
            binding.prevNext.tvShowAllClasses.setVisibility(View.GONE);
            binding.prevNext.cardPrevNext.setClickable(false);
            binding.prevNext.cardPrevNext.setFocusable(false);
        }
    }

    private void onFetchUpdate(Resource<Integer> resource) {
        if (resource == null) {
            Timber.d("Nice meme, fetch details res is null");
            return;
        }

        if (resource.status == Status.SUCCESS) {
            Timber.d("Fetch class details success");
            disciplinesViewModel.setDetailsFinished(true);
            AnimUtils.fadeOutGone(getContext(), binding.pbLoading);
        } else if (resource.status == Status.ERROR) {
            Timber.d("Class details fetch error, message: %s", resource.message);
            disciplinesViewModel.setDetailsFinished(true);
            AnimUtils.fadeOutGone(getContext(), binding.pbLoading);
            Toast.makeText(getContext(), R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
        } else {
            //noinspection ConstantConditions
            Timber.d("Loading: %s", getString(resource.data));
            AnimUtils.fadeIn(getContext(), binding.pbLoading);
        }
    }

    private void onDisciplineChanged(Discipline discipline) {
        if (discipline == null) {
            Timber.d("Discipline is null... Thinking...");
            return;
        }

        this.mDiscipline = discipline;
        String fullName = discipline.getCode() + " - " + discipline.getName();
        binding.detailsInfo.tvClassName.setText(fullName);
        binding.misses.tvClassesMissed.setText(getString(R.string.class_details_classes_missed, discipline.getMissedClasses()));

        int missed = discipline.getMissedClasses();
        int maxAllowed = discipline.getCredits()/4;

        int remains = maxAllowed - missed;
        if (remains > (maxAllowed / 4) + 1 && remains <= (maxAllowed / 2)) {
            binding.misses.ivIconClassMissed.setImageResource(R.drawable.ic_tag_faces_neutral_black_24dp);
        } else if (remains <= (maxAllowed / 4) + 1 && remains >= 0)
            binding.misses.ivIconClassMissed.setImageResource(R.drawable.ic_tag_faces_sad_black_24dp);
        else if (remains < 0)
            binding.misses.ivIconClassMissed.setImageResource(R.drawable.ic_tag_faces_dead_black_24dp);

        binding.general.tvClassMissLimit.setText(getString(R.string.class_details_miss_limit, maxAllowed));
        binding.misses.pbClassesMissed.setProgress((missed*100)/maxAllowed);

        if (remains > 0) binding.misses.tvMissRemain.setText(getString(R.string.class_details_miss_remain, remains));
        else if (remains == 0) binding.misses.tvMissRemain.setText(getString(R.string.class_details_can_not_miss_anymore));
        else binding.misses.tvMissRemain.setText(getString(R.string.class_details_failed_by_lack));

        binding.prevNext.tvLastClass.setText(getString(R.string.class_details_previous_class, discipline.getLastClass()));
        binding.prevNext.tvNextClass.setText(getString(R.string.class_details_next_class, discipline.getNextClass()));

        binding.general.tvClassCredits.setText(getString(R.string.class_details_credits, discipline.getCredits()));
    }
}
