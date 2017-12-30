package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
public class ASemester {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "uefs_id")
    private String uefsId;
    private String name;

    public ASemester(String uefsId, String name) {
        this.uefsId = uefsId;
        this.name = name;
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    @Override
    public String toString() {
        return name + " " + uefsId;
    }
}
