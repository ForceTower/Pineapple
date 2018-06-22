package com.forcetower.uefs.vm.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.AppExecutors;
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
    private final AppExecutors executors;

    private LiveData<List<TodoItem>> currentSource;
    private int state;

    @Inject
    TodoItemViewModel(TodoItemDao dao, AppExecutors executors) {
        this.todoItemDao = dao;
        this.executors = executors;
        this.source = new MediatorLiveData<>();
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

    public void createTodoItem(String title, String message, String date, boolean hasLimit) {
        executors.others().execute(() -> {
            TodoItem item = new TodoItem(null, title, date, hasLimit);
            item.setMessage(message);
            todoItemDao.insertTodoItem(item);
        });
    }

    public void markTodoItemCompleted(TodoItem item) {
        executors.others().execute(() -> {
            item.setCompleted(!item.isCompleted());
            todoItemDao.insertTodoItem(item);
        });
    }

    public void deleteTodoItem(TodoItem item) {
        executors.others().execute(() -> todoItemDao.deleteTodoItem(item));
    }
}
