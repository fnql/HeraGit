package com.cookandroid.heragit

import android.provider.SyncStateContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class typeAccess

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class typeApi

    @Provides
    fun provideGithubUrl() = SyncStateContract.Constants.getGithubUrl()

    @Provides
    fun provideGithubApiUrl() = SyncStateContract.Constants.getGithubApiUrl()

    @Singleton
    @Provides
    @typeAccess
    fun provideAccessOkHttpClient() = if (BuildConfig.DEBUG) {
        // Debug에서는 로깅
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    @typeApi
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        // Debug에서는 로깅
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    private val interceptor = Interceptor { chain ->
        val token:String? = LoginController.getAccessToken()
        val req = chain.request()
            .newBuilder()
            .addHeader("Authorization", "token $token")
            .build()
        chain.proceed(req)
    }

    @Singleton
    @Provides
    @typeAccess
    fun provideAccessRetrofit(@typeAccess okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideGithubUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    @typeAccess
    fun provideAccessService(@typeAccess retrofit: Retrofit): AccessService {
        return retrofit.create(AccessService::class.java)
    }

    @Singleton
    @Provides
    @typeApi
    fun provideApiRetrofit(@typeApi okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(provideGithubApiUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @typeApi
    fun provideApiService(@typeApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @typeAccess
    fun provideAccessRepository(@typeAccess accessService: AccessService)= AccessRepository(accessService)

    @Singleton
    @Provides
    @typeApi
    fun provideApiRepository(@typeApi apiService: ApiService)= ApiRepository(apiService)

}