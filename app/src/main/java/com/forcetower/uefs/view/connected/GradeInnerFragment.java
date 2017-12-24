package com.forcetower.uefs.view.connected;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.domain.SagresSemester;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;
import com.forcetower.uefs.view.adapters.AllGradesAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 02/12/2017.
 */

public class GradeInnerFragment extends Fragment {
    private static final String SEMESTER_KEY = "semester";
    private RecyclerView rv_all_grades;
    private RelativeLayout mask_download_semester;
    private View sv_view_root;
    private Button btnDownload;
    private ProgressBar progressBar;
    private TextView textView;
    private List<SagresGrade> grades;
    private LinearLayoutManager layoutManager;
    private String semester = null;
    private AllGradesAdapter gradesAdapter;
    private GradesFragment callback;

    public GradeInnerFragment() {}

    public static GradeInnerFragment newInstance(String semester) {
        GradeInnerFragment fragment = new GradeInnerFragment();
        Bundle args = new Bundle();
        args.putString(SEMESTER_KEY, semester);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades_of_semester, container, false);
        rv_all_grades = view.findViewById(R.id.rv_all_grades);
        sv_view_root = view.findViewById(R.id.sv_view_root);
        mask_download_semester = view.findViewById(R.id.mask_download_semester);
        btnDownload = view.findViewById(R.id.btn_download_semester_grades);
        progressBar = view.findViewById(R.id.progress_bar_semester);
        textView = view.findViewById(R.id.semester_grades_not_downloaded);

        if (getArguments() != null) {
            semester = getArguments().getString(SEMESTER_KEY);
            grades = SagresProfile.getCurrentProfile().getGradesOfSemester(semester);
            fillWithGrades(grades);
        } else if (semester != null) {
            grades = SagresProfile.getCurrentProfile().getGradesOfSemester(semester);
            fillWithGrades(grades);
        } else {
            Log.i(APP_TAG, "No arguments, semester is null... Skipped");
        }

        btnDownload.setOnClickListener(btnDownloadClick);

        return view;
    }

    private void fillWithGrades(List<SagresGrade> grades) {
        if (grades == null || getContext() == null) {
            Log.i(APP_TAG, "Returned because Ctx: " + getContext() + ". grs: " + grades);
            return;
        }

        if (!grades.isEmpty()) {
            rv_all_grades.setVisibility(View.VISIBLE);
            sv_view_root.setVisibility(View.VISIBLE);
            mask_download_semester.setVisibility(View.INVISIBLE);

            gradesAdapter = new AllGradesAdapter(getContext(), grades);
            layoutManager = new LinearLayoutManager(getContext());
            rv_all_grades.setLayoutManager(layoutManager);
            rv_all_grades.setAdapter(gradesAdapter);
            rv_all_grades.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            rv_all_grades.setNestedScrollingEnabled(false);
        } else {
            rv_all_grades.setVisibility(View.INVISIBLE);
            sv_view_root.setVisibility(View.INVISIBLE);
            mask_download_semester.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener btnDownloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (semester == null) {
                Toast.makeText(view.getContext(), R.string.this_is_and_error, Toast.LENGTH_SHORT).show();
                return;
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    disableButtonAndShowProgress();
                    Pair<Integer, HashMap<SagresSemester, List<SagresGrade>>> pair = SagresUtility.connectAndGetGrades(semester);
                    if (pair.first < 0) {
                        showOnToast(R.string.get_grades_failed);
                        enableButtonAndRemoveProgress();
                    } else {
                        for (Map.Entry<SagresSemester, List<SagresGrade>> entry : pair.second.entrySet()) {
                            if (entry.getKey().getName().equalsIgnoreCase(semester)) {
                                updateInterfaceWithInformation(entry.getValue());
                                if (SagresProfile.getCurrentProfile() != null) {
                                    SagresProfile.getCurrentProfile().setGradesOfSemester(entry);
                                    SagresProfile.saveProfile();
                                }
                                return;
                            }
                        }
                    }
                }
            };

            SagresPortalSDK.getExecutor().execute(runnable);
        }
    };

    private void updateInterfaceWithInformation(final List<SagresGrade> grades) {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillWithGrades(grades);
            }
        });
    }

    private void showOnToast(@StringRes final int id) {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), id, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableButtonAndRemoveProgress() {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.fadeIn(btnDownload, getActivity());
                Utils.fadeOut(progressBar, getActivity());
                textView.setText(R.string.semester_grades_not_downloaded);
            }
        });
    }

    private void disableButtonAndShowProgress() {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.fadeOut(btnDownload, getActivity());
                Utils.fadeIn(progressBar, getActivity());
                textView.setText(R.string.downloading_grades);
            }
        });
    }

    public void masterReference(GradesFragment gradesFragment) {
        callback = gradesFragment;
    }

    public interface GradesFragmentCallback {
        void isOnTop();
        void notOnTop();
    }
}
