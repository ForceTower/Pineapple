package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.TodoItem;

import java.util.List;

/**
 * Created by João Paulo on 30/12/2017.
 */
@Dao
public interface TodoItemDao {
    @Query("SELECT * FROM TodoItem")
    LiveData<List<TodoItem>> getAllTodoItems();

    @Query("SELECT * FROM TodoItem WHERE disciplineCode = :disciplineCode")
    LiveData<List<TodoItem>> getTodoForDiscipline(String disciplineCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTodoItem(TodoItem item);

    @Delete
    void deleteTodoItem(TodoItem item);

    @Query("DELETE FROM TodoItem")
    void deleteAllTodoItems();
}
