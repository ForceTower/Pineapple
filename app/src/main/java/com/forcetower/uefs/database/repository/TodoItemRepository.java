package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ATodoItemDao;
import com.forcetower.uefs.database.entities.ATodoItem;

import java.util.List;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class TodoItemRepository {
    private final ATodoItemDao dao;

    public TodoItemRepository(ATodoItemDao dao) {
        this.dao = dao;
    }

    public List<ATodoItem> getAllTodoItems() {
        return dao.getAllTodoItems();
    }

    public List<ATodoItem> getTodoForDiscipline(String disciplineCode) {
        return dao.getTodoForDiscipline(disciplineCode);
    }

    public long insertTodoItem(ATodoItem item) {
        return dao.insertTodoItem(item);
    }

    public void deleteTodoItem(ATodoItem item) {
        dao.deleteTodoItem(item);
    }

    public void deleteAllTodoItems() {
        dao.deleteAllTodoItems();
    }
}
