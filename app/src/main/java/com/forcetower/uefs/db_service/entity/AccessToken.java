package com.forcetower.uefs.db_service.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.forcetower.uefs.util.WordUtils;
import com.google.gson.annotations.SerializedName;

import timber.log.Timber;

/**
 * Created by João Paulo on 29/04/2018.
 */
@Entity
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
    private boolean expired;
    @ColumnInfo(name = "created_at")
    private long createdAt;

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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isValid() {
        return WordUtils.validString(accessToken) && WordUtils.validString(tokenType);
    }

    public boolean isTimeExpired() {
        long currentTime = System.currentTimeMillis() / 1000;
        long expireDate = createdAt + expiresIn;
        Timber.d("CurrentTime: " + currentTime + " ExpireTime: " + expireDate + " ExpiredAcc? " + (currentTime > expireDate) + " Expired? " + expired);
        return currentTime > expireDate || expired;
    }
}
