package com.example.myapplication.model.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.repository.MapRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _addressData = MutableLiveData<Triple<String, Double, Double>>()
    val addressData: LiveData<Triple<String, Double, Double>> = _addressData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    private val client = OkHttpClient()

    fun searchPlaceByName(query: String) {
        val request = Request.Builder()
            .url("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$query")
            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
            .addHeader("X-NCP-APIGW-API-KEY", "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("MapViewModel", "Failed to get address: ${e.message}")
                _errorMessage.postValue("Failed to get address")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("MapViewModel", "Request failed: ${response.message}")
                    _errorMessage.postValue("Request failed")
                    return
                }

                response.body?.let {
                    try {
                        val jsonObject = JSONObject(it.string())
                        val addresses = jsonObject.getJSONArray("addresses")
                        if (addresses.length() > 0) {
                            val addressObject = addresses.getJSONObject(0)
                            val roadAddress = addressObject.getString("roadAddress")
                            val x = addressObject.getDouble("x")
                            val y = addressObject.getDouble("y")
                            Log.d("MapViewModel", "Address found: $roadAddress, x: $x, y: $y")
                            _addressData.postValue(Triple(roadAddress, y, x))
                        } else {
                            Log.e("MapViewModel", "No address found")
                            _errorMessage.postValue("No address found")
                        }
                    } catch (e: Exception) {
                        Log.e("MapViewModel", "Failed to parse response: ${e.message}")
                        _errorMessage.postValue("Failed to parse response")
                    }
                } ?: run {
                    Log.e("MapViewModel", "Response body is null")
                    _errorMessage.postValue("Response body is null")
                }
            }
        })
    }

    fun searchPlaceByCoordinates(query: String) {
        val request = Request.Builder()
            .url("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$query")
            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
            .addHeader("X-NCP-APIGW-API-KEY", "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("MapViewModel", "Failed to get address: ${e.message}")
                _errorMessage.postValue("Failed to get address")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("MapViewModel", "Request failed: ${response.message}")
                    _errorMessage.postValue("Request failed")
                    return
                }

                response.body?.let {
                    try {
                        val jsonObject = JSONObject(it.string())
                        val addresses = jsonObject.getJSONArray("addresses")
                        if (addresses.length() > 0) {
                            val addressObject = addresses.getJSONObject(0)
                            val roadAddress = addressObject.getString("roadAddress")
                            val x = addressObject.getDouble("x")
                            val y = addressObject.getDouble("y")
                            Log.d("MapViewModel", "Address found: $roadAddress, x: $x, y: $y")
                            _addressData.postValue(Triple(roadAddress, y, x))
                        } else {
                            Log.e("MapViewModel", "No address found")
                            _errorMessage.postValue("No address found")
                        }
                    } catch (e: Exception) {
                        Log.e("MapViewModel", "Failed to parse response: ${e.message}")
                        _errorMessage.postValue("Failed to parse response")
                    }
                } ?: run {
                    Log.e("MapViewModel", "Response body is null")
                    _errorMessage.postValue("Response body is null")
                }
            }
        })

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
}}





//package com.example.myapplication.model.viewModel
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
//import java.io.IOException
//
//class MapViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val _addressData = MutableLiveData<Triple<String, Double, Double>>()
//    val addressData: LiveData<Triple<String, Double, Double>> = _addressData
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> = _errorMessage
//
//    fun searchPlaceByName(query: String) {
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$query")
//            .addHeader("X-NCP-APIGW-API-KEY-ID", "YOUR_CLIENT_ID")
//            .addHeader("X-NCP-APIGW-API-KEY", "YOUR_CLIENT_SECRET")
//            .build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                _errorMessage.postValue("Failed to get address")
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                response.body?.let {
//                    try {
//                        val jsonObject = JSONObject(it.string())
//                        val addresses = jsonObject.getJSONArray("addresses")
//                        if (addresses.length() > 0) {
//                            val addressObject = addresses.getJSONObject(0)
//                            val roadAddress = addressObject.getString("roadAddress")
//                            val x = addressObject.getDouble("x")
//                            val y = addressObject.getDouble("y")
//                            _addressData.postValue(Triple(roadAddress, y, x))
//                        } else {
//                            _errorMessage.postValue("No address found")
//                        }
//                    } catch (e: Exception) {
//                        _errorMessage.postValue("Failed to parse response")
//                    }
//                } ?: run {
//                    _errorMessage.postValue("Response body is null")
//                }
//            }
//        })
//    }
//}
//
//
