package com.forcetower.uefs.service;

import android.arch.lifecycle.LiveData;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by João Paulo on 13/03/2018.
 */

public interface UNEService {
    @FormUrlEncoded
    @POST("/unes_update")
    LiveData<ApiResponse<SimpleResponse>> setupMasterSync(@Field("state") boolean state, @Field("password") String password);

    @GET("/unes_update")
    LiveData<ApiResponse<SyncResponse>> getSyncState();
}
