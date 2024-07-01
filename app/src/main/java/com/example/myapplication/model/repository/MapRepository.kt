package com.example.myapplication.model.repository

import android.content.Context
import android.util.Log
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

    private const val TAG = "MapRepository"
    private const val NAVER_MAPS_CLIENT_ID = "w86eyz5x78" // 네이버 지도 API 클라이언트 ID
    private const val NAVER_MAPS_CLIENT_SECRET = "09NHCAmjUFBTKHMuGiXvO4oY3CRYALgN5ywWfk8S" // 네이버 지도 API 클라이언트 시크릿

    // 장소 이름으로 검색하는 메서드
    fun searchPlaceByName(
        context: Context,
        placeName: String,
        onSuccess: (String, Double, Double) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d(TAG, "장소 이름으로 검색 시작 - 장소명: $placeName")

        // 검색할 주소를 인코딩
        val encodedQuery = URLEncoder.encode(placeName, StandardCharsets.UTF_8.toString())

        // API 호출 URL
        val apiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$encodedQuery"
        Log.d(TAG, "API 호출 URL: $apiUrl")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // HTTP 연결 설정
                val url = URL(apiUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_MAPS_CLIENT_ID)
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_MAPS_CLIENT_SECRET)

                // 응답 코드 확인
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "API 호출 성공 - HTTP 상태 코드: ${conn.responseCode}")

                    // 응답 데이터 읽기
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // JSON 파싱
                    val jsonObject = JSONObject(response.toString())
                    val addresses = jsonObject.getJSONArray("addresses")
                    if (addresses.length() > 0) {
                        val firstAddress = addresses.getJSONObject(0)
                        val roadAddress = firstAddress.getString("roadAddress")
                        val x = firstAddress.getDouble("x")
                        val y = firstAddress.getDouble("y")

                        Log.d(TAG, "주소 정보 파싱 성공 - 도로명 주소: $roadAddress, 좌표: ($x, $y)")

                        // 성공 콜백 호출
                        onSuccess(roadAddress, x, y)
                    } else {
                        onFailure("주소를 찾을 수 없습니다.")
                    }
                } else {
                    onFailure("네이버 지도 API 호출 실패 - 응답 코드: ${conn.responseCode}")
                }
                conn.disconnect()
            } catch (e: Exception) {
                onFailure("네트워크 오류: ${e.message}")
            }
        }
    }

    fun reverseGeocode(
        context: Context,
        placeName: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d(TAG, "장소 이름으로 주소 검색 시작 - 장소명: $placeName")

        // 장소 이름을 API에서 지원하는 포맷으로 변환 (여기서는 그대로 사용)
        val query = URLEncoder.encode(placeName, StandardCharsets.UTF_8.toString())

        // API 호출 URL
        val apiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=$query"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // HTTP 연결 설정
                val url = URL(apiUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_MAPS_CLIENT_ID)
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_MAPS_CLIENT_SECRET)

                // 응답 코드 확인
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    // 응답 데이터 읽기
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // JSON 파싱
                    val jsonObject = JSONObject(response.toString())
                    val addresses = jsonObject.getJSONArray("addresses")
                    if (addresses.length() > 0) {
                        val firstAddress = addresses.getJSONObject(0)
                        val roadAddress = firstAddress.getString("roadAddress")

                        Log.d(TAG, "주소 정보 파싱 성공 - 도로명 주소: $roadAddress")

                        // 성공 콜백 호출
                        onSuccess(roadAddress)
                    } else {
                        onFailure("주소를 찾을 수 없습니다.")
                    }
                } else {
                    onFailure("네이버 지도 API 호출 실패 - 응답 코드: ${conn.responseCode}")
                }
                conn.disconnect()
            } catch (e: Exception) {
                onFailure("네트워크 오류: ${e.message}")
            }
        }
    }
}
