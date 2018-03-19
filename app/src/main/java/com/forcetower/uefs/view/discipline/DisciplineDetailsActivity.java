package com.forcetower.uefs.view.discipline;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.forcetower.uefs.R;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.discipline.fragments.GradesFragment;
import com.forcetower.uefs.view.discipline.fragments.OverviewFragment;
import com.forcetower.uefs.vm.DisciplinesViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class DisciplineDetailsActivity extends UBaseActivity implements HasSupportFragmentInjector {
    public static final String INTENT_DISCIPLINE_ID = "discipline_id";
    public static final String INTENT_DISCIPLINE_GROUP_ID = "discipline_group_id";

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager viewPager;

    private int groupId;
    private int disciplineId;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private DisciplinesViewModel disciplinesViewModel;

    public static void startActivity(Context context, int groupId, int disciplineId) {
        Intent intent = new Intent(context, DisciplineDetailsActivity.class);
        intent.putExtra(INTENT_DISCIPLINE_GROUP_ID, groupId);
        intent.putExtra(INTENT_DISCIPLINE_ID, disciplineId);
        context.startActivity(intent);
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_discipline_details, savedInstanceState);
        tabLayout.setVisibility(View.VISIBLE);

        groupId = getIntent().getIntExtra(INTENT_DISCIPLINE_GROUP_ID, 0);
        disciplineId = getIntent().getIntExtra(INTENT_DISCIPLINE_ID, 0);

        disciplinesViewModel = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);

        setupToolbar();
        setupPages();
        setupFragmentsIntoPager();
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

        viewPager.setAdapter(new DisciplineSectionFragmentAdapter(getSupportFragmentManager(), list));
    }

    private void setupPages() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void setupToolbar() {
        if (VersionUtils.isLollipop()) {
            appBarLayout.setElevation(10);
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.discipline_details);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_ignore_discipline) {
            showDialogIgnore();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    private class DisciplineSectionFragmentAdapter extends FragmentPagerAdapter {
        private List<Pair<String, Fragment>> fragments;

        public DisciplineSectionFragmentAdapter(FragmentManager fm, @NonNull List<Pair<String, Fragment>> fragments) {
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

    private void showDialogIgnore() {
        Timber.d("Show dialog...");
        new AlertDialog.Builder(this)
                .setTitle(R.string.ignore_class)
                .setMessage(R.string.ignore_class_description)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    disciplinesViewModel.ignoreGroup(groupId);
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}
