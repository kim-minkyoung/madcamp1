package com.example.myapplication.model.viewModel
import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.model.data.Address
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val _addressData = MutableLiveData<Address>()
    val addressData: LiveData<Address> = _addressData
    private val _specificValue = MutableLiveData<String>()
    val specificAddressData: LiveData<String> = _specificValue
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private var isGeocoding = false

    private val _selectedAddress = MutableLiveData<String>()
    val selectedAddress: LiveData<String>
        get() = _selectedAddress

    private val _navigateToAddress = MutableLiveData<Pair<Double, Double>>()
    val navigateToAddress: LiveData<Pair<Double, Double>>
        get() = _navigateToAddress
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
//                                if (landName.contains(query, true)) {
//                                    // landName이 일치하는 경우에만 값을 post
//                                    _specificValue.postValue(landName)
//                                    _addressData.postValue(Address(landName, roadAddress, y, x))
//                                    return
//                                } else {
//                                    _addressData.postValue(Address(null, roadAddress, y, x))
//                                }
                                _addressData.postValue(Address(null, roadAddress, y, x))
                            }
//                            _errorMessage.postValue("No matching address found")
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
                                val specificValue = addition0?.optString("value", null)
                                _specificValue.postValue(specificValue)  // Post specific value to live data
                                _addressData.postValue(Address(specificValue, fullAddress, lat, lng))
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

    fun onAddressClicked(address: Address) {
        if (isGeocoding) return  // Prevent multiple simultaneous requests
        isGeocoding = true

        val client = OkHttpClient()
        val encodedQuery = URLEncoder.encode(address.roadAddress, "UTF-8")
        val url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$encodedQuery"
        val request = Request.Builder()
            .url(url)
            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
            .addHeader("X-NCP-APIGW-API-KEY", "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                _errorMessage.postValue("Failed to geocode address")
                isGeocoding = false
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let {
                    try {
                        val jsonResponse = it.string()
                        val jsonObject = JSONObject(jsonResponse)
                        val addresses = jsonObject.getJSONArray("addresses")
                        if (addresses.length() > 0) {
                            val addressObject = addresses.getJSONObject(0)
                            val lat = addressObject.getDouble("y")
                            val lng = addressObject.getDouble("x")

                            // Move map camera to the coordinates


                            // Show marker on the map
//                            showMarkerOnMap(lat, lng, address)
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

    fun setAddressData(roadAddress: String, latitude: Double, longitude: Double) {
        _addressData.value = Address(null, roadAddress, latitude, longitude)
    }


}