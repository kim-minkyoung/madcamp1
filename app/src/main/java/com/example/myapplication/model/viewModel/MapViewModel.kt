package com.example.myapplication.model.viewModel
import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.BuildConfig
import com.example.myapplication.model.data.Address
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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
        val kakaoApiKey = BuildConfig.kakao_rest_key

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("dapi.kakao.com")
            .addPathSegments("/v2/local/search/keyword.json")
            .addQueryParameter("query", query)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "KakaoAK $kakaoApiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _errorMessage.postValue("주소를 가져오는 데 실패했습니다.")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    try {
                        val jsonObject = JSONObject(it.string())
                        val documents = jsonObject.getJSONArray("documents")

                        if (documents.length() > 0) {
                            val firstDocument = documents.getJSONObject(0)
                            val placeName = firstDocument.getString("place_name")
                            val roadAddressName = firstDocument.optString("road_address_name", "")
                            val x = firstDocument.getDouble("x")
                            val y = firstDocument.getDouble("y")

                            _specificValue.postValue(placeName)
                            _addressData.postValue(Address(placeName, roadAddressName, y, x))
                        } else {
                            _errorMessage.postValue("해당 장소를 찾을 수 없습니다.")
                        }
                    } catch (e: Exception) {
                        _errorMessage.postValue("응답을 분석하는 데 실패했습니다.")
                    }
                } ?: run {
                    _errorMessage.postValue("응답 본문이 비어 있습니다.")
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