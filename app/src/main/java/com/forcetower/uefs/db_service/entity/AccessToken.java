package com.forcetower.uefs.db_service.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by João Paulo on 29/04/2018.
 */
@Entity(tableName = "access_token")
public class AccessToken {
    @PrimaryKey(autoGenerate = true)
    private long uid;
    @SerializedName(value = "token_type")
    @ColumnInfo(name = "token_type")
    private String tokenType;
    @SerializedName(value = "expires_in")
    @ColumnInfo(name = "expires_in")
    private int expiresIn;
    @SerializedName(value = "access_token")
    @ColumnInfo(name = "access_token")
    private String accessToken;
    @SerializedName(value = "refresh_token")
    @ColumnInfo(name = "refresh_token")
    private String refreshToken;

    public AccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
