package com.forcetower.uefs.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.adapters.ui.AllGradesAdapter;
import com.forcetower.uefs.adapters.ui.GradesAdapter;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import java.util.List;

/**
 * Created by João Paulo on 02/12/2017.
 */

public class GradeInnerFragment extends Fragment{
    private static final String SEMESTER_KEY = "semester";
    private RecyclerView rv_all_grades;
    private List<SagresGrade> grades;
    private String semester = null;
    private AllGradesAdapter gradesAdapter;

    public GradeInnerFragment() {}

    public static GradeInnerFragment newInstance(String semester) {
        GradeInnerFragment fragment = new GradeInnerFragment();
        Bundle args = new Bundle();
        args.putString(SEMESTER_KEY, semester);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades_of_semester, container, false);
        rv_all_grades = view.findViewById(R.id.rv_all_grades);

        if (getArguments() != null) {
            semester = getArguments().getString(SEMESTER_KEY);
            grades = SagresProfile.getCurrentProfile().getGradesOfSemester(semester);
            fillWithGrades(grades);
        } else if (semester != null) {
            grades = SagresProfile.getCurrentProfile().getGradesOfSemester(semester);
            fillWithGrades(grades);
        } else {
            Log.i(Constants.APP_TAG, "No arguments, semester is null... Skipped");
        }

        return view;
    }

    private void fillWithGrades(List<SagresGrade> grades) {
        if (grades == null || getContext() == null) {
            Log.i(Constants.APP_TAG, "Returned because Ctx: " + getContext() + ". grs: " + grades);
            return;
        }

        gradesAdapter = new AllGradesAdapter(getContext(), grades);
        rv_all_grades.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_all_grades.setAdapter(gradesAdapter);
        rv_all_grades.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rv_all_grades.setNestedScrollingEnabled(false);
    }
}
