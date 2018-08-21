package com.forcetower.uefs.db.helper;

import androidx.room.Relation;

import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.Grade;

import java.util.List;

/**
 * Created by João Paulo on 24/06/2018.
 */
public class DisciplineAndGrade extends Discipline {
    @Relation(entityColumn = "discipline", parentColumn = "uid")
    private List<Grade> finalGrade;

    public DisciplineAndGrade(String semester, String name, String code) {
        super(semester, name, code);
    }

    public List<Grade> getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(List<Grade> finalGrade) {
        this.finalGrade = finalGrade;
    }
}
