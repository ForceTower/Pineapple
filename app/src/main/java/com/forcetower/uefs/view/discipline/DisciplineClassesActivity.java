package com.forcetower.uefs.view.discipline;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.discipline.adapters.ClassesAdapter;
import com.forcetower.uefs.vm.DisciplinesViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class DisciplineClassesActivity extends UBaseActivity implements HasSupportFragmentInjector {
    public static final String INTENT_GROUP_ID = "group_id";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    private ClassesAdapter classesAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public static void startActivity(Context context, int groupId) {
        Intent intent = new Intent(context, DisciplineClassesActivity.class);
        intent.putExtra(INTENT_GROUP_ID, groupId);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_discipline_classes, savedInstanceState);

        setupToolbar();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        classesAdapter = new ClassesAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(classesAdapter);

        int groupId = getIntent().getIntExtra(INTENT_GROUP_ID, 0);

        DisciplinesViewModel disciplinesVM = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
        disciplinesVM.getDisciplineItems(groupId).observe(this, this::onItemsUpdate);
    }

    private void setupToolbar() {
        if (VersionUtils.isLollipop()) {
            appBarLayout.setElevation(10);
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.discipline_classes);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void onItemsUpdate(List<DisciplineClassItem> disciplineClassItems) {
        Timber.d("Class items update!");
        classesAdapter.setClasses(disciplineClassItems);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        Timber.i("This activity will not inject into fragments. Be aware to not call this");
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
