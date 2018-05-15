package com.forcetower.uefs.view.connected.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DisciplineDetailsFragment extends androidx.fragment.app.Fragment implements Injectable {
    public static final String INTENT_DISCIPLINE_ID = "discipline_id";
    public static final String INTENT_DISCIPLINE_GROUP_ID = "discipline_group_id";

    @BindView(R.id.container)
    ViewPager viewPager;

    private int groupId;
    private int disciplineId;

    private ActivityController controller;

    public static Fragment getFragment(int groupId, int disciplineId) {
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_DISCIPLINE_ID, disciplineId);
        bundle.putInt(INTENT_DISCIPLINE_GROUP_ID, groupId);

        Fragment fragment = new DisciplineDetailsFragment();
        fragment.setArguments(bundle);
        //fragment.setEnterTransition(new Slide(Gravity.END));
        //fragment.setExitTransition(new Slide(Gravity.END));
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discipline_details, container, false);

        ButterKnife.bind(this, view);
        controller.changeTitle(R.string.discipline_details);
        controller.getTabLayout().setVisibility(View.VISIBLE);

        if (getArguments() != null) {
            groupId = getArguments().getInt(INTENT_DISCIPLINE_GROUP_ID, 0);
            disciplineId = getArguments().getInt(INTENT_DISCIPLINE_ID, 0);
        }

        setupPages();
        setupFragmentsIntoPager();
        return view;
    }

    private void setupFragmentsIntoPager() {
        Fragment overview = new OverviewFragment();
        Fragment grades = new GradesFragment();

        Timber.d("GroupId: %d - DisciplineId: %d", groupId, disciplineId);
        Bundle arguments = new Bundle();
        arguments.putInt(INTENT_DISCIPLINE_GROUP_ID, groupId);
        arguments.putInt(INTENT_DISCIPLINE_ID, disciplineId);

        overview.setArguments(arguments);
        grades.setArguments(arguments);

        List<Pair<String, Fragment>> list = new ArrayList<>();
        list.add(new Pair<>(getString(R.string.discipline_overview), overview));
        list.add(new Pair<>(getString(R.string.discipline_grades),   grades));

        viewPager.setAdapter(new DisciplineSectionFragmentAdapter(getChildFragmentManager(), list));
    }

    private void setupPages() {
        controller.getTabLayout().setupWithViewPager(viewPager);
        controller.getTabLayout().setTabGravity(com.google.android.material.tabs.TabLayout.GRAVITY_CENTER);
        controller.getTabLayout().setTabMode(TabLayout.MODE_SCROLLABLE);
        controller.getTabLayout().addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener(controller.getTabLayout()));
    }


    private class DisciplineSectionFragmentAdapter extends androidx.fragment.app.FragmentPagerAdapter {
        private List<Pair<String, androidx.fragment.app.Fragment>> fragments;

        DisciplineSectionFragmentAdapter(androidx.fragment.app.FragmentManager fm, @NonNull List<Pair<String, Fragment>> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position).second;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).first;
        }
    }
}
