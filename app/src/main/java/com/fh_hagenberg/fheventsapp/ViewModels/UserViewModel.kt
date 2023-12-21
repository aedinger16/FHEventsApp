package com.fh_hagenberg.fheventsapp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fh_hagenberg.fheventsapp.API.Interfaces.Repository
import com.fh_hagenberg.fheventsapp.API.UserModel
import kotlinx.coroutines.launch

class UserViewModel(private val repository: Repository) : ViewModel() {

    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> get() = _user

    private val _userLoadError = MutableLiveData<String>()
    val userLoadError: LiveData<String> get() = _userLoadError

    fun loadUser(userId: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUser(userId)
                _user.value = user
            } catch (e: Exception) {
                _userLoadError.value = "Error loading user: ${e.message}"
            }
        }
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            val result = repository.saveUser(user)
            if (!result.success) {
                // Handle save error if needed
            }
        }
    }
}