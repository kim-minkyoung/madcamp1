package com.example.myapplication.model.interfaces

import com.example.myapplication.model.data.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapService {
    @GET("/map-place/v1/search")
    fun searchPlace(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Query("query") query: String
    ): Call<PlaceResponse>
}