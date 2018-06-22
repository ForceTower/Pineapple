package com.forcetower.uefs.vm.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.db.dao.TodoItemDao;
import com.forcetower.uefs.db.entity.TodoItem;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by João Paulo on 22/06/2018.
 */
public class TodoItemViewModel extends ViewModel {
    private final TodoItemDao todoItemDao;
    private final MediatorLiveData<List<TodoItem>> source;
    private LiveData<List<TodoItem>> currentSource;
    private int state;

    @Inject
    TodoItemViewModel(TodoItemDao dao) {
        this.todoItemDao = dao;
        source = new MediatorLiveData<>();
        state = -1;
        getAllIncompleteTodoItems();
    }

    public void getAllIncompleteTodoItems() {
        if (state == 1) return;
        if (currentSource != null) source.removeSource(currentSource);
        currentSource = todoItemDao.getAllIncompleteTodoItems();
        source.addSource(currentSource, source::postValue);
        state = 1;
    }

    public void getAllTodoItems() {
        if (state == 0) return;
        if (currentSource != null) source.removeSource(currentSource);
        currentSource = todoItemDao.getAllTodoItems();
        source.addSource(currentSource, source::postValue);
        state = 0;
    }

    public LiveData<List<TodoItem>> getSource() {
        return source;
    }
}
