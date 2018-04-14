package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.DisciplineClickListener;
import com.forcetower.uefs.view.connected.adapters.SemesterAdapter;
import com.forcetower.uefs.vm.DisciplinesViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class DisciplinesFragment extends Fragment implements Injectable {
    @BindView(R.id.recycler_view)
    RecyclerView rvSemesters;

    @Inject
    AppExecutors executors;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private DisciplinesViewModel disciplinesViewModel;

    private SemesterAdapter adapter;
    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disciplines, container, false);
        ButterKnife.bind(this, view);
        controller.getTabLayout().setVisibility(View.GONE);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disciplinesViewModel = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
        disciplinesViewModel.getAllDisciplines().observe(this, this::onDisciplinesReceived);
    }

    private void setupRecyclerView() {
        rvSemesters.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SemesterAdapter(getContext(), new ArrayList<>());
        adapter.setClickListener(disciplineClickListener);
        rvSemesters.setAdapter(adapter);
    }

    private void onDisciplinesReceived(List<Discipline> disciplines) {
        if (disciplines != null) {
            rvSemesters.setVisibility(View.VISIBLE);
            adapter.setDisciplines(disciplines);
        }
    }

    private DisciplineClickListener disciplineClickListener = discipline -> {
        /*disciplinesViewModel.getDisciplineGroups(discipline.getUid()).observe(this, disciplineGroups -> {*/
        executors.others().execute(() -> {
            Timber.d("Discipline name: %s - %s", discipline.getName(), discipline.getCode());
            List<DisciplineGroup> disciplineGroups = disciplinesViewModel.getDisciplineGroupsDir(discipline.getUid());
            executors.mainThread().execute(() -> {
            //noinspection ConstantConditions
            if (disciplineGroups.size() == 0) {
                Timber.d("Well that's odd");
            } else if (disciplineGroups.size() == 1) {
                Timber.d("Only one group for this discipline");
                DisciplineGroup group = disciplineGroups.get(0);
                controller.navigateToDisciplineDetails(group.getUid(), discipline.getUid());
            } else {
                Timber.d("This discipline has %d groups", disciplineGroups.size());
                Timber.d("The groups are %s", disciplineGroups);
                showSelectGroupDialog(disciplineGroups);
            }
            });
        });

        /*});*/
        Timber.d("Clicked on discipline %s", discipline.getName());
    };

    private void showSelectGroupDialog(List<DisciplineGroup> disciplineGroups) {
        AlertDialog.Builder selectDialog = new AlertDialog.Builder(requireContext());
        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_book_open_black_24dp);
        if (icon != null) {
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            selectDialog.setIcon(icon);
        } else {
            selectDialog.setIcon(R.drawable.ic_book_open_black_24dp);
        }
        selectDialog.setTitle(R.string.select_a_class);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_item);
        for (DisciplineGroup group : disciplineGroups) {
            String text = group.getGroup() != null ? group.getGroup() : "???";
            if (group.getIgnored() == 1) {
                text = getString(R.string.ignored_class_format, text);
            }
            arrayAdapter.add(text);
        }

        selectDialog.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        selectDialog.setAdapter(arrayAdapter, (dialog, which) -> {
            String strName = arrayAdapter.getItem(which);
            int position = arrayAdapter.getPosition(strName);
            DisciplineGroup group = disciplineGroups.get(position);
            controller.navigateToDisciplineDetails(group.getUid(), group.getDiscipline());
            dialog.dismiss();
        });
        executors.mainThread().execute(selectDialog::show);
    }
}
