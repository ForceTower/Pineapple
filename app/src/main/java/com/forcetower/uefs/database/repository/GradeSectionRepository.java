package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.AGradeSectionDao;
import com.forcetower.uefs.database.entities.AGradeSection;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class GradeSectionRepository {
    private final AGradeSectionDao dao;

    public GradeSectionRepository(AGradeSectionDao dao) {
        this.dao = dao;
    }

    public List<AGradeSection> getAllSections() {
        return dao.getAllSections();
    }

    public List<AGradeSection> getSectionsFromDiscipline(int disciplineId) {
        return dao.getSectionsFromDiscipline(disciplineId);
    }

    public List<AGradeSection> getSectionsFromDiscipline(String semester, String code) {
        return dao.getSectionsFromDiscipline(semester, code);
    }

    public void insertGradeSection(AGradeSection... gradeSection) {
        dao.insertGradeSection(gradeSection);
    }

    public void deleteGradeSection(AGradeSection gradeSection) {
        dao.deleteGradeSection(gradeSection);
    }

    public void deleteAllGradeSections() {
        dao.deleteAllGradeSections();
    }
}
