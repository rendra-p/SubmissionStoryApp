package com.example.mystoryapp.data.remote.retrofit

import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.RegisterResponse
import com.example.mystoryapp.data.remote.response.StoryResponse
import com.example.mystoryapp.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): UploadResponse
}