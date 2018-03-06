package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
@ForeignKey(entity = ADisciplineGroup.class, parentColumns = "uid", childColumns = "group", onUpdate = CASCADE, onDelete = CASCADE)
public class ADisciplineClassItem {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int groupId;
    private int number;
    private String situation;
    private String subject;
    private String date;
    private int numberOfMaterials;

    public ADisciplineClassItem(int groupId, int number, String situation, String subject, String date, int numberOfMaterials) {
        this.groupId = groupId;
        this.number = number;
        this.situation = situation;
        this.subject = subject;
        this.date = date;
        this.numberOfMaterials = numberOfMaterials;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberOfMaterials() {
        return numberOfMaterials;
    }

    public void setNumberOfMaterials(int numberOfMaterials) {
        this.numberOfMaterials = numberOfMaterials;
    }
}
