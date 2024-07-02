package com.example.myapplication.model.viewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _addressData = MutableLiveData<Triple<String, Double, Double>>()
    val addressData: LiveData<Triple<String, Double, Double>> = _addressData
    private val _specificValue = MutableLiveData<String>()
    val specificAddressData: LiveData<String> = _specificValue
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private var isGeocoding = false
    fun searchPlaceByName(query: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$query")
            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
            .addHeader("X-NCP-APIGW-API-KEY", "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S")
            .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                _errorMessage.postValue("Failed to get address")
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let {
                    try {
                        val jsonObject = JSONObject(it.string())
                        val addresses = jsonObject.getJSONArray("addresses")
                        if (addresses.length() > 0) {
                            for (i in 0 until addresses.length()) {
                                val addressObject = addresses.getJSONObject(i)
                                val roadAddress = addressObject.getString("roadAddress")
                                val x = addressObject.getDouble("x")
                                val y = addressObject.getDouble("y")
                                val landName = addressObject.optString("landName", "")
                                // landName이 query와 일치하는지 확인
                                if (landName.contains(query, true)) {
                                    _addressData.postValue(Triple(roadAddress, y, x))
                                    _specificValue.postValue(landName)
                                    return
                                }
                            }
                            _errorMessage.postValue("No matching address found")
                        } else {
                            _errorMessage.postValue("No address found")
                        }
                    } catch (e: Exception) {
                        _errorMessage.postValue("Failed to parse response")
                    }
                } ?: run {
                    _errorMessage.postValue("Response body is null")
                }
            }
        })
    }
    fun reverseGeocode(lat: Double, lng: Double) {
        if (isGeocoding) return  // Prevent multiple simultaneous requests
        isGeocoding = true
        val client = OkHttpClient()
        val url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=$lng,$lat&orders=roadaddr&output=json"
        val request = Request.Builder()
            .url(url)
            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
            .addHeader("X-NCP-APIGW-API-KEY", "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S")
            .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                _errorMessage.postValue("Failed to get address")
                isGeocoding = false
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let {
                    try {
                        val jsonObject = JSONObject(it.string())
                        val results = jsonObject.getJSONArray("results")
                        if (results.length() > 0) {
                            for (i in 0 until results.length()) {
                                val result = results.getJSONObject(i)
                                val region = result.getJSONObject("region")
                                val roadAddress = result.getJSONObject("land").getString("name")
                                val fullAddress = "${region.getJSONObject("area1").getString("name")} ${region.getJSONObject("area2").getString("name")} ${region.getJSONObject("area3").getString("name")} $roadAddress"
                                // Extract the specific "value" field from "addition0" under "land"
                                val land = result.getJSONObject("land")
                                val addition0 = land.optJSONObject("addition0")
                                val specificValue = addition0?.optString("value", "No value found") ?: "No value found"
                                _addressData.postValue(Triple(fullAddress, lat, lng))
                                _specificValue.postValue(specificValue)  // Post specific value to live data
                            }
                        } else {
                            _errorMessage.postValue("No address found")
                        }
                    } catch (e: Exception) {
                        _errorMessage.postValue("Failed to parse response")
                    } finally {
                        isGeocoding = false
                    }
                } ?: run {
                    _errorMessage.postValue("Response body is null")
                    isGeocoding = false
                }
            }
        })
    }
}