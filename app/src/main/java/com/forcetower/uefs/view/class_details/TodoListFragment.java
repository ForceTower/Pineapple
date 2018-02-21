package com.forcetower.uefs.view.class_details;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.database.entities.ATodoItem;
import com.forcetower.uefs.view.adapters.TodoListAdapter;
import com.forcetower.uefs.view_models.TodoItemCollectionViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class TodoListFragment extends Fragment {
    @BindView(R.id.rv_todo_list)
    RecyclerView rvTodoList;
    @BindView(R.id.sw_show_options)
    Switch swIncompleteFilter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    TodoItemCollectionViewModel viewModel;

    private String listCriteria;
    private List<ATodoItem> items;

    private TodoListAdapter adapter;

    private ClassDetailsCallback callback;

    public TodoListFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (ClassDetailsCallback) context;
        } catch(ClassCastException e) {
            Log.e(APP_TAG, "Must implement ClassDetailsCallback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        ButterKnife.bind(this, view);

        listCriteria = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            listCriteria = arguments.getString("discipline");
        }

        swIncompleteFilter.setOnClickListener(this::swChangedListener);

        rvTodoList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodoListAdapter(new ArrayList<>());
        adapter.setOnClickListener(clickCallbacks);
        rvTodoList.setAdapter(adapter);
        rvTodoList.setNestedScrollingEnabled(false);
        rvTodoList.setItemAnimator(new DefaultItemAnimator());
        rvTodoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                callback.onScrolled(recyclerView, dx, dy);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UEFSApplication)getActivity().getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TodoItemCollectionViewModel.class);

        viewModel.getTodoItems(listCriteria).observe(this, items -> {
            if (items != null) {
                updateItems(items);
            } else {
                this.items = null;
                this.adapter.setItems(new ArrayList<>());
            }
        });
    }

    private void updateItems(List<ATodoItem> newItems) {
        this.items = newItems;
        adapter.removeAll();
        adapter.setItems(items);
        swChangedListener(null);
    }

    private void onTodoItemClicked(View view, int position) {

    }

    private void swChangedListener(View view) {
        if (items != null) {
            List<ATodoItem> filtered = new ArrayList<>();
            adapter.removeAll();
            if (swIncompleteFilter.isChecked()) {
                for (ATodoItem item : items) {
                    if (!item.isCompleted()) {
                        filtered.add(item);
                    }
                }
                adapter.insertAll(filtered);
            } else {
                adapter.insertAll(items);
            }
        }
    }

    private OnTodoItemClickedListener clickCallbacks = new OnTodoItemClickedListener() {
        @Override
        public void onItemClicked(View view, int position) {
            onTodoItemClicked(view, position);
        }

        @Override
        public void onItemUpdated(View view, ATodoItem item, int position) {
            callback.onTodoItemUpdated(item, position);
        }

        @Override
        public void onItemDelete(View view, ATodoItem item, int position) {
            callback.onTodoItemDeleted(item, position);
        }
    };

    public interface OnTodoItemClickedListener {
        void onItemClicked(View view, int position);
        void onItemUpdated(View view, ATodoItem item, int position);
        void onItemDelete(View view, ATodoItem item, int position);
    }
}
