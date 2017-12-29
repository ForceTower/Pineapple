package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.AGradeDao;
import com.forcetower.uefs.database.entities.AGrade;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class GradeRepository {
    private final AGradeDao dao;

    public GradeRepository(AGradeDao dao) {
        this.dao = dao;
    }

    public List<AGrade> getAllGrades() {
        return dao.getAllGrades();
    }

    public AGrade getDisciplineGrades(int disciplineId) {
        return dao.getDisciplineGrades(disciplineId);
    }

    public AGrade getDisciplineGrades(String code, String semester) {
        return dao.getDisciplineGrades(code, semester);
    }

    public void insertGrade(AGrade... grades) {
        dao.insertGrade(grades);
    }

    public void deleteGrade(AGrade grade) {
        dao.deleteGrade(grade);
    }

    public void deleteAllGrades() {
        dao.deleteAllGrades();
    }
}
