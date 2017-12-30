package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 29/12/2017.
 * This is prob a useless class
 */
@Entity
@ForeignKey(entity = ADiscipline.class, parentColumns = "uid", childColumns = "discipline", onDelete = CASCADE, onUpdate = CASCADE)
public class AGrade {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int discipline;
    @ColumnInfo(name = "final_score")
    private String finalScore;
    @ColumnInfo(name = "partial_mean")
    private String partialMean;

    public AGrade(int discipline) {
        this.discipline = discipline;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public String getPartialMean() {
        return partialMean;
    }

    public void setPartialMean(String partialMean) {
        this.partialMean = partialMean;
    }
}
