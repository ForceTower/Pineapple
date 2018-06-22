package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentRemindersAnnotationsBinding;
import com.forcetower.uefs.db.entity.TodoItem;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.SwipeToDeleteCallback;
import com.forcetower.uefs.view.connected.ActivityController;
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
    private boolean checked = false;
    private List<TodoItem> items;

    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminders_annotations, container, false);

        if (controller.getTabLayout() != null) controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_reminders);

        binding.btnAddReminder.setOnClickListener(v -> createReminder());

        prepareRecyclerView();
        binding.showCompleted.setOnCheckedChangeListener((view, value) -> onCheckChanged(value));
        return binding.getRoot();
    }

    private void prepareRecyclerView() {
        adapter = new ReminderAdapter();
        binding.rvReminders.setAdapter(adapter);
        binding.rvReminders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReminders.setItemAnimator(new DefaultItemAnimator());
        binding.rvReminders.setNestedScrollingEnabled(false);

        swipeActionsSupport();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            checked = savedInstanceState.getBoolean("checked", false);
        }

        binding.showCompleted.setChecked(checked);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TodoItemViewModel.class);
        viewModel.getSource().observe(this, this::onDataChanged);
    }

    private void onDataChanged(List<TodoItem> todoItems) {
        if (todoItems == null) return;
        adapter.setReminders(todoItems);
        items = todoItems;

        if (todoItems.isEmpty()) {
            binding.noRemindersLayout.setVisibility(View.VISIBLE);
            binding.rvReminders.setVisibility(View.GONE);
        } else {
            binding.noRemindersLayout.setVisibility(View.GONE);
            binding.rvReminders.setVisibility(View.VISIBLE);
        }
    }

    private void onCheckChanged(boolean value) {
        if (value) viewModel.getAllTodoItems();
        else viewModel.getAllIncompleteTodoItems();
    }

    private void createReminder() {
        CreateReminderFragment createReminder = new CreateReminderFragment();
        createReminder.show(getChildFragmentManager(), createReminder.getTag());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("checked", checked);
    }

    private void swipeActionsSupport() {
        SwipeToDeleteCallback callback = new SwipeToDeleteCallback(requireContext()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TodoItem item = items.get(position);

                viewModel.deleteTodoItem(item);
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(binding.rvReminders);
    }
}
