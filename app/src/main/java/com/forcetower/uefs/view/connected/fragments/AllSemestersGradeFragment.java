package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.WordUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.vm.base.GradesViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class AllSemestersGradeFragment extends androidx.fragment.app.Fragment implements Injectable {
    @BindView(R.id.view_pager)
    androidx.viewpager.widget.ViewPager viewPager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private GradesFragmentAdapter fragmentAdapter;
    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            controller = (ActivityController) context;
        } catch (ClassCastException ignored) {
            Timber.d("Activity %s must implement MainContentController", context.getClass().getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_semesters_grades, container, false);
        ButterKnife.bind(this, view);
        controller.changeTitle(R.string.title_grades);
        fragmentAdapter = new GradesFragmentAdapter(getChildFragmentManager());
        configureViewPager();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GradesViewModel gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);
        gradesViewModel.getAllSemesters().observe(this, this::onSemestersReceived);
    }

    private void onSemestersReceived(List<Semester> semesters) {
        Timber.d("Received semester list %s", semesters);

        //This is a risky call, but since we are in a lifecycle aware method, this should be fine
        com.google.android.material.tabs.TabLayout tabLayout = controller.getTabLayout();
        if (tabLayout != null) {
            tabLayout.removeAllTabs();
            tabLayout.clearOnTabSelectedListeners();
            tabLayout.setupWithViewPager(viewPager);
            viewPager.clearOnPageChangeListeners();
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        }
        fragmentAdapter.setSemesterList(semesters);
    }

    private void configureViewPager() {
        viewPager.setAdapter(fragmentAdapter);
    }

    private class GradesFragmentAdapter extends FragmentPagerAdapter {
        private List<Semester> semesterList;
        private List<SemesterGradesFragment> gradesFragmentList;

        GradesFragmentAdapter(androidx.fragment.app.FragmentManager fm) {
            super(fm);
            gradesFragmentList = new ArrayList<>();
        }

        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            return gradesFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return gradesFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return semesterList == null ?
                    super.getPageTitle(position) :
                    semesterList.get(position).getName().substring(0, 4) + "." +
                            semesterList.get(position).getName().substring(4);
        }

        void setSemesterList(List<Semester> semesterList) {
            if (this.semesterList == null) this.semesterList = new ArrayList<>();

            Collections.sort(semesterList);
            gradesFragmentList.clear();
            this.semesterList.clear();
            this.semesterList.addAll(semesterList);

            Collections.sort(this.semesterList);
            for (Semester semester : semesterList) {
                if (!WordUtils.validString(semester.getName())) {
                    Timber.d("It's a shame that this semester is invalid");
                    continue;
                }

                SemesterGradesFragment fragment = new SemesterGradesFragment();
                Bundle arguments = new Bundle();
                arguments.putString("semester", semester.getName());
                fragment.setArguments(arguments);

                gradesFragmentList.add(fragment);
            }

            notifyDataSetChanged();
        }
    }
}
