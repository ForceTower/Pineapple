package com.forcetower.uefs.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.forcetower.uefs.database.entities.ATodoItem;
import com.forcetower.uefs.database.repository.TodoItemRepository;

import java.util.List;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class TodoItemCollectionViewModel extends ViewModel {
    private TodoItemRepository repository;

    public TodoItemCollectionViewModel(TodoItemRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ATodoItem>> getTodoItems() {
        return repository.getAllTodoItems();
    }

    public void addTodoItem(ATodoItem item) {
        new AddItemTask().execute(item);
    }

    public void deleteItem(ATodoItem item) {
        new DeleteItemTask().execute(item);
    }

    private class AddItemTask extends AsyncTask<ATodoItem, Void, Void> {
        @Override
        protected Void doInBackground(ATodoItem... aTodoItems) {
            repository.insertTodoItem(aTodoItems[0]);
            return null;
        }
    }

    private class DeleteItemTask extends AsyncTask<ATodoItem, Void, Void> {
        @Override
        protected Void doInBackground(ATodoItem... aTodoItems) {
            repository.deleteTodoItem(aTodoItems[0]);
            return null;
        }
    }
}
