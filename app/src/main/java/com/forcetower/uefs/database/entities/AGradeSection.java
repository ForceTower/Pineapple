package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
@ForeignKey(entity = ADiscipline.class, parentColumns = "uid", childColumns = "discipline", onDelete = CASCADE, onUpdate = CASCADE)
public class AGradeSection {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int discipline;
    private String name;
    @ColumnInfo(name = "partial_mean")
    private String partialMean;

    public AGradeSection(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartialMean() {
        return partialMean;
    }

    public void setPartialMean(String partialMean) {
        this.partialMean = partialMean;
    }

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }
}
