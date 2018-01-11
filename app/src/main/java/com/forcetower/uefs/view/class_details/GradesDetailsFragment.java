package com.forcetower.uefs.view.class_details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.view.adapters.GradesAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 11/01/2018.
 */

public class GradesDetailsFragment extends Fragment {
    private static final String CLASS_CODE_KEY = "class_code";
    private static final String SEMESTER_KEY = "semester";
    private static final String GROUP_KEY = "group";

    private SagresClassDetails details;
    private SagresClassGroup detailsGroup;

    private String semester;
    private String classCode;
    private String group;

    //View References card grades
    @BindView(R.id.rv_discipline_grades)
    RecyclerView classGrades;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades_class_details, container, false);
        ButterKnife.bind(this, view);

        findDesiredClass();
        if (details == null) {
            Toast.makeText(getContext(), R.string.class_not_found_dev_error, Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return view;
        }

        SagresGrade grade = SagresProfile.getCurrentProfile().getGradesOfClass(details.getCode(), details.getSemester());
        classGrades.setLayoutManager(new LinearLayoutManager(getContext()));
        classGrades.setAdapter(new GradesAdapter(getContext(), grade));
        classGrades.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        classGrades.setNestedScrollingEnabled(false);

        if (grade != null) {
            Pair<Boolean, Double> value = grade.getCalculatedPartialMean();
            tvSituation.setText(details.getSituation() != null ? details.getSituation() : getString(R.string.situation_unknown));

            String mean = value.second.toString();
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

        return view;
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
}
