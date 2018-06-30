package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CourseVariant {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "uefs_id", index = true)
    private String uefsId;
    private String name;
    private boolean selected;

    public CourseVariant(String uefsId, String name) {
        this.uefsId = uefsId;
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUefsId() {
        return uefsId;
    }

    public void setUefsId(String uefsId) {
        this.uefsId = uefsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
