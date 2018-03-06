package com.forcetower.uefs.dependency_injection.module;

import com.forcetower.uefs.content.UNEService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by João Paulo on 27/02/2018.
 */
@Module
public class ServicesModule {
    private final UNEService service;

    public ServicesModule() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.232/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(UNEService.class);
    }

    @Singleton
    @Provides
    UNEService providesUnesService() {
        return service;
    }
}
