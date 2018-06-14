package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;
import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
@ForeignKey(entity = DisciplineGroup.class, parentColumns = "uid", childColumns = "group", onUpdate = CASCADE, onDelete = CASCADE)
public class DisciplineClassItem {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int groupId;
    private int number;
    private String situation;
    private String subject;
    private String date;
    @ColumnInfo(name = "number_of_materials")
    private int numberOfMaterials;
    @ColumnInfo(name = "class_material_link")
    private String classMaterialLink;
    @Ignore
    private List<DisciplineClassMaterialLink> materials;

    public DisciplineClassItem(int groupId, int number, String situation, String subject, String date, int numberOfMaterials, String classMaterialLink) {
        this.groupId = groupId;
        this.number = number;
        this.situation = situation;
        this.subject = subject;
        this.date = date;
        this.numberOfMaterials = numberOfMaterials;
        this.classMaterialLink = classMaterialLink;
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

    public void selectiveCopy(DisciplineClassItem other) {
        if (validString(other.subject)   || subject == null)    subject     = other.subject;
        if (validString(other.situation) || situation == null)  situation   = other.situation;
        if (validString(other.date)      || date == null)       date        = other.date;
        if (other.numberOfMaterials != -1) numberOfMaterials = other.numberOfMaterials;
        if (validString(other.classMaterialLink) || classMaterialLink == null) classMaterialLink = other.classMaterialLink;
    }

    public String getClassMaterialLink() {
        return classMaterialLink;
    }

    public void setClassMaterialLink(String classMaterialLink) {
        this.classMaterialLink = classMaterialLink;
    }

    public List<DisciplineClassMaterialLink> getMaterials() {
        return materials;
    }

    public void setMaterials(List<DisciplineClassMaterialLink> materials) {
        this.materials = materials;
    }
}
