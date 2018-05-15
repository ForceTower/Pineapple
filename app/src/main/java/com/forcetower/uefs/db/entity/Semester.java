package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
public class Semester implements Comparable<Semester>{
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @ColumnInfo(name = "uefs_id")
    private String uefsId;
    private String name;

    public Semester(String uefsId, String name) {
        this.uefsId = uefsId;
        this.name = name;
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

    public String getUefsId() {
        return uefsId;
    }

    public void setUefsId(String uefsId) {
        this.uefsId = uefsId;
    }

    @Override
    public int compareTo(@NonNull Semester o) {
        try {
            String o1 = getName();
            String o2 = o.getName();
            int str1 = Integer.parseInt(o1.substring(0, 5));
            int str2 = Integer.parseInt(o2.substring(0, 5));

            if (str1 == str2) {
                if (o1.length() > 5) return -1;
                return 1;
            } else {
                return Integer.compare(str1, str2) * -1;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Semester getCurrentSemester(List<Semester> semesters) {
        if (semesters == null ||semesters.isEmpty())
            return new Semester("0", "20181");
        Collections.sort(semesters);
        return semesters.get(0);
    }
}
