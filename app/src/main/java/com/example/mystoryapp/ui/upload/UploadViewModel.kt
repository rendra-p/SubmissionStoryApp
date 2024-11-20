package com.example.mystoryapp.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.data.remote.response.UploadResponse
import com.example.mystoryapp.ui.welcome.TokenDataStore
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(
    private val repository: UserRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {
    private val _uploadResult = MutableLiveData<Result<UploadResponse>>()
    val uploadResult: LiveData<Result<UploadResponse>> = _uploadResult

    fun uploadStory(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        viewModelScope.launch {
            _uploadResult.value = repository.uploadStory(description, photo, lat, lon)
        }
    }
}