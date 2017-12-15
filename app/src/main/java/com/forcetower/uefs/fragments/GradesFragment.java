package com.forcetower.uefs.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.domain.SagresSemester;
import com.forcetower.uefs.sagres_sdk.parsers.SagresGradesParser;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class GradesFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout maskView;
    private Button downloadBtn;
    private ProgressBar progressBar;
    private TextView textCenter;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private HashMap<SagresSemester, List<SagresGrade>> grades;
    private List<String> gradesList;

    public GradesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        viewPager = view.findViewById(R.id.container);
        tabLayout = view.findViewById(R.id.tabs);
        maskView = view.findViewById(R.id.mask_view);
        downloadBtn = view.findViewById(R.id.btn_download_grades);
        progressBar = view.findViewById(R.id.progress_bar);
        textCenter = view.findViewById(R.id.text_center_not_downloaded);

        downloadBtn.setOnClickListener(downloadClick);
        if (Utils.isLollipop()) downloadBtn.setElevation(3);

        grades = SagresProfile.getCurrentProfile().getAllSemestersGrades();
        if (grades != null && !grades.isEmpty()) {
            insertGradesOnInterface();
        }

        return view;
    }

    private void insertGradesOnInterface() {
        if (getActivity() == null) {
            Log.i(APP_TAG, "Null activity. It will not place the grades");
            return;
        }

        if (!isAdded() || isDetached()) {
            Log.i(APP_TAG, "Not added ot detached. It will not place the grades");
            return;
        }

        if (grades == null) {
            Log.i(APP_TAG, "Null grades. It will not place the grades");
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
                if (Utils.isLollipop()) tabLayout.setElevation(10);

                Utils.fadeIn(tabLayout, getActivity());

                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

                Utils.fadeOut(maskView, getActivity());
                gradesList = asKeyList(grades);

                for (String semester : gradesList) {
                    TabLayout.Tab tab = tabLayout.newTab();
                    String start = semester.substring(0, 4);
                    tab.setText(start + "." + semester.substring(4));
                    tabLayout.addTab(tab);
                }

                sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
                viewPager.setAdapter(sectionsPagerAdapter);
            }
        });
    }

    private List<String> asKeyList(HashMap<SagresSemester, List<SagresGrade>> grades) {
        List<String> list = new ArrayList<>();

        if (grades != null) {
            for (SagresSemester semester : grades.keySet()) {
                list.add(semester.getName());
            }
        }

        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                try {
                    int i1 = Integer.parseInt(s.substring(0, 5));
                    int i2 = Integer.parseInt(t1.substring(0, 5));
                    if (i2 < i1)
                        return -1;
                    else
                        return 1;
                } catch (NumberFormatException e) {
                    return 1;
                }
            }
        });

        return list;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<GradeInnerFragment> fragments;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<>();
            for (String semester : gradesList) {
                fragments.add(GradeInnerFragment.newInstance(semester));
            }

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
            //String semester = gradesList.get(position);
            //return GradeInnerFragment.newInstance(semester);
        }

        @Override
        public int getCount() {
            return gradesList.size();
        }
    }

    private View.OnClickListener downloadClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    disableButtonAndShowProgress();
                    Pair<Integer, HashMap<SagresSemester, List<SagresGrade>>> pair = SagresUtility.connectAndGetGrades(null);
                    if (pair.first < 0) {
                        showOnToast(R.string.get_grades_failed);
                        enableButtonAndRemoveProgress();
                    } else {
                        if (SagresProfile.getCurrentProfile() != null) {
                            SagresProfile.getCurrentProfile().setAllSemestersGrades(pair.second);
                            SagresProfile.saveProfile();
                        }
                        gotGrades(pair.second);
                    }
                }
            };

            SagresPortalSDK.getExecutor().execute(runnable);
        }
    };

    private void disableButtonAndShowProgress() {
        if (getActivity() == null)
            return;

        if(!isAdded() || isRemoving())
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.fadeOut(downloadBtn, getActivity());
                Utils.fadeIn(progressBar, getActivity());
                textCenter.setText(R.string.downloading_grades);
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
                Utils.fadeIn(downloadBtn, getActivity());
                Utils.fadeOut(progressBar, getActivity());
                textCenter.setText(R.string.grades_not_downloaded_yet);
            }
        });
    }

    private void gotGrades(HashMap<SagresSemester, List<SagresGrade>> allSemestersGrades) {
        grades = allSemestersGrades;
        insertGradesOnInterface();
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
}
