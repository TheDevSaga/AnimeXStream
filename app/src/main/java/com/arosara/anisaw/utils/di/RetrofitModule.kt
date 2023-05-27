package com.arosara.anisaw.utils.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.arosara.anisaw.BuildConfig
import com.arosara.anisaw.utils.preference.Preference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Qualifier
    annotation class HttpBodyDebugger

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        //TODO Remove RXAdapter factory & Move to Coroutines
        sharedPreferences: Preference,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(sharedPreferences.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .build()
    }

    @Provides
    fun provideDebugOkHttpClient(@HttpBodyDebugger interceptor: HttpLoggingInterceptor): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder().addInterceptor(interceptor).build()
        } else {
            OkHttpClient.Builder().build()
        }
    }

    @Provides
    @HttpBodyDebugger
    fun provideFullLogInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    fun provideRxJavaFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }
}
