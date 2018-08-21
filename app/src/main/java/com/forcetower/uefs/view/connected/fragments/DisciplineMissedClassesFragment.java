package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentDisciplineMissedClassesBinding;
import com.forcetower.uefs.db.entity.DisciplineMissedClass;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.adapters.MissedClassesAdapter;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.base.DisciplinesViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 23/06/2018.
 */
public class DisciplineMissedClassesFragment extends Fragment implements Injectable {
    public static DisciplineMissedClassesFragment getFragment(int disciplineId) {
        Bundle bundle = new Bundle();
        bundle.putInt("discipline_id", disciplineId);

        DisciplineMissedClassesFragment fragment = new DisciplineMissedClassesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Inject
    UEFSViewModelFactory viewModelFactory;

    private FragmentDisciplineMissedClassesBinding binding;
    private MissedClassesAdapter adapter;
    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discipline_missed_classes, container, false);
        prepareRecyclerView();
        controller.changeTitle(R.string.discipline_missed_classes);
        controller.getTabLayout().setVisibility(View.GONE);
        return binding.getRoot();
    }

    private void prepareRecyclerView() {
        adapter = new MissedClassesAdapter();
        binding.recyclerMisses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMisses.setNestedScrollingEnabled(false);
        binding.recyclerMisses.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.recyclerMisses.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerMisses.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisciplinesViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
        if (getArguments() != null) {
            int disciplineId = getArguments().getInt("discipline_id");
            viewModel.getMissedClasses(disciplineId).observe(this, this::onMissedClassesUpdate);
        }
    }

    private void onMissedClassesUpdate(List<DisciplineMissedClass> disciplineMissedClasses) {
        if (disciplineMissedClasses.isEmpty()) {
            binding.recyclerMisses.setVisibility(View.INVISIBLE);
            binding.layoutNoData.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerMisses.setVisibility(View.VISIBLE);
            binding.layoutNoData.setVisibility(View.INVISIBLE);
        }

        adapter.setItems(disciplineMissedClasses);
    }
}
