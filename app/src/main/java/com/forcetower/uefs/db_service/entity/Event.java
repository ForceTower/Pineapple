package com.forcetower.uefs.db_service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by João Paulo on 14/06/2018.
 */
@Entity
public class Event {
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
    @SerializedName(value = "presented_by")
    @ColumnInfo(name = "presented_by")
    @NonNull
    private String presentedBy;
    @NonNull
    private String start;
    @NonNull
    private String end;
    @NonNull
    private String location;
    @SerializedName(value = "is_free")
    @ColumnInfo(name = "is_free")
    private boolean isFree;
    private double price;

    public Event(@NonNull String name, @NonNull String subtitle, @NonNull String description, String imageUrl, @NonNull String creatorName, @NonNull String creatorUsername, int creatorId, @NonNull String presentedBy, @NonNull String start, @NonNull String end, @NonNull String location, boolean isFree, double price) {
        this.name = name;
        this.subtitle = subtitle;
        this.description = description;
        this.imageUrl = imageUrl;
        this.creatorName = creatorName;
        this.creatorUsername = creatorUsername;
        this.creatorId = creatorId;
        this.presentedBy = presentedBy;
        this.start = start;
        this.end = end;
        this.location = location;
        this.isFree = isFree;
        this.price = price;
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
    public String getPresentedBy() {
        return presentedBy;
    }

    public void setPresentedBy(@NonNull String presentedBy) {
        this.presentedBy = presentedBy;
    }

    @NonNull
    public String getStart() {
        return start;
    }

    public void setStart(@NonNull String start) {
        this.start = start;
    }

    @NonNull
    public String getEnd() {
        return end;
    }

    public void setEnd(@NonNull String end) {
        this.end = end;
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
}
