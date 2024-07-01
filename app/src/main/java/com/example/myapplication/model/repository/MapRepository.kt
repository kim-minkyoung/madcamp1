package com.example.myapplication.model.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient

object MapRepository {

    // 장소 이름으로 검색하는 메서드
    fun searchPlaceByName(
        context: Context,
        placesClient: PlacesClient,
        placeName: String,
        onSuccess: (String, Double, Double) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // 요청 생성
        val request = com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
            .setQuery(placeName)
            .build()

        // API 호출
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            if (predictions.isNotEmpty()) {
                val prediction = predictions[0]
                val placeId = prediction.placeId

                // 장소 ID를 이용해 장소의 상세 정보를 가져옵니다.
                fetchPlaceById(context, placesClient, placeId, onSuccess, onFailure)
            } else {
                onFailure("해당 이름의 장소를 찾을 수 없습니다.")
            }
        }.addOnFailureListener { exception ->
            Log.e("MapRepository", "Place not found: $exception")
            onFailure("장소 검색 중 오류가 발생했습니다.")
        }
    }

    // 장소 ID로 장소의 위치를 검색하는 메서드
    private fun fetchPlaceById(
        context: Context,
        placesClient: PlacesClient,
        placeId: String,
        onSuccess: (String, Double, Double) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val placeFields = listOf(
            com.google.android.libraries.places.api.model.Place.Field.ID,
            com.google.android.libraries.places.api.model.Place.Field.NAME,
            com.google.android.libraries.places.api.model.Place.Field.LAT_LNG
        )

        val request = com.google.android.libraries.places.api.net.FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val name = place.name ?: "이름 없음"
            val latLng = place.latLng ?: LatLng(0.0, 0.0)
            onSuccess(name, latLng.latitude, latLng.longitude)
        }.addOnFailureListener { exception ->
            Log.e("MapRepository", "Place not found: $exception")
            onFailure("장소 정보를 불러오는 중 오류가 발생했습니다.")
        }
    }
}
