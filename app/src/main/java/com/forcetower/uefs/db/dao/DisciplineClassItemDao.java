package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.DisciplineClassItem;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface DisciplineClassItemDao {
    @Query("SELECT * FROM DisciplineClassItem")
    LiveData<List<DisciplineClassItem>> getAllDisciplineClassItems();

    @Query("SELECT * FROM DisciplineClassItem WHERE groupId = :groupId")
    LiveData<List<DisciplineClassItem>> getDisciplineClassItemsFromGroup(int groupId);

    @Query("SELECT * FROM DisciplineClassItem WHERE groupId = :groupId")
    List<DisciplineClassItem> getDisciplineClassItemsFromGroupDirect(int groupId);

    @Query("SELECT * FROM DisciplineClassItem WHERE groupId = :groupId AND number = :number")
    DisciplineClassItem getItemFromGroupAndNumberDirect(int groupId, int number);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertClassItem(DisciplineClassItem classItems);

    @Delete
    void deleteClassItem(DisciplineClassItem classItem);

    @Query("DELETE FROM DisciplineClassItem")
    void deleteAllDisciplineClassItems();
}