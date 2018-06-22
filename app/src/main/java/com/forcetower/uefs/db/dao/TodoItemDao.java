package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.TodoItem;

import java.util.List;

/**
 * Created by João Paulo on 30/12/2017.
 */
@Dao
public interface TodoItemDao {
    @Query("SELECT * FROM TodoItem")
    LiveData<List<TodoItem>> getAllTodoItems();

    @Query("SELECT * FROM TodoItem WHERE completed = 0")
    LiveData<List<TodoItem>> getAllIncompleteTodoItems();

    @Query("SELECT * FROM TodoItem WHERE completed = 1")
    LiveData<List<TodoItem>> getAllCompleteTodoItems();

    @Query("SELECT * FROM TodoItem WHERE disciplineCode = :disciplineCode")
    LiveData<List<TodoItem>> getTodoForDiscipline(String disciplineCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTodoItem(TodoItem item);

    @Delete
    void deleteTodoItem(TodoItem item);

    @Query("DELETE FROM TodoItem")
    void deleteAllTodoItems();
}
