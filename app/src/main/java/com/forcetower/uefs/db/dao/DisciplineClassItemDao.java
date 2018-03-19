package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
    void insertClassItem(DisciplineClassItem... classItems);

    @Delete
    void deleteClassItem(DisciplineClassItem classItem);

    @Query("DELETE FROM DisciplineClassItem")
    void deleteAllDisciplineClassItems();
}