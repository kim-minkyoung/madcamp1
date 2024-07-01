package com.example.myapplication.model.repository

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object MapRepository {
    private val client = OkHttpClient()

    fun searchAddress(address: String, onSuccess: (String, Double, Double) -> Unit, onError: (String) -> Unit) {
        val url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$address"

        val request = Request.Builder()
            .url(url)
            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
            .addHeader("X-NCP-APIGW-API-KEY", "yq4vrhypcs")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                onError("검색 실패: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let {
                    try {
                        val jsonObject = JSONObject(it)
                        if (jsonObject.has("addresses")) {
                            val addresses = jsonObject.getJSONArray("addresses")
                            if (addresses.length() > 0) {
                                val firstAddress = addresses.getJSONObject(0)
                                val roadAddress = firstAddress.getString("roadAddress")
                                val latitude = firstAddress.getDouble("y")
                                val longitude = firstAddress.getDouble("x")
                                onSuccess(roadAddress, latitude, longitude)
                            } else {
                                onError("검색 결과가 없습니다.")
                            }
                        } else {
                            onError("주소 정보를 찾을 수 없습니다.")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        onError("검색 결과 파싱 실패")
                    }
                } ?: onError("응답 본문이 비어 있습니다.")
            }
        })
    }
}
