package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 14/05/2018.
 */
@Entity
@ForeignKey(entity = DisciplineClassItem.class, parentColumns = "uid", childColumns = "class_id", onUpdate = CASCADE, onDelete = CASCADE)
public class DisciplineClassMaterialLink {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "class_id")
    private int classId;
    private String name;
    private String link;

    public DisciplineClassMaterialLink(int classId, String name, String link) {
        this.classId = classId;
        this.name = name;
        this.link = link;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DisciplineClassMaterialLink) {
            DisciplineClassMaterialLink d = (DisciplineClassMaterialLink) obj;
            return uid == d.uid || d.name.equalsIgnoreCase(name) && d.link.equalsIgnoreCase(link) && d.classId == classId;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\tLink: " + link;
    }

    public void selectiveCopy(DisciplineClassMaterialLink other) {
        if (validString(other.link) || link == null) link = other.link;
        if (validString(other.name) || name == null) name = other.name;
    }
}
