package com.example.mystoryapp.data

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import com.example.mystoryapp.ui.ViewModelFactory
import com.example.mystoryapp.ui.welcome.TokenDataStore

object Injection {
    private fun provideUserRepository(context: Context): UserRepository {
        val tokenDataStore = provideTokenDataStore(context)
        val apiService = ApiConfig.getApiService(tokenDataStore)
        return UserRepository(apiService)
    }

    private fun provideTokenDataStore(context: Context): TokenDataStore {
        return TokenDataStore.getInstance(context)
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        val repository = provideUserRepository(context)
        val tokenDataStore = provideTokenDataStore(context)
        return ViewModelFactory(repository, tokenDataStore)
    }
}