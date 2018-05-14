package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.DisciplineClassMaterialLink;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 14/05/2018.
 */
@Dao
public interface DisciplineClassMaterialLinkDao {
    @Insert(onConflict = REPLACE)
    void insert(DisciplineClassMaterialLink... materialLinks);

    @Query("SELECT * FROM DisciplineClassMaterialLink")
    LiveData<List<DisciplineClassMaterialLink>> getAllMaterials();

    @Query("SELECT * FROM DisciplineClassMaterialLink WHERE class_id = :classId")
    LiveData<List<DisciplineClassMaterialLink>> getMaterialsFromClass(int classId);

    @Query("SELECT * FROM DisciplineClassMaterialLink WHERE class_id = :classId")
    List<DisciplineClassMaterialLink> getMaterialsFromClassDirect(int classId);

    @Query("SELECT DisciplineClassMaterialLink.* FROM DisciplineClassMaterialLink, DisciplineClassItem " +
            "WHERE DisciplineClassMaterialLink.class_id = DisciplineClassItem.uid " +
            "AND DisciplineClassItem.number = :classNumber")
    List<DisciplineClassMaterialLink> getMaterialsFromClassNumberDirect(int classNumber);

    @Query("SELECT DisciplineClassMaterialLink.* FROM DisciplineClassMaterialLink, DisciplineClassItem, DisciplineGroup, Discipline " +
            "WHERE DisciplineClassMaterialLink.class_id = DisciplineClassItem.uid " +
            "AND DisciplineClassItem.groupId = DisciplineGroup.uid " +
            "AND DisciplineGroup.discipline = Discipline.uid " +
            "AND Discipline.uid = :disciplineId LIMIT 1")
    LiveData<List<DisciplineClassMaterialLink>> getMaterialsFromDiscipline(int disciplineId);

    @Query("SELECT DisciplineClassMaterialLink.* FROM DisciplineClassMaterialLink, DisciplineClassItem, DisciplineGroup " +
            "WHERE DisciplineClassMaterialLink.class_id = DisciplineClassItem.uid " +
            "AND DisciplineClassItem.groupId = DisciplineGroup.uid " +
            "AND DisciplineGroup.uid = :groupId")
    LiveData<List<DisciplineClassMaterialLink>> getMaterialsFromDisciplineGroup(int groupId);

    @Delete
    void delete(DisciplineClassMaterialLink materialLink);
}
