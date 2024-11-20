package com.example.mystoryapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.ui.login.LoginViewModel
import com.example.mystoryapp.ui.main.StoryViewModel
import com.example.mystoryapp.ui.signup.SignupViewModel
import com.example.mystoryapp.ui.upload.UploadViewModel
import com.example.mystoryapp.ui.welcome.TokenDataStore

class ViewModelFactory(
    private val repository: UserRepository,
    private val tokenDataStore: TokenDataStore? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SignupViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                LoginViewModel(repository, tokenDataStore!!) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                StoryViewModel(repository, tokenDataStore!!) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                UploadViewModel(repository, tokenDataStore!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}