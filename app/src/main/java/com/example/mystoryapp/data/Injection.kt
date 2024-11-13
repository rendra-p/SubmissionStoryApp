package com.example.mystoryapp.data

import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import com.example.mystoryapp.ui.ViewModelFactory

object Injection {
    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository(apiService)
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        val repository = provideUserRepository()
        return ViewModelFactory(repository)
    }
}