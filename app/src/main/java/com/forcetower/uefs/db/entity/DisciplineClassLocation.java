package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
@ForeignKey(entity = DisciplineGroup.class, parentColumns = "uid", childColumns = "groupId", onUpdate = CASCADE, onDelete = CASCADE)
public class DisciplineClassLocation implements Comparable<DisciplineClassLocation> {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int groupId;
    @ColumnInfo(name = "start_time")
    private String startTime;
    @ColumnInfo(name = "end_time")
    private String endTime;
    private String day;
    private String room;
    private String campus;
    private String modulo;
    @ColumnInfo(name = "class_name")
    private String className;
    @ColumnInfo(name = "class_group")
    private String classGroup;
    @ColumnInfo(name = "class_code")
    private String classCode;

    public DisciplineClassLocation(int groupId, String startTime, String endTime, String day, String room, String campus, String modulo) {
        this.groupId = groupId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.room = room;
        this.campus = campus;
        this.modulo = modulo;
    }

    @Ignore
    public DisciplineClassLocation(String startTime, String endTime, String day, String room, String campus, String modulo, String className, String classCode, String classGroup) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.room = room;
        this.campus = campus;
        this.modulo = modulo;
        this.className = className;
        this.classCode = classCode;
        this.classGroup = classGroup;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public int compareTo(@NonNull DisciplineClassLocation o) {
        return getStartTime().compareTo(o.getStartTime());
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassGroup(String classGroup) {
        this.classGroup = classGroup;
    }

    public String getClassGroup() {
        return classGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisciplineClassLocation that = (DisciplineClassLocation) o;

        if (uid != that.uid) return false;
        if (groupId != that.groupId) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
            return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (day != null ? !day.equals(that.day) : that.day != null) return false;
        if (room != null ? !room.equals(that.room) : that.room != null) return false;
        if (campus != null ? !campus.equals(that.campus) : that.campus != null) return false;
        return modulo != null ? modulo.equals(that.modulo) : that.modulo == null;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + groupId;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        result = 31 * result + (campus != null ? campus.hashCode() : 0);
        result = 31 * result + (modulo != null ? modulo.hashCode() : 0);
        return result;
    }
}
