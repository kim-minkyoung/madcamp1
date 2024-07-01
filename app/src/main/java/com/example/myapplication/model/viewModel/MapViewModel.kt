package com.example.myapplication.model.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.repository.MapRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val placesClient: PlacesClient = Places.createClient(application)

    private val _addressData = MutableLiveData<Triple<String, Double, Double>>()
    val addressData: LiveData<Triple<String, Double, Double>> = _addressData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun searchPlaceByName(placeName: String) {
        MapRepository.searchPlaceByName(
            getApplication(),
            placeName,
            placesClient,
            onSuccess = { name, latitude, longitude ->
                _addressData.postValue(Triple(name, latitude, longitude))
            }
        ) { errorMessage ->
            _errorMessage.postValue(errorMessage)
        }
    }
}
