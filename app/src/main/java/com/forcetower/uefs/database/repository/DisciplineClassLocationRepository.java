package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ADisciplineClassLocationDao;
import com.forcetower.uefs.database.entities.ADisciplineClassLocation;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class DisciplineClassLocationRepository {
    private final ADisciplineClassLocationDao dao;

    public DisciplineClassLocationRepository(ADisciplineClassLocationDao dao) {
        this.dao = dao;
    }

    public List<ADisciplineClassLocation> getAllDisciplineClassesLocations() {
        return dao.getAllDisciplineClassesLocations();
    }

    public List<ADisciplineClassLocation> getDisciplineClassLocations(int groupId) {
        return dao.getDisciplineClassLocations(groupId);
    }

    public List<ADisciplineClassLocation> getClassesFromSemester(String semester_uefs_id) {
        return dao.getClassesFromSemester(semester_uefs_id);
    }

    public void insertClassLocation(ADisciplineClassLocation... classLocations) {
        dao.insertClassLocation(classLocations);
    }

    public void deleteClassLocation(ADisciplineClassLocation classLocation) {
        dao.deleteClassLocation(classLocation);
    }

    public void deleteAllDisciplineClassLocations() {
        dao.deleteAllDisciplineClassLocations();
    }
}
