package com.example.myapplication.model.repository

import android.content.Context
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object MapRepository {

    private const val NAVER_MAPS_CLIENT_ID = "w86eyz5x78" // 네이버 지도 API 클라이언트 ID
    private const val NAVER_MAPS_CLIENT_SECRET = "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S" // 네이버 지도 API 클라이언트 시크릿

    // 장소 이름으로 검색하는 메서드
    fun searchPlaceByName(
        context: Context,
        placeName: String,
        placesClient: PlacesClient,
        onSuccess: (String, Double, Double) -> Unit,
        onFailure: (String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(placeName, StandardCharsets.UTF_8.toString())
                val apiUrl = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=$encodedQuery"

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_MAPS_CLIENT_ID)
                connection.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_MAPS_CLIENT_SECRET)

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var inputLine: String?

                    while (reader.readLine().also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                    reader.close()

                    val json = JSONObject(response.toString())
                    val items = json.getJSONArray("places")
                    if (items.length() > 0) {
                        val item = items.getJSONObject(0)
                        val name = item.getString("name")
                        val latitude = item.getDouble("y")
                        val longitude = item.getDouble("x")

                        // Update UI thread with LiveData
                        launch(Dispatchers.Main) {
                            onSuccess(name, latitude, longitude)
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            onFailure("No place found")
                        }
                    }
                } else {
                    launch(Dispatchers.Main) {
                        onFailure("HTTP Error: $responseCode")
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onFailure(e.message ?: "Unknown error")
                }
            }
        }
    }
}