package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentRemindersAnnotationsBinding;
import com.forcetower.uefs.db.dao.TodoItemDao;
import com.forcetower.uefs.db.entity.TodoItem;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.adapters.ReminderAdapter;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.base.TodoItemViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 22/06/2018.
 */
public class RemindersFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;

    private FragmentRemindersAnnotationsBinding binding;
    private ReminderAdapter adapter;
    private TodoItemViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminders_annotations, container, false);
        prepareRecyclerView();
        binding.showCompleted.setOnCheckedChangeListener((view, value) -> onCheckChanged(value));
        return binding.getRoot();
    }

    private void prepareRecyclerView() {
        adapter = new ReminderAdapter();
        binding.rvReminders.setAdapter(adapter);
        binding.rvReminders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReminders.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TodoItemViewModel.class);
        viewModel.getSource().observe(this, this::onDataChanged);
    }

    private void onDataChanged(List<TodoItem> todoItems) {
        if (todoItems == null) return;
        adapter.setReminders(todoItems);

        if (todoItems.isEmpty()) {
            binding.noRemindersLayout.setVisibility(View.VISIBLE);
            binding.rvReminders.setVisibility(View.INVISIBLE);
        } else {
            binding.noRemindersLayout.setVisibility(View.INVISIBLE);
            binding.rvReminders.setVisibility(View.VISIBLE);
        }
    }

    private void onCheckChanged(boolean value) {
        if (value) viewModel.getAllTodoItems();
        else viewModel.getAllIncompleteTodoItems();
    }
}
