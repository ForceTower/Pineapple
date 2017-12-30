package com.forcetower.uefs.view.class_details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.forcetower.uefs.R;
import com.forcetower.uefs.database.entities.ATodoItem;
import com.forcetower.uefs.helpers.MockUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class TodoListFragment extends Fragment {
    @BindView(R.id.fab_add_new_task)
    FloatingActionButton fabAddTask;
    @BindView(R.id.rv_todo_list)
    RecyclerView rvTodoList;
    @BindView(R.id.sw_show_options)
    Switch swIncompleteFilter;

    private String listCriteria;
    private TodoListAdapter adapter;

    public TodoListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        ButterKnife.bind(this, view);

        listCriteria = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            listCriteria = arguments.getString("list");
        }

        swIncompleteFilter.setOnClickListener(this::swChangedListener);

        List<ATodoItem> todoList = MockUtils.getTodoList();
        rvTodoList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodoListAdapter(todoList);
        adapter.setOnClickListener(this::onTodoItemClicked);
        rvTodoList.setAdapter(adapter);
        rvTodoList.setNestedScrollingEnabled(false);
        rvTodoList.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        rvTodoList.setItemAnimator(new DefaultItemAnimator());

        rvTodoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) fabAddTask.hide();
                else if (dy < 0) fabAddTask.show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void onTodoItemClicked(View view, int position) {
        Log.d(APP_TAG, "Position clicked: " + position);
    }

    private void swChangedListener(View view) {
        List<ATodoItem> items = MockUtils.getTodoList();
        if(swIncompleteFilter.isChecked()) {
            for (int i = items.size()-1; i >= 0; i--) {
                if (items.get(i).isCompleted()) {
                    adapter.removeItem(i);
                }
            }
        } else {
            adapter.setItems(items);
        }
    }

    public interface OnTodoItemClickedListener {
        void onItemClicked(View view, int position);
    }
}
