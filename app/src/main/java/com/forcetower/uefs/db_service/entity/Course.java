package com.forcetower.uefs.db_service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.google.gson.annotations.SerializedName;

import timber.log.Timber;

/**
 * Created by João Paulo on 20/06/2018.
 */
@Entity
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String name;
    @ColumnInfo(name = "image_url")
    @SerializedName(value = "image_url")
    private String imageUrl;
    @ColumnInfo(name = "inserted_at")
    @SerializedName(value = "inserted_at")
    private long insertedAt;
    @ColumnInfo(name = "number_of_semesters")
    @SerializedName(value = "number_of_semesters")
    private int numberOfSemesters;
    @ColumnInfo(name = "service_id")
    @SerializedName(value = "id")
    private int serviceId;

    public Course(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(long insertedAt) {
        this.insertedAt = insertedAt;
    }

    public boolean isOutdated() {
        long current = System.currentTimeMillis()/1000;
        long limit = insertedAt + Constants.OUTDATED_SECONDS;
        boolean outdated = current > limit;
        if (outdated)
            Timber.d("Course Outdated");
        else
            Timber.d("Course Up To Date");
        return outdated;
    }

    public int getNumberOfSemesters() {
        return numberOfSemesters;
    }

    public void setNumberOfSemesters(int numberOfSemesters) {
        this.numberOfSemesters = numberOfSemesters;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public static Course getUndefined(Context ctx) {
        Course course = new Course(ctx.getString(R.string.any_course), null);
        course.setServiceId(0);
        return course;
    }

    @Override
    public String toString() {
        return name;
    }
}
