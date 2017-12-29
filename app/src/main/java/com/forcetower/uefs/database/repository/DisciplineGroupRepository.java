package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ADisciplineGroupDao;
import com.forcetower.uefs.database.entities.ADisciplineGroup;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class DisciplineGroupRepository {
    private final ADisciplineGroupDao dao;

    public DisciplineGroupRepository(ADisciplineGroupDao dao) {
        this.dao = dao;
    }

    public List<ADisciplineGroup> getAllDisciplineGroups() {
        return dao.getAllDisciplineGroups();
    }

    public List<ADisciplineGroup> getDisciplineGroups(int disciplineId) {
        return dao.getDisciplineGroups(disciplineId);
    }

    public List<ADisciplineGroup> getDisciplineGroups(String semester, String code) {
        return dao.getDisciplineGroups(semester, code);
    }

    public void insertDisciplineGroup(ADisciplineGroup... disciplineGroups) {
        dao.insertDisciplineGroup(disciplineGroups);
    }

    public void deleteDisciplineGroup(ADisciplineGroup disciplineGroup) {
        dao.deleteDisciplineGroup(disciplineGroup);
    }

    public void deleteAllDisciplineGroups() {
        dao.deleteAllDisciplineGroups();
    }
}
