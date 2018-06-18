package com.forcetower.uefs.db_service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.forcetower.uefs.Constants;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by João Paulo on 14/06/2018.
 */
@Entity
public class Event {
    @SerializedName(value = "id")
    @PrimaryKey(autoGenerate = true)
    private int uid;
    @NonNull
    private String name;
    @NonNull
    private String subtitle;
    @NonNull
    private String description;
    @SerializedName(value = "image_url")
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    @SerializedName(value = "creator_name")
    @ColumnInfo(name = "creator_name")
    @NonNull
    private String creatorName;
    @SerializedName(value = "creator_username")
    @ColumnInfo(name = "creator_username")
    @NonNull
    private String creatorUsername;
    @SerializedName(value = "creator_id")
    @ColumnInfo(name = "creator_id")
    private int creatorId;
    @SerializedName(value = "offered_by")
    @ColumnInfo(name = "offered_by")
    @NonNull
    private String offeredBy;
    @SerializedName(value = "start_date")
    @ColumnInfo(name = "start_date")
    @NonNull
    private String startDate;
    @SerializedName(value = "start_time")
    @ColumnInfo(name = "start_time")
    @NonNull
    private String startTime;
    @Nullable
    @SerializedName(value = "end_date")
    @ColumnInfo(name = "end_date")
    private String endDate;
    @SerializedName(value = "end_time")
    @ColumnInfo(name = "end_time")
    @Nullable
    private String endTime;
    @NonNull
    private String location;
    @SerializedName(value = "is_free")
    @ColumnInfo(name = "is_free")
    private boolean isFree;
    private double price;
    @ColumnInfo(name = "inserted_at")
    private long insertedAt;
    @SerializedName(value = "has_certificate")
    @ColumnInfo(name = "has_certificate")
    private boolean hasCertificate;
    @SerializedName(value = "certificate_hours")
    @ColumnInfo(name = "certificate_hours")
    private int certificateHours;
    @SerializedName(value = "image_delete_hash")
    @ColumnInfo(name = "image_delete_hash")
    private String deleteHash;
    @SerializedName(value = "uuid")
    @ColumnInfo(name = "uuid")
    private String uuid;
    @SerializedName(value = "created_at")
    @ColumnInfo(name = "created_at")
    private String createdAt;

    public Event(@NonNull String name, @NonNull String subtitle, @NonNull String description, String imageUrl, @NonNull String creatorName, @NonNull String creatorUsername, int creatorId, @NonNull String offeredBy, @NonNull String startDate, @NonNull String startTime, String endDate, String endTime, @NonNull String location, boolean isFree, double price) {
        this.name = name;
        this.subtitle = subtitle;
        this.description = description;
        this.imageUrl = imageUrl;
        this.creatorName = creatorName;
        this.creatorUsername = creatorUsername;
        this.creatorId = creatorId;
        this.offeredBy = offeredBy;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
        this.isFree = isFree;
        this.price = price;
    }

    @Ignore
    public Event() {
        this.name = "";
        this.subtitle = "";
        this.description = "";
        this.imageUrl = "";
        this.creatorName = "";
        this.creatorUsername = "";
        this.creatorId = 0;
        this.offeredBy = "";
        this.startDate = "";
        this.startTime = "";
        this.endDate = "";
        this.endTime = "";
        this.location = "";
        this.isFree = true;
        this.price = 0;
        this.hasCertificate = false;
        this.certificateHours = 0;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(@NonNull String subtitle) {
        this.subtitle = subtitle;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(@NonNull String creatorName) {
        this.creatorName = creatorName;
    }

    @NonNull
    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(@NonNull String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    @NonNull
    public String getOfferedBy() {
        return offeredBy;
    }

    public void setOfferedBy(@NonNull String offeredBy) {
        this.offeredBy = offeredBy;
    }

    @NonNull
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(@NonNull String startDate) {
        this.startDate = startDate;
    }

    @NonNull
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull String startTime) {
        this.startTime = startTime;
    }

    @Nullable
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable String endDate) {
        this.endDate = endDate;
    }

    @Nullable
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(@Nullable String endTime) {
        this.endTime = endTime;
    }

    @NonNull
    public String getLocation() {
        return location;
    }

    public void setLocation(@NonNull String location) {
        this.location = location;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getInsertedAt() {
        return insertedAt;
    }

    public void setInsertedAt(long createdAt) {
        this.insertedAt = createdAt;
    }

    public boolean isOutdated() {
        long current = System.currentTimeMillis()/1000;
        long limit = insertedAt + Constants.OUTDATED_SECONDS;
        boolean outdated = current > limit;
        if (outdated)
            Timber.d("Event Outdated");
        else
            Timber.d("Event Up To Date");
        return outdated;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event && ((Event) obj).uid == uid;
    }

    public int getCertificateHours() {
        return certificateHours;
    }

    public void setCertificateHours(int certificateHours) {
        this.certificateHours = certificateHours;
    }

    public boolean isHasCertificate() {
        return hasCertificate;
    }

    public void setHasCertificate(boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
