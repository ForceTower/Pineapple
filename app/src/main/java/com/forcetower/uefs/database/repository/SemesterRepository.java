package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ASemesterDao;
import com.forcetower.uefs.database.entities.ASemester;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class SemesterRepository {
    private final ASemesterDao dao;

    public SemesterRepository(ASemesterDao dao) {
        this.dao = dao;
    }

    public ASemester getSemester(String uefsId) {
        return dao.getSemester(uefsId);
    }

    public void insertSemesters(ASemester... semesters) {
        dao.insertSemesters(semesters);
    }

    public void removeSemester(ASemester semester) {
        dao.removeSemester(semester);
    }

    public void removeAllSemesters() {
        dao.removeAllSemesters();
    }

    public List<ASemester> getAllSemesters() {
        return dao.getAllSemesters();
    }
}
