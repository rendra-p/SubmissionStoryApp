package com.example.mystoryapp.data.remote.retrofit

import com.example.mystoryapp.BuildConfig
import com.example.mystoryapp.ui.welcome.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getApiService(tokenDataStore: TokenDataStore): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val token = runBlocking { tokenDataStore.token.first() }

            val authRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            chain.proceed(authRequest)
        }
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}