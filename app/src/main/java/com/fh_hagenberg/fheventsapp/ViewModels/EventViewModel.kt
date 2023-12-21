package com.fh_hagenberg.fheventsapp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fh_hagenberg.fheventsapp.API.Interfaces.Repository
import com.fh_hagenberg.fheventsapp.API.Models.EventModel
import kotlinx.coroutines.launch

class EventViewModel(private val repository: Repository) : ViewModel() {

    private val _events = MutableLiveData<List<EventModel>>()
    val events: LiveData<List<EventModel>> get() = _events

    private val _eventLoadError = MutableLiveData<String>()
    val eventLoadError: LiveData<String> get() = _eventLoadError

    fun loadEvents() {
        viewModelScope.launch {
            try {
                val eventsList = repository.getEvents()
                _events.value = eventsList
            } catch (e: Exception) {
                _eventLoadError.value = "Error loading events: ${e.message}"
            }
        }
    }

    fun saveEvent(event: EventModel) {
        viewModelScope.launch {
            val result = repository.saveEvent(event)
            if (!result.success) {
                // Handle save error if needed
            }
        }
    }
}