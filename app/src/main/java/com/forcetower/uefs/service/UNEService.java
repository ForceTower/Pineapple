package com.forcetower.uefs.service;

import android.arch.lifecycle.LiveData;

import com.forcetower.uefs.db_service.entity.UpdateStatus;
import com.forcetower.uefs.db_service.entity.Version;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by João Paulo on 13/03/2018.
 */

public interface UNEService {
    @GET("update")
    LiveData<ApiResponse<UpdateStatus>> getUpdateStatus();

    @GET("version")
    LiveData<ApiResponse<Version>> getLatestVersion();

    @FormUrlEncoded
    @POST("update")
    LiveData<ApiResponse<ActionResult<UpdateStatus>>> changeUpdateStatus(@Field("manager") int manager, @Field("alarm") int alarm);
}
