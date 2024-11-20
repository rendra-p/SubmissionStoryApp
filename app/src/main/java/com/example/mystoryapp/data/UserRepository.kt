package com.example.mystoryapp.data

import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.RegisterResponse
import com.example.mystoryapp.data.remote.response.StoryResponse
import com.example.mystoryapp.data.remote.response.UploadResponse
import com.example.mystoryapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(private val apiService: ApiService) {
    suspend fun registerUser(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser (email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStories(): Result<StoryResponse> {
        return try {
            val response = apiService.getStories()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadStory(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Result<UploadResponse> {
        return try {
            val response = apiService.uploadStory(description, photo, lat, lon)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}