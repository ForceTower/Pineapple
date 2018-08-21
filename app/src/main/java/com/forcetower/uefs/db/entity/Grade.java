package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
@ForeignKey(entity = Discipline.class, parentColumns = "uid", childColumns = "discipline", onDelete = CASCADE, onUpdate = CASCADE)
public class Grade {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int discipline;
    @ColumnInfo(name = "final_score")
    private String finalScore;
    @ColumnInfo(name = "partial_mean")
    private String partialMean;

    @Ignore
    private List<GradeSection> sections;

    @Ignore
    private String disciplineName;

    public Grade(int discipline) {
        this.discipline = discipline;
        sections = new ArrayList<>();
    }

    @Ignore
    public Grade(String disciplineName) {
        this.disciplineName = disciplineName;
        sections = new ArrayList<>();
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

    public String getDisciplineName() {
        return disciplineName;
    }

    public List<GradeSection> getSections() {
        return sections;
    }

    public void addSection(GradeSection section) {
        this.sections.add(section);
    }

    public void selectiveCopy(Grade other) {
        if (other.getFinalScore() != null && !other.getFinalScore().equalsIgnoreCase(getFinalScore()))
            setFinalScore(other.getFinalScore());

        if (other.getPartialMean() != null && !other.getPartialMean().equalsIgnoreCase(getPartialMean()))
            setPartialMean(other.getPartialMean());
    }

    @Override
    public String toString() {
        return "final score: " + finalScore + " - partial mean: " + partialMean;
    }

    public Pair<Boolean, Double> getCalculatedPartialMean() {
        if (sections == null) {
            Timber.d("Sections are null");
            return new Pair<>(false, -1d);
        } else if (sections.size() == 0) {
            Timber.d("Sections are empty");
            return new Pair<>(false, -1d);
        }

        if (sections.size() == 1) {
            List<GradeInfo> grades = sections.get(0).getGrades();
            double value = 0;
            double weightSum = 0;
            if (grades != null) {
                for (GradeInfo info : grades) {
                    double calcVal = info.getCalculatedValue();
                    if (calcVal >= 0) {
                        value += info.getCalculatedValue();
                        weightSum += info.getWeight();
                    }
                }
            }
            Timber.d("Mean Calculated: %s", value/weightSum);
            return new Pair<>(true, value/weightSum);
        } else {
            double totalValue = 0;
            for (GradeSection section : sections) {
                List<GradeInfo> grades = section.getGrades();
                double value = 0;
                double weightSum = 0;
                if (grades != null) {
                    for (GradeInfo info : grades) {
                        double calcVal = info.getCalculatedValue();
                        if (calcVal >= 0) {
                            value += info.getCalculatedValue();
                            weightSum += info.getWeight();
                        }
                    }
                }
                totalValue += value/weightSum;
            }
            Timber.d("Mean Calculated: %s", totalValue/sections.size());
            return new Pair<>(true, totalValue/sections.size());
        }
    }

    public double getPartialMeanValue() {
        if (getPartialMean() != null && !getPartialMean().equalsIgnoreCase("Não Divulgada")) {
            String partial = getPartialMean();
            partial = partial.replace(",", ".");
            if (partial.endsWith("*")) partial = partial.substring(0, partial.length() - 1);
            try {
                return Double.parseDouble(partial);
            } catch (Exception e) {
                Timber.d("Failed parsing double ate getPartialMeanValue(): %s", partial);
                return -1;
            }
        } else {
            return -1;
        }
    }

    public void setSections(List<GradeSection> sections) {
        this.sections = sections;
    }
}
