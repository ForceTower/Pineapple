package com.forcetower.uefs.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ADisciplineClassItem;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface ADisciplineClassItemDao {
    @Query("SELECT * FROM ADisciplineClassItem")
    List<ADisciplineClassItem> getAllDisciplineClassItems();

    @Query("SELECT * FROM ADisciplineClassItem WHERE groupId = :groupId")
    List<ADisciplineClassItem> getDisciplineClassItemsFromGroup(int groupId);

    @Insert
    void insertClassItem(ADisciplineClassItem... classItems);

    @Delete
    void deleteClassItem(ADisciplineClassItem classItem);

    @Query("DELETE FROM ADisciplineClassItem")
    void deleteAllDisciplineClassItems();
}
