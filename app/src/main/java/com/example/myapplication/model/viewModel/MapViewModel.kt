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

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

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
                            val addressObject = addresses.getJSONObject(0)
                            val roadAddress = addressObject.getString("roadAddress")
                            val x = addressObject.getDouble("x")
                            val y = addressObject.getDouble("y")
                            _addressData.postValue(Triple(roadAddress, y, x))
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
        val client = OkHttpClient()
        val url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=$lng,$lat&orders=addr&output=json"
        val request = Request.Builder()
            .url(url)
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
                        val results = jsonObject.getJSONArray("results")
                        if (results.length() > 0) {
                            val resultObject = results.getJSONObject(0)
                            val region = resultObject.getJSONObject("region")
                            val area1 = region.getJSONObject("area1")
                            val area2 = region.getJSONObject("area2")
                            val area3 = region.getJSONObject("area3")
                            val address = "${area1.getString("name")} ${area2.getString("name")} ${area3.getString("name")}"
                            _addressData.postValue(Triple(address, lat, lng))
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
}


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
//import android.util.Log
//
//class MapViewModel(application: Application) : AndroidViewModel(application) {
//
//    // 주소 데이터를 저장하기 위한 MutableLiveData
//    private val _addressData = MutableLiveData<Triple<String, Double, Double>>()
//
//    // 주소 데이터를 외부에서 관찰할 수 있도록 LiveData로 노출
//    val addressData: LiveData<Triple<String, Double, Double>> = _addressData
//
//    // 에러 메시지를 저장하기 위한 MutableLiveData
//    private val _errorMessage = MutableLiveData<String>()
//
//    // 에러 메시지를 외부에서 관찰할 수 있도록 LiveData로 노출
//    val errorMessage: LiveData<String> = _errorMessage
//
//    // OkHttpClient 인스턴스 생성
//    private val client = OkHttpClient()
//
//    // 장소 이름으로 검색하는 함수
//    fun searchPlaceByName(query: String) {
//        // 요청을 빌드
//        val request = Request.Builder()
//            .url("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$query")
//            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
//            .addHeader("X-NCP-APIGW-API-KEY", "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S")
//            .build()
//
//        // 비동기 네트워크 호출
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                // 네트워크 요청 실패 시 로그 출력 및 에러 메시지 업데이트
//                Log.e("MapViewModel", "Failed to get address: ${e.message}")
//                _errorMessage.postValue("Failed to get address")
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                if (!response.isSuccessful) {
//                    // 응답이 성공적이지 않을 경우 로그 출력 및 에러 메시지 업데이트
//                    Log.e("MapViewModel", "Request failed: ${response.message}")
//                    _errorMessage.postValue("Request failed")
//                    return
//                }
//
//                response.body?.let {
//                    try {
//                        // 응답 본문을 문자열로 변환
//                        val responseBody = it.string()
//                        Log.d("MapViewModel", "Response: $responseBody") // 응답 로그 추가
//                        // 응답 문자열을 JSON 객체로 변환
//                        val jsonObject = JSONObject(responseBody)
//                        // 주소 배열 추출
//                        val addresses = jsonObject.getJSONArray("addresses")
//                        if (addresses.length() > 0) {
//                            // 첫 번째 주소 객체 추출
//                            val addressObject = addresses.getJSONObject(0)
//                            val roadAddress = addressObject.getString("roadAddress")
//                            val x = addressObject.getDouble("x")
//                            val y = addressObject.getDouble("y")
//                            Log.d("MapViewModel", "Address found: $roadAddress, x: $x, y: $y")
//                            // 주소 데이터를 MutableLiveData에 업데이트
//                            _addressData.postValue(Triple(roadAddress, y, x))
//                        } else {
//                            // 주소가 없는 경우 로그 출력 및 에러 메시지 업데이트
//                            Log.e("MapViewModel", "No address found")
//                            _errorMessage.postValue("No address found")
//                        }
//                    } catch (e: Exception) {
//                        // 응답 파싱 실패 시 로그 출력 및 에러 메시지 업데이트
//                        Log.e("MapViewModel", "Failed to parse response: ${e.message}")
//                        _errorMessage.postValue("Failed to parse response")
//                    }
//                } ?: run {
//                    // 응답 본문이 null인 경우 로그 출력 및 에러 메시지 업데이트
//                    Log.e("MapViewModel", "Response body is null")
//                    _errorMessage.postValue("Response body is null")
//                }
//            }
//        })
//    }
//
//    // 좌표로 장소 검색하는 함수
//    fun searchPlaceByCoordinates(query: String) {
//        val request = Request.Builder()
//            .url("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=$query&output=json")
//            .addHeader("X-NCP-APIGW-API-KEY-ID", "w86eyz5x78")
//            .addHeader("X-NCP-APIGW-API-KEY", "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S")
//            .build()
//
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                Log.e("MapViewModel", "Failed to get address: ${e.message}")
//                _errorMessage.postValue("Failed to get address")
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                if (!response.isSuccessful) {
//                    Log.e("MapViewModel", "Request failed: ${response.message}")
//                    _errorMessage.postValue("Request failed")
//                    return
//                }
//
//                response.body?.let {
//                    val responseBody = it.string()
//                    Log.d("MapViewModel", "Response: $responseBody")
//                    try {
//                        val jsonObject = JSONObject(responseBody)
//                        val results = jsonObject.getJSONArray("results")
//                        if (results.length() > 0) {
//                            val resultObject = results.getJSONObject(0)
//                            val region = resultObject.getJSONObject("region")
//                            val area1 = region.getJSONObject("area1").getString("name")
//                            val area2 = region.getJSONObject("area2").getString("name")
//                            val address = "$area1 $area2"
//                            val x = resultObject.getJSONObject("point").getDouble("x")
//                            val y = resultObject.getJSONObject("point").getDouble("y")
//                            Log.d("MapViewModel", "Address found: $address, x: $x, y: $y")
//                            _addressData.postValue(Triple(address, y, x))
//                        } else {
//                            Log.e("MapViewModel", "No address found in the response")
//                            _errorMessage.postValue("No address found")
//                        }
//                    } catch (e: Exception) {
//                        Log.e("MapViewModel", "Failed to parse response: ${e.message}")
//                        _errorMessage.postValue("Failed to parse response")
//                    }
//                } ?: run {
//                    Log.e("MapViewModel", "Response body is null")
//                    _errorMessage.postValue("Response body is null")
//                }
//            }
//        })
//    }
//}
//
//
//
