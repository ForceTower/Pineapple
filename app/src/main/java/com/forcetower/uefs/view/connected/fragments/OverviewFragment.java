package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.DateUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.vm.base.DisciplinesViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment.INTENT_DISCIPLINE_GROUP_ID;

/**
 * Created by João Paulo on 09/03/2018.
 */

public class OverviewFragment extends Fragment implements Injectable {
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.draft_card)
    CardView draftCard;
    @BindView(R.id.tv_class_credits)
    TextView classCredits;
    @BindView(R.id.tv_class_period)
    TextView classPeriod;
    @BindView(R.id.tv_class_teacher)
    TextView classTeacher;
    @BindView(R.id.tv_class_department)
    TextView classDepartment;
    @BindView(R.id.tv_class_miss_limit)
    TextView classMissLimit;
    //View References card faults
    @BindView(R.id.iv_icon_class_missed)
    ImageView classAlertImage;
    @BindView(R.id.tv_classes_missed)
    TextView classesMissed;
    @BindView(R.id.tv_miss_remain)
    TextView classMissRemain;
    @BindView(R.id.pb_classes_missed)
    ProgressBar classesMissedProgress;
    //View References card prev and next
    @BindView(R.id.tv_last_class)
    TextView classPrevious;
    @BindView(R.id.tv_next_class)
    TextView classNext;
    @BindView(R.id.tv_show_all_classes)
    TextView tvShowAllClasses;
    @BindView(R.id.card_prev_next)
    CardView cvClasses;
    //View References card identification
    @BindView(R.id.tv_class_name)
    TextView className;
    @BindView(R.id.tv_class_group)
    TextView classGroup;
    //View References card classes and day
    //@BindView(R.id.inc_card_classes_days)
    //CardView cardViewClassesDay;
    //@BindView(R.id.rv_class_time)
    //RecyclerView classDayAndTime;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private DisciplinesViewModel disciplinesViewModel;

    private Discipline mDiscipline;

    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discipline_overview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disciplinesViewModel = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
        int groupId = getArguments().getInt(INTENT_DISCIPLINE_GROUP_ID);
        Timber.d("This is my groupId: %d", groupId);
        disciplinesViewModel.findDisciplineFromGroupId(groupId).observe(this, this::onDisciplineChanged);
        disciplinesViewModel.findDisciplineGroup(groupId).observe(this, this::onDisciplineGroupChanged);
        disciplinesViewModel.fetchDetails(groupId).observe(this, this::onFetchUpdate);

        if (!disciplinesViewModel.isDetailsFinished())
            AnimUtils.fadeIn(getContext(), pbLoading);

        draftCard.setOnClickListener(v -> {
            if (disciplinesViewModel.isDetailsFinished()) {
                Timber.d("Click Accepted");
                disciplinesViewModel.triggerFetchDetails();
                disciplinesViewModel.fetchDetails(groupId);
                AnimUtils.fadeIn(getContext(), pbLoading);
                pbLoading.setIndeterminate(true);
            } else {
                Timber.d("Details were no finished, click rejected");
            }
        });

        cvClasses.setOnClickListener(v -> controller.navigateToDisciplineClasses(groupId));
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

        if (!group.isDraft()) classCredits.setText(getString(R.string.class_details_credits, group.getCredits()));
        classPeriod.setText(getString(R.string.class_details_class_period, group.isDraft() ? "???" : group.getClassPeriod()));
        classTeacher.setText(getString(R.string.class_details_teacher, group.isDraft() ? "???" : group.getTeacher()));
        classDepartment.setText(group.isDraft() ? getString(R.string.class_details_department, "???") : group.getDepartment());
        classGroup.setText(group.getGroup());

        if (!group.isDraft()) {
            draftCard.setVisibility(View.GONE);
            tvShowAllClasses.setVisibility(View.VISIBLE);
            cvClasses.setClickable(true);
            cvClasses.setFocusable(true);
        }
        else {
            tvShowAllClasses.setVisibility(View.GONE);
            cvClasses.setClickable(false);
            cvClasses.setFocusable(false);
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
            AnimUtils.fadeOutGone(getContext(), pbLoading);
        } else if (resource.status == Status.ERROR) {
            Timber.d("Class details fetch error, message: %s", resource.message);
            disciplinesViewModel.setDetailsFinished(true);
            AnimUtils.fadeOutGone(getContext(), pbLoading);
            Toast.makeText(getContext(), R.string.failed_to_connect, Toast.LENGTH_SHORT).show();
        } else {
            Timber.d("Loading: %s", getString(resource.data));
            AnimUtils.fadeIn(getContext(), pbLoading);
        }
    }

    private void onDisciplineChanged(Discipline discipline) {
        if (discipline == null) {
            Timber.d("Discipline is null... Thinking...");
            return;
        }

        this.mDiscipline = discipline;
        String fullName = discipline.getCode() + " - " + discipline.getName();
        className.setText(fullName);
        classesMissed.setText(getString(R.string.class_details_classes_missed, discipline.getMissedClasses()));

        int missed = discipline.getMissedClasses();
        int maxAllowed = discipline.getCredits()/4;

        int remains = maxAllowed - missed;
        if (remains > (maxAllowed / 4) + 1 && remains <= (maxAllowed / 2)) {
            classAlertImage.setImageResource(R.drawable.ic_tag_faces_neutral_black_24dp);
        } else if (remains <= (maxAllowed / 4) + 1 && remains >= 0)
            classAlertImage.setImageResource(R.drawable.ic_tag_faces_sad_black_24dp);
        else if (remains < 0)
            classAlertImage.setImageResource(R.drawable.ic_tag_faces_dead_black_24dp);

        classMissLimit.setText(getString(R.string.class_details_miss_limit, maxAllowed));
        classesMissedProgress.setProgress((missed*100)/maxAllowed);

        if (remains > 0) classMissRemain.setText(getString(R.string.class_details_miss_remain, remains));
        else if (remains == 0) classMissRemain.setText(getString(R.string.class_details_can_not_miss_anymore));
        else classMissRemain.setText(getString(R.string.class_details_failed_by_lack));

        classPrevious.setText(getString(R.string.class_details_previous_class, discipline.getLastClass()));
        classNext.setText(getString(R.string.class_details_next_class, discipline.getNextClass()));

        classCredits.setText(getString(R.string.class_details_credits, discipline.getCredits()));
    }
}
