package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.AGradeInfoDao;
import com.forcetower.uefs.database.entities.AGradeInfo;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class GradeInfoRepository {
    private final AGradeInfoDao dao;

    public GradeInfoRepository(AGradeInfoDao dao) {
        this.dao = dao;
    }

    public List<AGradeInfo> getAllGradeInfos() {
        return dao.getAllGradeInfos();
    }

    public List<AGradeInfo> getGradesFromSection(int sectionId) {
        return dao.getGradesFromSection(sectionId);
    }

    public void insertGradeInfo(AGradeInfo... gradesInfo) {
        dao.insertGradeInfo(gradesInfo);
    }

    public void deleteGradeInfo(AGradeInfo gradeInfo) {
        dao.deleteGradeInfo(gradeInfo);
    }

    public void deleteAllGradesInfo() {
        dao.deleteAllGradesInfo();
    }
}
