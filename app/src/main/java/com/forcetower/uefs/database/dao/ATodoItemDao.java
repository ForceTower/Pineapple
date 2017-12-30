package com.forcetower.uefs.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ATodoItem;

import java.util.List;

/**
 * Created by João Paulo on 30/12/2017.
 */
@Dao
public interface ATodoItemDao {
    @Query("SELECT * FROM ATodoItem")
    LiveData<List<ATodoItem>> getAllTodoItems();

    @Query("SELECT * FROM ATodoItem WHERE disciplineCode = :disciplineCode")
    LiveData<List<ATodoItem>> getTodoForDiscipline(String disciplineCode);

    @Insert
    long insertTodoItem(ATodoItem item);

    @Delete
    void deleteTodoItem(ATodoItem item);

    @Query("DELETE FROM ATodoItem")
    void deleteAllTodoItems();
}
