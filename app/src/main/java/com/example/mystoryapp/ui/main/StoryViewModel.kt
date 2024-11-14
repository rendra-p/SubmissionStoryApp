package com.example.mystoryapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.UserRepository
import com.example.mystoryapp.data.remote.response.ListStoryItem
import com.example.mystoryapp.ui.welcome.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoryViewModel(
    private val repository: UserRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ambil token dari DataStore
                val token = tokenDataStore.token.first()

                // Pastikan token tidak null
                token?.let {
                    val result = repository.getStories()
                    result.onSuccess { response ->
                        _stories.value = response.listStory
                    }
                    result.onFailure {
                        // Handle error
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            } finally {
                _isLoading.value = false
            }
        }
    }
}