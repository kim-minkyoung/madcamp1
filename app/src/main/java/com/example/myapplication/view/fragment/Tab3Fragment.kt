package com.example.myapplication.view.fragment

import android.Manifest
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTab3Binding
import com.example.myapplication.model.viewModel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.LocationTrackingMode
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import java.util.*

class Tab3Fragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private var _binding: FragmentTab3Binding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var placesClient: PlacesClient? = null
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private val marker = Marker()
    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTab3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 위치 권한 요청
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        // FusedLocationSource 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // MapView 초기화 및 설정
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // BottomSheetBehavior 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)

        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "YOUR_API_KEY")  // 실제 API 키로 교체
        }
        placesClient = Places.createClient(requireContext())

        // AutoCompleteTextView 설정: 주소 자동 검색
        autoCompleteTextView = binding.address as AutoCompleteTextView
        autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.setAdapter(autoCompleteAdapter)

        autoCompleteTextView.setOnKeyListener { _, _, _ ->
            val query = autoCompleteTextView.text.toString()
            if (query.isNotEmpty()) {
                fetchAutocompletePredictions(query)
            }
            false
        }

        // Set search button click listener
        binding.submit.setOnClickListener {
            val address = binding.address.text.toString()
            if (address.isNotEmpty()) {
                viewModel.searchAddress(address)
            } else {
                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.addressData.observe(viewLifecycleOwner, { data ->
            val (roadAddress, latitude, longitude) = data
            updateBottomSheet(roadAddress, latitude, longitude)
            moveCameraToLocation(latitude, longitude)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun fetchAutocompletePredictions(query: String) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(token)
            .build()

        placesClient?.findAutocompletePredictions(request)?.addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            val suggestionList = predictions.map { it.getFullText(null).toString() }
            autoCompleteAdapter.clear()
            autoCompleteAdapter.addAll(suggestionList)
            autoCompleteAdapter.notifyDataSetChanged()
        }?.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }

    // 위치 권한 요청 함수
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            }
            else -> {
                // No location access granted
                Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    private fun updateBottomSheet(address: String, latitude: Double, longitude: Double) {
        binding.persistentBottomSheet.findViewById<TextView>(R.id.address_text_view).text = address
        binding.persistentBottomSheet.findViewById<TextView>(R.id.latitude_text_view).text =
            "위도: $latitude"
        binding.persistentBottomSheet.findViewById<TextView>(R.id.longitude_text_view).text =
            "경도: $longitude"
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun moveCameraToLocation(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        naverMap.moveCamera(cameraUpdate)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // naverMap locationSource에 FusedLocationSource 적용
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 지도가 클릭 되면 onMapClick() 콜백 메서드가 호출 되며, 파라미터로 클릭된 지점의 화면 좌표와 지도 좌표가 전달 된다.
        naverMap.setOnMapClickListener { point, coord ->
            marker(coord.latitude, coord.longitude)
        }
    }

    // 클릭 된 지점의 좌표에 마커를 추가하는 함수
    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        getAddress(latitude, longitude)
    }

    // 클릭 된 지점의 좌표에 대한 주소를 구하는 함수
    private fun getAddress(latitude: Double, longitude: Double) {
        // Geocoder 선언
        val geocoder = Geocoder(requireContext(), Locale.KOREAN)

        // 안드로이드 API 레벨이 33 이상인 경우
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latitude, longitude, 1
            ) { addresses ->
                if (addresses.isNotEmpty()) {
                    // 반환 값에서 전체 주소만 사용한다.
                    toast(addresses[0].getAddressLine(0))
                }
            }
        } else { // API 레벨이 33 미만인 경우
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    toast(addresses[0].getAddressLine(0))
                }
            }
        }
    }

    private fun toast(text: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSavePlaceDialog(placeName: String?, address: String?) {
        val dialog = SavePlaceDialogFragment.newInstance(placeName, address)
        dialog.show(childFragmentManager, "SavePlaceDialog")
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
