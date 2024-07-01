package com.example.myapplication.model.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.repository.MapRepository

class MapViewModel : ViewModel() {

    private val repository = MapRepository

    private val _addressData = MutableLiveData<Triple<String, Double, Double>>()
    val addressData: LiveData<Triple<String, Double, Double>> get() = _addressData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun searchAddress(address: String) {
        repository.searchAddress(
            address,
            onSuccess = { roadAddress, latitude, longitude ->
                _addressData.postValue(Triple(roadAddress, latitude, longitude))
            },
            onError = { message ->
                _errorMessage.postValue(message)
            }
        )
    }
}