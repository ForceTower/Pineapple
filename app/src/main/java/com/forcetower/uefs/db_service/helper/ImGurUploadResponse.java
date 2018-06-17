package com.forcetower.uefs.db_service.helper;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class ImGurUploadResponse {
    private ImGurDataObject data;
    private boolean success;
    private int status;

    public ImGurUploadResponse(ImGurDataObject data, boolean success, int status) {
        this.data = data;
        this.success = success;
        this.status = status;
    }

    public ImGurDataObject getData() {
        return data;
    }

    public void setData(ImGurDataObject data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
