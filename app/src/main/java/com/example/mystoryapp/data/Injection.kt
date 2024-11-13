package com.example.mystoryapp.data

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import com.example.mystoryapp.ui.ViewModelFactory
import com.example.mystoryapp.ui.welcome.TokenDataStore

object Injection {
    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository(apiService)
    }

    fun provideTokenDataStore(context: Context): TokenDataStore {
        return TokenDataStore.getInstance(context)
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        val repository = provideUserRepository()
        val tokenDataStore = provideTokenDataStore(context)
        return ViewModelFactory(repository, tokenDataStore)
    }
}