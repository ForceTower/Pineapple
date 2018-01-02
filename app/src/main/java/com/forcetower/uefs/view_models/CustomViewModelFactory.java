package com.forcetower.uefs.view_models;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider.Factory;
import android.support.annotation.NonNull;

import com.forcetower.uefs.database.repository.CalendarRepository;
import com.forcetower.uefs.database.repository.TodoItemRepository;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class CustomViewModelFactory implements Factory{
    private final TodoItemRepository todoItemRepository;
    private final CalendarRepository calendarItemRepository;

    public CustomViewModelFactory(TodoItemRepository todoItemRepository, CalendarRepository calendarItemRepository) {
        this.todoItemRepository = todoItemRepository;
        this.calendarItemRepository = calendarItemRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TodoItemCollectionViewModel.class)) {
            return (T) new TodoItemCollectionViewModel(todoItemRepository);
        } else if (modelClass.isAssignableFrom(CalendarItemCollectionViewModel.class)) {
            return (T) new CalendarItemCollectionViewModel(calendarItemRepository);
        }

        throw new IllegalArgumentException("View model is not declared into custom factory");
    }
}
