package com.forcetower.uefs.di.module;

import android.content.Context;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.db.dao.AccessDao;
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
import timber.log.Timber;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Module
public class NetworkModule {

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
    Interceptor provideHttpInterceptor() {
        return chain -> {
            Request oRequest = chain.request();
            Timber.d("oRequest.url().host(): %s", oRequest.url().host());
            if (oRequest.url().host().contains(Constants.UNES_SERVICE_URL)) {
                Headers.Builder builder = oRequest.headers().newBuilder()
                        .add("Accept", "application/json");

                Headers newHeaders = builder.build();
                Request request = chain.request().newBuilder().headers(newHeaders).build();

                return chain.proceed(request);
            }

            return chain.proceed(oRequest);
        };
    }

}
