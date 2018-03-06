package com.forcetower.uefs.content;

import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by João Paulo on 27/02/2018.
 */

public interface UNEService {

    @POST("user/information")
    Call<Void> sendInformation(@Body SagresProfile profile);

    @GET("experiment/class_review")
    Call<Void> getInformation();
}
