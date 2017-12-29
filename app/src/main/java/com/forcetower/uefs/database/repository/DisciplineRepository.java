package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ADisciplineDao;
import com.forcetower.uefs.database.entities.ADiscipline;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class DisciplineRepository {
    private final ADisciplineDao dao;

    public DisciplineRepository(ADisciplineDao dao) {
        this.dao = dao;
    }

    public List<ADiscipline> getAllClasses() {
        return dao.getAllClasses();
    }

    public List<ADiscipline> getClassesFromSemester(String semester) {
        return dao.getClassesFromSemester(semester);
    }

    public List<ADiscipline> getDisciplinesByCode(String code) {
        return dao.getDisciplinesByCode(code);
    }

    public List<ADiscipline> getDisciplinesBySemesterAndCode(String semester, String code) {
        return dao.getDisciplinesBySemesterAndCode(semester, code);
    }

    public void insertDiscipline(ADiscipline... disciplines) {
        dao.insertDiscipline(disciplines);
    }

    public void deleteDiscipline(ADiscipline discipline) {
        dao.deleteDiscipline(discipline);
    }

    public void deleteAllDisciplines() {
        dao.deleteAllDisciplines();
    }
}
