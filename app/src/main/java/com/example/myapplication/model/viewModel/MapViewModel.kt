package com.example.myapplication.model.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.repository.MapRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _addressData = MutableLiveData<Triple<String, Double, Double>>()
    val addressData: LiveData<Triple<String, Double, Double>> = _addressData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun searchPlaceByName(placeName: String) {
        MapRepository.searchPlaceByName(
            context = getApplication(),
            placeName = placeName,
            onSuccess = { roadAddress, x, y ->
                Log.d(TAG, "검색 성공 - 도로명 주소: $roadAddress, 좌표: ($x, $y)")
                _addressData.postValue(Triple(roadAddress, x, y))
            },
            onFailure = { errorMessage ->
                Log.e(TAG, "검색 실패: $errorMessage")
                _errorMessage.postValue(errorMessage)
            }
        )
    }
}
