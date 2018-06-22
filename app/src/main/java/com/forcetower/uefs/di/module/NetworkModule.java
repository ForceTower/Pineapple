package com.forcetower.uefs.di.module;

import android.content.Context;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.db_service.dao.AccessTokenDao;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.service.adapter.LiveDataCallAdapterFactory;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Module
public class NetworkModule {

    @Provides
    @Singleton
    UNEService provideUNEService(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Constants.UNES_SERVICE_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(client)
                .build()
                .create(UNEService.class);
    }

    @Singleton
    @Provides
    CookieHandler provideCookieHandler() {
        return new CookieManager();
    }

    @Provides
    @Singleton
    ClearableCookieJar provideCookieJar(Context context) {
        return new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
    }

    @Singleton
    @Provides
    OkHttpClient provideHttpClient(ClearableCookieJar cookieJar, Interceptor interceptor) {
        return new OkHttpClient.Builder()
                .followRedirects(true)
                .cookieJar(cookieJar)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();
    }

    @Singleton
    @Provides
    Interceptor provideHttpInterceptor(AccessTokenDao accessTokenDao) {
        return chain -> {
            Request oRequest = chain.request();
            if (oRequest.url().host().contains(Constants.UNES_SERVICE_URL)) {
                Headers.Builder builder = oRequest.headers().newBuilder()
                        .add("Accept", "application/json");

                AccessToken token = accessTokenDao.getAccessTokenDirect();
                if (token != null && token.isValid()) {
                    builder.add("Authorization", token.getTokenType() + " " + token.getAccessToken());
                }

                Headers newHeaders = builder.build();
                Request request = oRequest.newBuilder().headers(newHeaders).build();

                return chain.proceed(request);
            }

            return chain.proceed(oRequest);
        };
    }

}
