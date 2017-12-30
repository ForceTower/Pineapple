package com.forcetower.uefs.view.class_details;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.parsers.SagresFullClassParser;
import com.forcetower.uefs.view.adapters.DaysAndClassesAdapter;
import com.forcetower.uefs.view.adapters.GradesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class OverviewFragment extends Fragment {
    private static final String CLASS_CODE_KEY = "class_code";
    private static final String SEMESTER_KEY = "semester";
    private static final String GROUP_KEY = "group";

    private SagresClassDetails details;
    private SagresClassGroup detailsGroup;

    @BindView(R.id.sv_details_root)
    ScrollView svRoot;

    //View References card details
    private TextView classCredits;
    private TextView classPeriod;
    private TextView classTeacher;
    private TextView classDepartment;
    private TextView classMissLimit;
    //View References card faults
    private ImageView classAlertImage;
    private TextView classesMissed;
    private TextView classMissRemain;
    private ProgressBar classesMissedProgress;
    //View References card prev and next
    private TextView classPrevious;
    private TextView classNext;
    //View References card identification
    private TextView className;
    private TextView classGroup;
    //View References card grades
    private RecyclerView classGrades;
    //View References card classes and day
    private CardView cardViewClassesDay;
    private RecyclerView classDayAndTime;

    private String semester;
    private String classCode;
    private String group;
    private boolean refreshing = false;

    private ProgressBar loadDetailsProgressBar;

    public OverviewFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_class_details_overview, container, false);
        ButterKnife.bind(this, fragmentView);

        findDesiredClass();
        if (details == null) {
            Toast.makeText(getContext(), R.string.class_not_found_dev_error, Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return fragmentView;
        }

        fragmentView.findViewById(R.id.draft_card).setOnClickListener(view -> refreshClass());
        if (details != null && detailsGroup != null && !detailsGroup.isDraft()) {
            fragmentView.findViewById(R.id.classes_pn_card).setOnClickListener(view -> DisciplineClassesActivity.startActivity(getContext(), detailsGroup.getClasses()));
            fragmentView.findViewById(R.id.tv_show_all_classes).setVisibility(View.VISIBLE);
        } else {
            fragmentView.findViewById(R.id.tv_show_all_classes).setVisibility(View.GONE);
        }

        classCredits = fragmentView.findViewById(R.id.tv_class_credits);
        classPeriod = fragmentView.findViewById(R.id.tv_class_period);
        classTeacher = fragmentView.findViewById(R.id.tv_class_teacher);
        classDepartment = fragmentView.findViewById(R.id.tv_class_department);
        classMissLimit = fragmentView.findViewById(R.id.tv_class_miss_limit);

        classAlertImage = fragmentView.findViewById(R.id.iv_icon_class_missed);
        classesMissed = fragmentView.findViewById(R.id.tv_classes_missed);
        classesMissedProgress = fragmentView.findViewById(R.id.pb_classes_missed);
        classMissRemain = fragmentView.findViewById(R.id.tv_miss_remain);

        classPrevious = fragmentView.findViewById(R.id.tv_last_class);
        classNext = fragmentView.findViewById(R.id.tv_next_class);
        loadDetailsProgressBar = fragmentView.findViewById(R.id.progress_bar_load_details);

        className = fragmentView.findViewById(R.id.tv_class_name);
        classGroup = fragmentView.findViewById(R.id.tv_class_group);

        classGrades = fragmentView.findViewById(R.id.rv_discipline_grades);

        cardViewClassesDay = fragmentView.findViewById(R.id.class_day_and_time_card);
        classDayAndTime = fragmentView.findViewById(R.id.rv_class_time);

        if (detailsGroup == null) {
            detailsGroup = new SagresClassGroup(null, null, null, null, null, null);
            System.out.println("It's null.. it needs something to hold it back");
        }
        if (!detailsGroup.isDraft()) fragmentView.findViewById(R.id.draft_card).setVisibility(View.GONE);

        classCredits.setText(getString(R.string.class_details_credits, detailsGroup.isDraft() ? details.getCredits() : detailsGroup.getCredits()));
        classPeriod.setText(getString(R.string.class_details_class_period, detailsGroup.isDraft() ? "???" : detailsGroup.getClassPeriod()));
        classTeacher.setText(getString(R.string.class_details_teacher, detailsGroup.isDraft() ? "???" : detailsGroup.getTeacher()));
        classDepartment.setText(detailsGroup.isDraft() ? getString(R.string.class_details_department, "???") : detailsGroup.getDepartment());
        classMissLimit.setText(getString(R.string.class_details_miss_limit, detailsGroup.isDraft() ? "???" : detailsGroup.getMissLimit()));
        classesMissed.setText(getString(R.string.class_details_classes_missed, details.getMissedClasses()));

        String fullName = details.getCode() + " - " + details.getName();
        className.setText(fullName);
        if (detailsGroup != null) classGroup.setText(detailsGroup.getType());
        else classGroup.setText("???");

        int missed = 0;
        int maxAllowed = 1;
        boolean visible = true;
        try {
            missed = Integer.parseInt(details.getMissedClasses());
            if (detailsGroup != null && detailsGroup.getMissLimit() != null && !detailsGroup.getMissLimit().trim().isEmpty()) maxAllowed = Integer.parseInt(detailsGroup.getMissLimit());
            else maxAllowed = Integer.parseInt(details.getCredits())/4;

            int remains = maxAllowed - missed;
            if (remains > (maxAllowed / 4) + 1 && remains <= (maxAllowed / 2)) {
                classAlertImage.setImageResource(R.drawable.ic_tag_faces_neutral_black_24dp);
            } else if (remains <= (maxAllowed / 4) + 1 && remains >= 0)
                classAlertImage.setImageResource(R.drawable.ic_tag_faces_sad_black_24dp);
            else if (remains < 0)
                classAlertImage.setImageResource(R.drawable.ic_tag_faces_dead_black_24dp);

            classMissLimit.setText(getString(R.string.class_details_miss_limit, detailsGroup.isDraft() ? maxAllowed+"" : detailsGroup.getMissLimit()));
        } catch (Exception ignored){
            ignored.printStackTrace();
            visible = false;
        }

        int remains = maxAllowed - missed;

        if (!visible) classesMissedProgress.setVisibility(View.INVISIBLE);
        else classesMissedProgress.setProgress((missed*100)/maxAllowed);

        if (remains > 0) classMissRemain.setText(getString(R.string.class_details_miss_remain, visible ? (remains + "") : "???"));
        else if (remains == 0) classMissRemain.setText(getString(R.string.class_details_can_not_miss_anymore));
        else classMissRemain.setText(getString(R.string.class_details_failed_by_lack));

        classPrevious.setText(getString(R.string.class_details_previous_class, details.getLastClass()));
        classNext.setText(getString(R.string.class_details_next_class, details.getNextClass()));


        SagresGrade grade = null;
        if (details != null) grade = SagresProfile.getCurrentProfile().getGradesOfClass(details.getCode(), details.getSemester());
        classGrades.setLayoutManager(new LinearLayoutManager(getContext()));
        classGrades.setAdapter(new GradesAdapter(getContext(), grade));
        classGrades.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        classGrades.setNestedScrollingEnabled(false);


        if (details != null && detailsGroup != null && !detailsGroup.isDraft()) {
            cardViewClassesDay.setVisibility(View.VISIBLE);
            classDayAndTime.setLayoutManager(new LinearLayoutManager(getContext()));
            classDayAndTime.setAdapter(new DaysAndClassesAdapter(getContext(), detailsGroup.getClassTimeList()));
            classDayAndTime.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            classDayAndTime.setNestedScrollingEnabled(false);
        } else {
            cardViewClassesDay.setVisibility(View.GONE);
        }

        setHasOptionsMenu(true);
        svRoot.setFocusable(false);
        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.class_details, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        svRoot.smoothScrollTo(0,0);
    }

    private void refreshClass() {
        if (refreshing) return;
        Log.i(APP_TAG, "Refresh");
        Utils.fadeIn(loadDetailsProgressBar, getContext());

        Runnable runnable = () -> {
            refreshing = true;
            List<SagresClassDetails> classDetails = SagresFullClassParser.loginConnectAndGetClassesDetails(semester, classCode, group, false);
            SagresProfile.getCurrentProfile().updateClassDetails(classDetails);
            Log.i(APP_TAG, "Finished... Start Interface Update");
            updateViewWithNewInfo();
            refreshing = false;
        };

        SagresPortalSDK.getExecutor().execute(runnable);
    }

    private void updateViewWithNewInfo() {
        if(isDetached() || isRemoving() || !isAdded()) {
            Log.i(APP_TAG, "Is finishing, it will not recreate");
            return;
        }

        //SagresClassDetails details = SagresProfile.getCurrentProfile().getClassDetailsWithParams(classCode, semester);

        getActivity().runOnUiThread(() -> {
            loadDetailsProgressBar.setVisibility(View.GONE);
            getActivity().recreate();
        });
    }

    private void findDesiredClass() {
        if (details != null)
            return;
        if (getActivity() == null) {
            details = null;
            return;
        }
        if (getActivity().getIntent() == null) {
            details = null;
            return;
        }
        if (getActivity().getIntent().getExtras() == null) {
            details = null;
            return;
        }

        Bundle bundle = getActivity().getIntent().getExtras();
        semester = bundle.getString(SEMESTER_KEY);
        classCode = bundle.getString(CLASS_CODE_KEY);
        group = bundle.getString(GROUP_KEY);

        if (semester == null || classCode == null) {
            return;
        }

        details = SagresProfile.getCurrentProfile().getClassDetailsWithParams(classCode, semester);
        findDesiredGroup(group);
    }

    private void findDesiredGroup(String group) {
        if (details == null)
            return;

        if (details.getGroups() == null)
            return;

        if (group == null && details.getGroups().size() > 0) {
            detailsGroup = details.getGroups().get(0);
            return;
        }

        if (details.getGroups().size() == 1) {
            detailsGroup = details.getGroups().get(0);
            return;
        }

        for (SagresClassGroup classGroup : details.getGroups()) {
            if (classGroup.getType() == null && details.getGroups().size() == 1) {
                this.detailsGroup = classGroup;
                return;
            }

            if (classGroup.getType().equalsIgnoreCase(group)) {
                this.detailsGroup = classGroup;
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            refreshClass();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void scrollToTop() {
        /*
        if (svRoot != null) {
            new Handler(Looper.getMainLooper()).postDelayed(
                    ()->svRoot.smoothScrollTo(0, 0),
                    100);
        }
        */
    }
}
