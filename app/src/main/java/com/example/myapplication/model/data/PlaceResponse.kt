package com.example.myapplication.model.data
import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName("status") val status: String,
    @SerializedName("meta") val meta: Meta,
    @SerializedName("addresses") val addresses: List<Address>
)

data class Meta(
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("count") val count: Int
)

data class Address(
    @SerializedName("road_address") val roadAddress: String?,
    @SerializedName("jibun_address") val jibunAddress: String?,
    @SerializedName("x") val longitude: Double,
    @SerializedName("y") val latitude: Double,
    @SerializedName("address_name") val addressName: String
)