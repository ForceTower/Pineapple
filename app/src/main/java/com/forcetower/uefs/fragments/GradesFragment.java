package com.forcetower.uefs.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.domain.SagresSemester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class GradesFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
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
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        if (Utils.isLollipop()) tabLayout.setElevation(10);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        grades = SagresProfile.getCurrentProfile().getAllSemestersGrades();
        gradesList = asKeyList(grades);

        for (String semester : gradesList) {
            TabLayout.Tab tab = tabLayout.newTab();
            String start = semester.substring(0, 4);
            tab.setText(start + "." + semester.substring(4));
            tabLayout.addTab(tab);
        }

        sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        return view;
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

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String semester = gradesList.get(position);
            return GradeInnerFragment.newInstance(semester);
        }

        @Override
        public int getCount() {
            return grades.size();
        }
    }
}
