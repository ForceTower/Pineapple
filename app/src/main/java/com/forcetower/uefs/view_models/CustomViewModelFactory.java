package com.forcetower.uefs.view_models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.support.annotation.NonNull;

import com.forcetower.uefs.database.repository.TodoItemRepository;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class CustomViewModelFactory implements Factory{
    private final TodoItemRepository todoItemRepository;

    public CustomViewModelFactory(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TodoItemCollectionViewModel.class)) {
            return (T) new TodoItemCollectionViewModel(todoItemRepository);
        }

        throw new IllegalArgumentException("View model is not declared into custom factory");
    }
}
