package com.example.mystoryapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.ui.signup.SignupViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}