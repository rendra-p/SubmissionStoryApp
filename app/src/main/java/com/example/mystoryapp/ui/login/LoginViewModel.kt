package com.example.mystoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.ui.welcome.TokenDataStore
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository, private val tokenDataStore: TokenDataStore) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.loginUser (email, password)
                _loginResult.value = result

                result.onSuccess { response ->
                    response.loginResult?.token?.let { token ->
                        tokenDataStore.saveToken(token)
                    }
                }
            } catch (e: Exception) {
                // Tangani error login
                val errorMessage = when {
                    e is HttpException && e.code() == 401 -> {
                        "Invalid email or password"
                    }
                    else -> e.message ?: "An unknown error occurred"
                }
                _loginResult.value = Result.failure(Exception(errorMessage))
            } finally {
                _isLoading.value = false
            }
        }
    }
}