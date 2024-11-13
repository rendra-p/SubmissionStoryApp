package com.example.mystoryapp.data

import com.example.mystoryapp.data.remote.response.RegisterResponse
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
}