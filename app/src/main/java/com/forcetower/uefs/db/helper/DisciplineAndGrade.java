package com.forcetower.uefs.db.helper;

import android.arch.persistence.room.Relation;

import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.Grade;

/**
 * Created by João Paulo on 24/06/2018.
 */
public class DisciplineAndGrade extends Discipline {
    @Relation(entityColumn = "discipline", parentColumn = "uid", entity = Grade.class)
    private Grade finalGrade;

    public DisciplineAndGrade(String semester, String name, String code) {
        super(semester, name, code);
    }

    public Grade getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Grade finalGrade) {
        this.finalGrade = finalGrade;
    }
}
