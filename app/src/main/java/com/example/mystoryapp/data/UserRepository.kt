package com.example.mystoryapp.data

import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.RegisterResponse
import com.example.mystoryapp.data.remote.response.StoryResponse
import com.example.mystoryapp.data.remote.retrofit.ApiService

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
}